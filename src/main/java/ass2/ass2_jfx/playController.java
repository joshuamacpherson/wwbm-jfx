package ass2.ass2_jfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

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

    languageController lc = languageController.getInstance();

    @FXML
    private Label messageLabel;
    @FXML
    private Button A;
    @FXML
    private Button B;
    @FXML
    private Button C;
    @FXML
    private Button D;
    @FXML
    private Button next;
    @FXML
    private Button restart;
    @FXML
    private Button mainMenu;

    @FXML
    private Label tier1;
    @FXML
    private Label tier2;
    @FXML
    private Label tier3;
    @FXML
    private Label tier4;
    @FXML
    private Label tier5;
    @FXML
    private Label tier6;
    @FXML
    private Label tier7;
    @FXML
    private Label tier8;
    @FXML
    private Label tier9;
    @FXML
    private Label tier10;
    @FXML
    private Label tier11;
    @FXML
    private Label tier12;
    @FXML
    private Label tier13;
    @FXML
    private Label tier14;
    @FXML
    private Label tier15;

    @FXML
    private Label playerMoneyLabel;
    @FXML
    private Label playerMoneyAmountLabel;
    @FXML
    private Label timerLabel;

    /**
     * Timer used for countdown functionality.
     */
    private Timeline timer;

    /**
     * Remaining time for the current question.
     */
    private int timeLeft;

    /**
     * Maps tier labels to their corresponding prize values.
     */
    private LinkedHashMap<Label, Integer> tierMap;

    /**
     * Tracks the player's total money earned.
     */
    int playerMoney = 0;

    /**
     * Array of tier labels for easier iteration.
     */
    private Label[] tiers;

    ArrayList<Question> questions = new ArrayList<Question>() {{
        add(new Question("Who is the best superhero?", new String[]{"Spiderman", "Superman", "Batman", "Wonder Woman"}, 0));
        add(new Question("What is the capital of France?", new String[]{"London", "Berlin", "Paris", "Rome"}, 2));
        add(new Question("Which planet is known as the Red Planet?", new String[]{"Earth", "Mars", "Jupiter", "Venus"}, 1));
        add(new Question("What is 2 + 2?", new String[]{"3", "4", "5", "6"}, 1));
        add(new Question("Who wrote Hamlet?", new String[]{"Shakespeare", "Dickens", "Austen", "Tolkien"}, 0));
        add(new Question("What is the largest ocean?", new String[]{"Atlantic", "Indian", "Arctic", "Pacific"}, 3));
        add(new Question("What color do you get by mixing red and blue?", new String[]{"Green", "Purple", "Orange", "Yellow"}, 1));
        add(new Question("Which animal is known as man's best friend?", new String[]{"Cat", "Dog", "Horse", "Rabbit"}, 1));
        add(new Question("What is the boiling point of water?", new String[]{"90°C", "100°C", "110°C", "120°C"}, 1));
        add(new Question("Who painted the Mona Lisa?", new String[]{"Van Gogh", "Da Vinci", "Picasso", "Rembrandt"}, 1));
        add(new Question("What is the largest continent?", new String[]{"Africa", "Asia", "Europe", "Australia"}, 1));
        add(new Question("What is the hardest natural substance?", new String[]{"Gold", "Iron", "Diamond", "Silver"}, 2));
        add(new Question("Which gas do plants absorb?", new String[]{"Oxygen", "Carbon Dioxide", "Nitrogen", "Hydrogen"}, 1));
        add(new Question("What is the main ingredient in bread?", new String[]{"Rice", "Wheat", "Corn", "Oats"}, 1));
        add(new Question("Which country is famous for sushi?", new String[]{"China", "Japan", "Thailand", "Vietnam"}, 1));
    }};

/**
     * Current question being displayed.
     */
    Question question = questions.get(0);

    /**
     * Tracks the player's current prize tier index.
     */
    int currentTier = 0;

    /**
     * Initializes the Play screen.
     * Sets localization, initializes prize tiers,
     * loads the first question, and highlights the starting tier.
     */
    @FXML
    private void initialize() {

        updateLanguage();
        tiers = new Label[]{
                tier1, tier2, tier3, tier4, tier5,
                tier6, tier7, tier8, tier9, tier10,
                tier11, tier12, tier13, tier14, tier15
        };

        int[] prices = {
                100, 200, 300, 500, 1000,
                2000, 4000, 8000, 16000, 32000,
                64000, 125000, 250000, 500000, 1000000
        };

        tierMap = new LinkedHashMap<>();

        for (int i = 0; i < tiers.length; i++) {
            tierMap.put(tiers[i], prices[i]);
            tiers[i].setText(String.format("$%,d", prices[i]));
        }

        loadQuestion(question);
        tiers[currentTier].getStyleClass().add("currentTier");
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
                A.setVisible(false);
                B.setVisible(false);
                C.setVisible(false);
                D.setVisible(false);
                mainMenu.setVisible(true);
                restart.setVisible(true);
            }
        }));

        timer.setCycleCount(60);
        timer.play();

        A.setDisable(false);
        B.setDisable(false);
        C.setDisable(false);
        D.setDisable(false);
    }

    /**
     * Handles answer button clicks.
     * Determines correctness, updates money and tier,
     * and controls UI visibility.
     *
     * @param event the button click event
     */
    @FXML
    private void onAnswerClick(ActionEvent event) {

        if (timer != null) timer.stop();

        Button clickedButton = (Button) event.getSource();
        String clickedAnswer = clickedButton.getText();

        if (questions.get(currentTier).isCorrect(clickedAnswer)) {

            if (currentTier == tiers.length - 1) {
                messageLabel.setText(lc.getString("win"));
                playerMoney += tierMap.get(tiers[currentTier]);
                playerMoneyAmountLabel.setText("$" + playerMoney);
                A.setVisible(false);
                B.setVisible(false);
                C.setVisible(false);
                D.setVisible(false);
                restart.setVisible(true);
                mainMenu.setVisible(true);
                return;
            }

            messageLabel.setText(lc.getString("correct"));
            playerMoney += tierMap.get(tiers[currentTier]);
            playerMoneyAmountLabel.setText("$" + playerMoney);
            next.setVisible(true);
            currentTier++;

        } else {
            messageLabel.setText(lc.getString("incorrect") + " " + questions.get(currentTier).getCorrectAnswer());
            restart.setVisible(true);
            mainMenu.setVisible(true);
        }

        A.setVisible(false);
        B.setVisible(false);
        C.setVisible(false);
        D.setVisible(false);
    }

    /**
     * Loads the next question and advances the tier highlight.
     */
    @FXML
    private void onNextClick() {

        loadQuestion(questions.get(currentTier));

        A.setVisible(true);
        B.setVisible(true);
        C.setVisible(true);
        D.setVisible(true);

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

        A.setVisible(true);
        B.setVisible(true);
        C.setVisible(true);
        D.setVisible(true);

        restart.setVisible(false);
        mainMenu.setVisible(false);

        playerMoney = 0;
        playerMoneyAmountLabel.setText("$" + playerMoney);

        tiers[currentTier].getStyleClass().remove("currentTier");
        currentTier = 0;

        loadQuestion(question);
        tiers[currentTier].getStyleClass().add("currentTier");
    }

    /**
     * Returns the user to the main menu screen.
     *
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
     *
     * @param q the Question to display
     */
    private void loadQuestion(Question q) {

        messageLabel.setText(q.getQuestionText());

        String[] answers = q.getAnswers();
        A.setText(answers[0]);
        B.setText(answers[1]);
        C.setText(answers[2]);
        D.setText(answers[3]);

        startTimer();
    }

    public void updateLanguage() {
        languageController lc = languageController.getInstance();
        playerMoneyLabel.setText(lc.getString("playerMoney"));
        next.setText(lc.getString("nextQuestion"));
        restart.setText(lc.getString("restart"));
        mainMenu.setText(lc.getString("mainMenu"));
    }

    @FXML private void onExitClick()  { menuBarHelper.exit(); }
    @FXML private void onDarkClick()  { menuBarHelper.setDark(); }
    @FXML private void onLightClick() { menuBarHelper.setLight(); }

    @FXML
    private void onENClick() {
        menuBarHelper.setEnglish();
        updateLanguage();
    }

    @FXML
    private void onFRClick() {
        menuBarHelper.setFrench();
        updateLanguage();
    }
}


