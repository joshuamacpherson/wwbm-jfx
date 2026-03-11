package ass2.ass2_jfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.io.IOException;
import java.util.*;

/**
 * Controller for the Play Mode screen.
 *
 * Handles gameplay logic including:
 * - Displaying questions and answers
 * - Managing the countdown timer
 * - Tracking player money
 * - Updating prize tiers
 * - Handling answer selection
 * - Restarting the game
 * - Navigating back to the main menu
 */
public class playController {
    @FXML private Button next, restart, mainMenu, A, B, C, D, fiftyFifty, phoneAFriend, askTheAudience;
    @FXML private Label t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15,
            messageLabel, playerMoneyAmountLabel, playerMoneyLabel, timerLabel, playerNameLabel;
    @FXML private TextArea debugArea;
    @FXML private ImageView playerImageView;

    private final languageController lc = languageController.getInstance();
    private Button[] answerButtons;
    private Label[] tiers;
    private Timeline timer;
    private int timeLeft;
    private LinkedHashMap<Label, Integer> tierMap;
    private int playerMoney = 0;

    private final int[] prices = {
            100, 200, 300, 500, 1000,
            2000, 4000, 8000, 16000, 32000,
            64000, 125000, 250000, 500000, 1000000
    };

    private int currentTier = 0;
    private int lastRand = 1;
    private boolean superpositionUsed = false;
    private boolean entanglementUsed = false;
    private boolean interferenceUsed = false;
    private int entangledIndex = -1;

    ArrayList<Question> questions = dataStore.getInstance().getQuestions();

    /**
     * Initializes the Play screen.
     * Sets localization, initializes prize tiers,
     * loads the first question, and highlights the starting tier.
     */
    @FXML
    private void initialize() {

        if (appMain.DEBUG) {
            debugArea.setVisible(true);
        } else {
            debugArea.setVisible(false);
        }
        debugArea.appendText("Debug Area: Initialized playController\n");
        answerButtons = new Button[]{A, B, C, D};
        tiers = new Label[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15};
        updateLanguage();
        tierMap = new LinkedHashMap<>();
        for (int i = 0; i < tiers.length; i++) {
            tierMap.put(tiers[i], prices[i]);
            tiers[i].setText(String.format("$%,d", prices[i]));
        }
        loadQuestion(questions.get(0));
        tiers[currentTier].getStyleClass().add("currentTier");

        Player currentPlayer = dataStore.getInstance().getCurrentPlayer();
        playerNameLabel.setText(dataStore.getInstance().getCurrentPlayer().getName());

        if (currentPlayer != null) {
            playerMoney = currentPlayer.getPlayerMoney();
            playerMoneyAmountLabel.setText("$" + playerMoney);
        }
    }

    /**
     * Starts or restarts the 60-second countdown timer.
     * Disables answer buttons when time runs out.
     */
    private void startTimer() {
        languageController lc = languageController.getInstance();
        if (timer != null) timer.stop();

        timeLeft = 60;
        timerLabel.setText(String.valueOf(timeLeft));

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));

            if (timeLeft <= 0) {
                timer.stop();
                messageLabel.setText(lc.getString("timeUp"));
                setAnswerButtonsVisible(false);
                mainMenu.setVisible(true);
                restart.setVisible(true);
            }
        }));

        timer.setCycleCount(60);
        timer.play();
    }

    /**
     * Shows or hides all answer buttons at once.
     * @param visible true to display the buttons, false to hide them
     */
    private void setAnswerButtonsVisible(boolean visible) {
        for (Button btn : answerButtons) {
            btn.setVisible(visible);
        }
    }

    /**
     * Handles answer button clicks.
     * Determines correctness, updates money and tier,
     * and controls UI visibility.
     * @param event the button click event
     */
    @FXML
    private void onAnswerClick(ActionEvent event) {
        if (timer != null) timer.stop();
        Button clickedButton = (Button) event.getSource();
        String clickedAnswer = clickedButton.getText();
        debugArea.appendText("Clicked Answer: " + clickedAnswer + "\n");
        if (questions.get(currentTier).isCorrect(clickedAnswer)) {
            if (currentTier == tiers.length - 1) {
                messageLabel.setText(lc.getString("win"));
                playerMoney += tierMap.get(tiers[currentTier]);Player p = dataStore.getInstance().getCurrentPlayer();
                p.addMoneyToPlayer(tierMap.get(tiers[currentTier]));
                playerMoney = p.getPlayerMoney();
                playerMoneyAmountLabel.setText("$" + playerMoney);
                setAnswerButtonsVisible(false);
                restart.setVisible(true);
                mainMenu.setVisible(true);
                return;
            }
            messageLabel.setText(lc.getString("correct"));
            playerMoney += tierMap.get(tiers[currentTier]);
            debugArea.appendText("Added " + tierMap.get(tiers[currentTier]) + " to player currency\n");
            playerMoneyAmountLabel.setText("$" + playerMoney);
            next.setVisible(true);
            currentTier++;
        } else {
            messageLabel.setText(lc.getString("incorrect") + " " + questions.get(currentTier).getCorrectAnswer());
            restart.setVisible(true);
            mainMenu.setVisible(true);
        }
        setAnswerButtonsVisible(false);
    }

    /** Loads the next question and advances the tier highlight. */
    @FXML
    private void onNextClick() {
        loadQuestion(questions.get(currentTier));
        setAnswerButtonsVisible(true);
        next.setVisible(false);
        tiers[currentTier].getStyleClass().add("currentTier");
        tiers[currentTier - 1].getStyleClass().remove("currentTier");
    }

    /**
     * Restarts the game by resetting money, tier position,
     * and reloading the first question.
     */
    @FXML
    private void onRestartClick() {
        setAnswerButtonsVisible(true);
        restart.setVisible(false);
        mainMenu.setVisible(false);
        playerMoney = 0;
        playerMoneyAmountLabel.setText("$" + playerMoney);
        tiers[currentTier].getStyleClass().remove("currentTier");
        currentTier = 0;
        loadQuestion(questions.get(0));
        tiers[currentTier].getStyleClass().add("currentTier");

        // reset player
        Player p = dataStore.getInstance().getCurrentPlayer();
        p.resetPlayerMoney();
        p.resetPlayerTier();

        // reset lifelines
        superpositionUsed = false;
        entanglementUsed = false;
        interferenceUsed = false;
        entangledIndex = -1;
        fiftyFifty.setDisable(false);
        phoneAFriend.setDisable(false);
        askTheAudience.setDisable(false);
    }

    /**
     * Returns the user to the main menu screen.
     * @param event the button click event
     */
    @FXML
    private void onMainMenuClick(ActionEvent event) {
        try {
            sceneController.getInstance().switchToMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a question into the UI and starts the timer.
     * @param q the Question to display
     */
    private void loadQuestion(Question q) {
        messageLabel.setText(q.getQuestionText());
        String[] answers = q.getAnswers();

        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(answers[i]);
            answerButtons[i].setDisable(false);
            answerButtons[i].setStyle("");
        }
        entangledIndex = -1;
        startTimer();
    }

    /**
     * Removes two incorrect answers at random.
     * Disables the eliminated buttons and locks the lifeline.
     */
    @FXML
    private void useSuperposition() {
        if (superpositionUsed) {
            return;
        }

        Question q = questions.get(currentTier);
        ArrayList<Integer> wrong = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            if (!q.isCorrect(i)) {
                wrong.add(i);
            }
        }

        Collections.shuffle(wrong);
        answerButtons[wrong.get(0)].setDisable(true);
        answerButtons[wrong.get(1)].setDisable(true);
        superpositionUsed = true;
        fiftyFifty.setDisable(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Superposition Activated!");
        alert.setHeaderText(null);
        alert.setContentText("Two incorrect answers have been removed!");
        alert.showAndWait();
    }

    /**
     * Highlights the correct answer and guarantees the player
     * will be marked correct on their next selection.
     */
    @FXML
    private void useEntanglement() {
        if (entanglementUsed){
            return;
        }

        Question q = questions.get(currentTier);
        for (int i = 0; i < 4; i++) {
            String answerText = answerButtons[i].getText();
            if (q.isCorrect(answerText)) {
                entangledIndex = i;
                answerButtons[i].setStyle("-fx-background-color: green;");
                break;
            }
        }

        entanglementUsed = true;
        phoneAFriend.setDisable(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Entanglement Activated!");
        alert.setHeaderText(null);
        alert.setContentText("Your friend thinks this is the answer!.");
        alert.showAndWait();
    }

    /**
     * Highlights a suggested answer. If Entanglement was used,
     * Interference will reinforce the same correct answer.
     */
    @FXML
    private void useInterference() {
        if (interferenceUsed) {
            return;
        }

        Random rand = new Random();
        Question q = questions.get(currentTier);
        int index = 0;

        if (rand.nextDouble() < 0.5) {
            for (int i = 0; i < 4; i++) {
                if (q.isCorrect(i)) {
                    index = i;
                    answerButtons[index].setStyle("-fx-background-color: green;");
                    break;
                }
            }
        } else {
            // random answer
            index = rand.nextInt(4);
            answerButtons[index].setStyle("-fx-background-color: green;");
        }
        interferenceUsed = true;
        askTheAudience.setDisable(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Interference Activated!");
        alert.setHeaderText(null);
        alert.setContentText("The audience thinks this is the answer!.");
        alert.showAndWait();

    }

    /** Need this to update UI or game-state is lost. */
    public void updateLanguage() {
        languageController lc = languageController.getInstance();
        playerMoneyLabel.setText(lc.getString("playerMoney"));
        next.setText(lc.getString("nextQuestion"));
        restart.setText(lc.getString("restart"));
        mainMenu.setText(lc.getString("mainMenu"));
    }

    /** Exits the application. */
    @FXML private void onExitClick()  { menuBarHelper.exit(); }
    /** Switches to dark theme. */
    @FXML private void onDarkClick()  { menuBarHelper.setDark(); }
    /** Switches to light theme. */
    @FXML private void onLightClick() { menuBarHelper.setLight(); }
    /** Sets language to English and refreshes UI text. */
    @FXML private void onENClick() {menuBarHelper.setEnglish();updateLanguage();}
    /** Sets language to French and refreshes UI text. */
    @FXML private void onFRClick() {menuBarHelper.setFrench();updateLanguage();}
}