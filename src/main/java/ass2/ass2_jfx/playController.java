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

    @FXML private Button next, restart, mainMenu, A, B, C, D;
    @FXML private Label t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15,
            messageLabel, playerMoneyAmountLabel, playerMoneyLabel, timerLabel;
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
     * Initializes the Play screen.
     * Sets localization, initializes prize tiers,
     * loads the first question, and highlights the starting tier.
     */
    @FXML
    private void initialize() {
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

    private void setAnswerButtonsVisible(boolean visible) {
        for (Button btn : answerButtons) {
            btn.setVisible(visible);
        }
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
                setAnswerButtonsVisible(false);
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
        setAnswerButtonsVisible(false);
    }

    /**
     * Loads the next question and advances the tier highlight.
     */
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
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(answers[i]);
        }

        startTimer();
    }

    /*
     * need this to update UI or gamestate is lost
     */
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

