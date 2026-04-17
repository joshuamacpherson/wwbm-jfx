package ass2.ass2_jfx.controller;

import ass2.ass2_jfx.model.networkProtocol;
import ass2.ass2_jfx.view.menuBarHelper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import java.io.IOException;
import java.util.*;

/**
 * Controller for the Multiplayer Play screen.
 * Handles all gameplay logic during a multiplayer session including:
 * - Receiving and displaying questions from the server
 * - Managing the countdown timer for each question
 * - Submitting player answers to the server
 * - Updating the scoreboard as results arrive
 * - Displaying the live chat feed
 * - Managing the three lifelines: Superposition, Entanglement, and Interference
 * - Handling disconnection and returning to the main menu
 * This controller registers itself as the message listener on the gameClient
 * and processes all incoming server messages on the JavaFX Application Thread.
 *
 * @author Shane O'Connell
 * @author Joshua MacPherson
 * @version Java 21
 */
public class multiplayerPlayController {

    /** Label displaying the current question text or status message. */
    @FXML private Label questionLabel;
    /** Label displaying the remaining time for the current question. */
    @FXML private Label timerLabel;
    /** Label displaying the player's current level. */
    @FXML private Label levelLabel;
    /** Label displaying the player's name. */
    @FXML private Label playerNameLabel;
    /** Label for the money display heading. */
    @FXML private Label playerMoneyLabel;
    /** Label displaying the player's current money total. */
    @FXML private Label playerMoneyAmountLabel;
    /** Answer button A. */
    @FXML private Button ansA;
    /** Answer button B. */
    @FXML private Button ansB;
    /** Answer button C. */
    @FXML private Button ansC;
    /** Answer button D. */
    @FXML private Button ansD;
    /** Button to activate the Superposition (50/50) lifeline. */
    @FXML private Button fiftyFifty;
    /** Button to activate the Entanglement (phone a friend) lifeline. */
    @FXML private Button phoneAFriend;
    /** Button to activate the Interference (ask the audience) lifeline. */
    @FXML private Button askTheAudience;
    /** Button to send a chat message. */
    @FXML private Button sendChatBtn;
    /** Button to disconnect from the game and return to the menu. */
    @FXML private Button disconnectBtn;
    /** List view displaying the live scoreboard sorted by money earned. */
    @FXML private ListView<String> scoreboardList;
    /** List view displaying the live chat feed. */
    @FXML private ListView<String> chatList;
    /** Text field for typing chat messages. */
    @FXML private TextField chatInput;
    /** Array of answer buttons for easy iteration. */
    private Button[] answerButtons;
    /** The game client used to communicate with the server. */
    private gameClient client;
    /** The name of the current player. */
    private String playerName;
    /** The player's current money total. */
    private int playerMoney = 0;
    /** The player's current question level. */
    private int currentLevel = 1;
    /** The countdown timer for each question. */
    private Timeline timer;
    /** Remaining seconds on the current countdown. */
    private int timeLeft;
    /** Whether the Superposition lifeline has been used this session. */
    private boolean superpositionUsed = false;
    /** Whether the Entanglement lifeline has been used this session. */
    private boolean entanglementUsed = false;
    /** Whether the Interference lifeline has been used this session. */
    private boolean interferenceUsed = false;
    /** The answer texts for the current question. */
    private String[] currentAnswers;
    /** Maps player names to their total money earned, used for the scoreboard. */
    private final Map<String, Integer> scoreboard = new LinkedHashMap<>();
    /** Prize money values for each of the 15 question tiers. */
    private final int[] prices = {
            100, 200, 300, 500, 1000,
            2000, 4000, 8000, 16000, 32000,
            64000, 125000, 250000, 500000, 1000000
    };
    /** Index for lifelines */
    private int correctIndex = -1;

    /**
     * Initializes the play screen.
     * Retrieves the client and player name from the shared multiplayerState,
     * sets up the message listener, and waits for the first question.
     */
    @FXML
    private void initialize() {
        answerButtons = new Button[]{ansA, ansB, ansC, ansD};

        multiplayerState state = multiplayerState.getInstance();
        client = state.getClient();
        playerName = state.getPlayerName();

        playerNameLabel.setText(playerName);
        playerMoneyAmountLabel.setText("$0");
        levelLabel.setText("Level: 1");

        client.setOnMessageReceived(this::handleServerMessage);

        setAnswerButtonsDisabled(true);
        questionLabel.setText("Waiting for first question...");
    }

    /**
     * Handles an incoming message from the server.
     * Routes the message based on its type: QUESTION, RESULT, CHAT, START, DISCONNECT, or ERROR.
     * @param message the raw message string received from the server
     */
    private void handleServerMessage(String message) {
        String[] parts = networkProtocol.parse(message);
        if (parts.length >= 7) {
            correctIndex = Integer.parseInt(parts[6]);
        }

        switch (parts[0]) {

            case networkProtocol.QUESTION -> {
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
                if (parts.length >= 4) {
                    String id = parts[1];
                    boolean correct = Boolean.parseBoolean(parts[2]);
                    int earned = Integer.parseInt(parts[3]);

                    scoreboard.merge(id, earned, Integer::sum);
                    refreshScoreboard();

                    if (id.equals(playerName)) {
                        if (correct) {
                            playerMoney += earned;
                            playerMoneyAmountLabel.setText("$" + playerMoney);
                            currentLevel++;
                            levelLabel.setText("Level: " + currentLevel);
                        }
                    }

                    if (parts[2].equals("GAMEOVER")) {
                        questionLabel.setText("Game Over!");
                        setAnswerButtonsDisabled(true);
                        if (timer != null) timer.stop();
                    }
                }
            }

            case networkProtocol.CHAT -> {
                if (parts.length >= 3) {
                    chatList.getItems().add(parts[1] + ": " + parts[2]);
                    chatList.scrollTo(chatList.getItems().size() - 1);
                }
            }

            case networkProtocol.START -> {
                chatList.getItems().add("Game started!");
            }

            case networkProtocol.DISCONNECT -> {
                chatList.getItems().add(
                        parts.length >= 2
                                ? parts[1] + " disconnected."
                                : "Someone disconnected.");
            }

            case networkProtocol.ERROR -> {
                if (parts.length >= 2) {
                    chatList.getItems().add("ERROR: " + parts[1]);
                }
            }
        }
    }

    /**
     * Handles an answer button click.
     * Stops the timer, determines which button was clicked, sends the answer
     * index to the server, and waits for results.
     * @param event the button click event
     */
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

    /**
     * Handles the Send Chat button click.
     * Sends the typed message to the server and clears the input field.
     */
    @FXML
    private void onSendChat() {
        String msg = chatInput.getText().trim();
        if (!msg.isEmpty()) {
            client.sendChat(playerName, msg);
            chatInput.clear();
        }
    }

    /**
     * Handles the Disconnect button click.
     * Stops the timer, disconnects the client, stops the server if hosting,
     * and returns to the main menu.
     * @param event the button click event
     */
    @FXML
    private void onDisconnect(ActionEvent event) {
        if (timer != null) timer.stop();
        if (client != null) client.disconnect(playerName);

        gameServer server = multiplayerState.getInstance().getServer();
        if (server != null) server.stop();

        try {
            sceneController.getInstance().switchToMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Activates the Superposition lifeline.
     * Randomly disables two of the four answer buttons.
     * Can only be used once per game.
     */
    @FXML
    private void useSuperposition() {
        if (superpositionUsed || currentAnswers == null) return;
        ArrayList<Integer> wrong = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != correctIndex) wrong.add(i);
        }
        Collections.shuffle(wrong);
        answerButtons[wrong.get(0)].setDisable(true);
        answerButtons[wrong.get(1)].setDisable(true);
        superpositionUsed = true;
        fiftyFifty.setDisable(true);
    }

    /**
     * Activates the Entanglement lifeline.
     * Highlights a random answer button green as a friend's suggestion.
     * Can only be used once per game.
     */
    @FXML
    private void useEntanglement() {
        if (entanglementUsed) return;
        answerButtons[correctIndex].setStyle("-fx-background-color: green;");
        entanglementUsed = true;
        phoneAFriend.setDisable(true);
    }

    /**
     * Activates the Interference lifeline.
     * Highlights a random answer button green as the audience's suggestion.
     * Can only be used once per game.
     */
    @FXML
    private void useInterference() {
        if (interferenceUsed) return;
        int idx = new Random().nextDouble() < 0.5 ? correctIndex : new Random().nextInt(4);
        answerButtons[idx].setStyle("-fx-background-color: green;");
        interferenceUsed = true;
        askTheAudience.setDisable(true);
    }
    /**
     * Starts a 60-second countdown timer for the current question.
     * When time runs out, disables answer buttons and sends a timeout answer of -1.
     */
    private void startTimer() {
        if (timer != null) timer.stop();
        timeLeft = 60;
        timerLabel.setText(String.valueOf(timeLeft));

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));
            if (timeLeft <= 0) {
                timer.stop();
                setAnswerButtonsDisabled(true);
                questionLabel.setText("Time's up!");
                client.sendAnswer(playerName, -1);
            }
        }));
        timer.setCycleCount(60);
        timer.play();
    }

    /**
     * Enables or disables all four answer buttons at once.
     * @param disabled true to disable all buttons, false to enable them
     */
    private void setAnswerButtonsDisabled(boolean disabled) {
        for (Button btn : answerButtons) {
            btn.setDisable(disabled);
        }
    }

    /**
     * Clears and rebuilds the scoreboard list view, sorted by money earned descending.
     */
    private void refreshScoreboard() {
        scoreboardList.getItems().clear();
        scoreboard.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> scoreboardList.getItems().add(
                        e.getKey() + " — $" + e.getValue()));
    }

    /** Exits the application. */
    @FXML private void onExitClick() { menuBarHelper.exit(); }
    /** Applies the dark theme. */
    @FXML private void onDarkClick() { menuBarHelper.setDark(); }
    /** Applies the light theme. */
    @FXML private void onLightClick() { menuBarHelper.setLight(); }
    /** Switches the application language to English. */
    @FXML private void onENClick() { menuBarHelper.setEnglish(); }
    /** Switches the application language to French. */
    @FXML private void onFRClick() { menuBarHelper.setFrench(); }
}