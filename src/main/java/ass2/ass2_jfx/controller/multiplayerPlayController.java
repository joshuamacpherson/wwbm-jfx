package ass2.ass2_jfx.controller;

import ass2.ass2_jfx.model.dataStore;
import ass2.ass2_jfx.view.menuBarHelper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class multiplayerPlayController {

    @FXML private Label questionLabel, timerLabel, levelLabel;
    @FXML private Label playerNameLabel, playerMoneyLabel,
            playerMoneyAmountLabel;
    @FXML private Button ansA, ansB, ansC, ansD;
    @FXML private Button fiftyFifty, phoneAFriend, askTheAudience;
    @FXML private Button sendChatBtn, disconnectBtn;
    @FXML private ListView<String> scoreboardList;
    @FXML private ListView<String> chatList;
    @FXML private TextField chatInput;

    private Button[] answerButtons;
    private gameClient client;
    private String playerName;
    private int playerMoney = 0;
    private int currentLevel = 1;
    private Timeline timer;
    private int timeLeft;

    private boolean superpositionUsed = false;
    private boolean entanglementUsed = false;
    private boolean interferenceUsed = false;

    private String[] currentAnswers;
    private final Map<String, Integer> scoreboard =
            new LinkedHashMap<>();

    private final int[] prices = {
            100, 200, 300, 500, 1000,
            2000, 4000, 8000, 16000, 32000,
            64000, 125000, 250000, 500000, 1000000
    };

    @FXML
    private void initialize() {
        answerButtons = new Button[]{ansA, ansB, ansC, ansD};

        multiplayerState state = multiplayerState.getInstance();
        client = state.getClient();
        playerName = state.getPlayerName();

        playerNameLabel.setText(playerName);
        playerMoneyAmountLabel.setText("$0");
        levelLabel.setText("Level: 1");

        // listen for server messages
        client.setOnMessageReceived(this::handleServerMessage);

        setAnswerButtonsDisabled(true);
        questionLabel.setText("Waiting for first question...");
    }

    private void handleServerMessage(String message) {
        String[] parts = networkProtocol.parse(message);
        if (parts.length == 0) return;

        switch (parts[0]) {

            case networkProtocol.QUESTION -> {
                // QUESTION|text|A|B|C|D
                if (parts.length >= 6) {
                    questionLabel.setText(parts[1]);
                    currentAnswers = new String[]{
                            parts[2], parts[3], parts[4], parts[5]
                    };
                    for (int i = 0; i < 4; i++) {
                        answerButtons[i].setText(currentAnswers[i]);
                        answerButtons[i].setDisable(false);
                        answerButtons[i].setStyle("");
                    }
                    setAnswerButtonsDisabled(false);
                    startTimer();
                }
            }

            case networkProtocol.RESULT -> {
                // RESULT|playerId|correct|moneyEarned
                if (parts.length >= 4) {
                    String id = parts[1];
                    boolean correct =
                            Boolean.parseBoolean(parts[2]);
                    int earned = Integer.parseInt(parts[3]);

                    // update scoreboard for everyone
                    scoreboard.merge(id, earned, Integer::sum);
                    refreshScoreboard();

                    // update own money display
                    if (id.equals(playerName)) {
                        if (correct) {
                            playerMoney += earned;
                            playerMoneyAmountLabel.setText(
                                    "$" + playerMoney);
                            currentLevel++;
                            levelLabel.setText(
                                    "Level: " + currentLevel);
                        }
                    }

                    // game over
                    if (parts[2].equals("GAMEOVER")) {
                        questionLabel.setText("Game Over!");
                        setAnswerButtonsDisabled(true);
                        if (timer != null) timer.stop();
                    }
                }
            }

            case networkProtocol.CHAT -> {
                // CHAT|playerName|msg
                if (parts.length >= 3) {
                    chatList.getItems().add(
                            parts[1] + ": " + parts[2]);
                    chatList.scrollTo(
                            chatList.getItems().size() - 1);
                }
            }

            case networkProtocol.START -> {
                chatList.getItems().add(
                        "Game started!");
            }

            case networkProtocol.DISCONNECT -> {
                chatList.getItems().add(
                        parts.length >= 2
                                ? parts[1] + " disconnected."
                                : "Someone disconnected.");
            }

            case networkProtocol.ERROR -> {
                if (parts.length >= 2) {
                    chatList.getItems().add(
                            "ERROR: " + parts[1]);
                }
            }
        }
    }

    @FXML
    private void onAnswerClick(ActionEvent event) {
        if (timer != null) timer.stop();

        Button clicked = (Button) event.getSource();
        int index = -1;
        for (int i = 0; i < answerButtons.length; i++) {
            if (answerButtons[i] == clicked) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            client.sendAnswer(playerName, index);
            setAnswerButtonsDisabled(true);
            questionLabel.setText("Waiting for other players...");
        }
    }

    @FXML
    private void onSendChat() {
        String msg = chatInput.getText().trim();
        if (!msg.isEmpty()) {
            client.sendChat(playerName, msg);
            chatInput.clear();
        }
    }

    @FXML
    private void onDisconnect(ActionEvent event) {
        if (timer != null) timer.stop();
        if (client != null) client.disconnect(playerName);

        gameServer server = multiplayerState.getInstance()
                .getServer();
        if (server != null) server.stop();

        try {
            sceneController.getInstance().switchToMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Lifelines ──

    @FXML
    private void useSuperposition() {
        if (superpositionUsed || currentAnswers == null) return;

        // client-side: disable 2 random non-correct buttons
        // we don't know correct answer, so just disable 2 random
        List<Integer> indices = new ArrayList<>(
                Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(indices);

        int disabled = 0;
        for (int i : indices) {
            if (disabled >= 2) break;
            answerButtons[i].setDisable(true);
            disabled++;
        }

        superpositionUsed = true;
        fiftyFifty.setDisable(true);
    }

    @FXML
    private void useEntanglement() {
        if (entanglementUsed) return;
        // highlight a random answer as "friend's suggestion"
        int idx = new Random().nextInt(4);
        answerButtons[idx].setStyle(
                "-fx-background-color: green;");
        entanglementUsed = true;
        phoneAFriend.setDisable(true);
    }

    @FXML
    private void useInterference() {
        if (interferenceUsed) return;
        int idx = new Random().nextInt(4);
        answerButtons[idx].setStyle(
                "-fx-background-color: green;");
        interferenceUsed = true;
        askTheAudience.setDisable(true);
    }

    // ── Helpers ──

    private void startTimer() {
        if (timer != null) timer.stop();
        timeLeft = 60;
        timerLabel.setText(String.valueOf(timeLeft));

        timer = new Timeline(new KeyFrame(
                Duration.seconds(1), e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));
            if (timeLeft <= 0) {
                timer.stop();
                setAnswerButtonsDisabled(true);
                questionLabel.setText("Time's up!");
                // send a timeout answer (-1)
                client.sendAnswer(playerName, -1);
            }
        }));
        timer.setCycleCount(60);
        timer.play();
    }

    private void setAnswerButtonsDisabled(boolean disabled) {
        for (Button btn : answerButtons) {
            btn.setDisable(disabled);
        }
    }

    private void refreshScoreboard() {
        scoreboardList.getItems().clear();
        scoreboard.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue()
                        .reversed())
                .forEach(e -> scoreboardList.getItems().add(
                        e.getKey() + " — $" + e.getValue()));
    }

    @FXML private void onExitClick() { menuBarHelper.exit(); }
    @FXML private void onDarkClick() { menuBarHelper.setDark(); }
    @FXML private void onLightClick() { menuBarHelper.setLight(); }
    @FXML private void onENClick() { menuBarHelper.setEnglish(); }
    @FXML private void onFRClick() { menuBarHelper.setFrench(); }
}