package ass2.ass2_jfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.io.IOException;
import java.util.LinkedHashMap;

public class playController {
    @FXML private Label messageLabel;
    @FXML private Button A;
    @FXML private Button B;
    @FXML private Button C;
    @FXML private Button D;
    @FXML private Button next;
    @FXML private Button restart;
    @FXML private Button mainMenu;
    @FXML private Label tier1;
    @FXML private Label tier2;
    @FXML private Label tier3;
    @FXML private Label tier4;
    @FXML private Label tier5;
    @FXML private Label tier6;
    @FXML private Label tier7;
    @FXML private Label tier8;
    @FXML private Label tier9;
    @FXML private Label tier10;
    @FXML private Label tier11;
    @FXML private Label tier12;
    @FXML private Label tier13;
    @FXML private Label tier14;
    @FXML private Label tier15;
    @FXML private Label playerMoneyLabel;
    @FXML private Label timerLabel;
    private Timeline timer;
    private int timeLeft;
    private LinkedHashMap<Label, Integer> tierMap;
    int playerMoney = 0;
    
    private Label[] tiers;
    
    Question question = new Question("Who's the best?", new String[]{"Spiderman", "Superman", "Batman", "Wonder Woman"}, 0);
    int currentTier = 0;
    
    @FXML
    private void initialize() {
        tiers = new Label[] { tier1, tier2, tier3, tier4, tier5, tier6, tier7, tier8, tier9, tier10, tier11, tier12, tier13, tier14, tier15 };
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
    
    private void startTimer() {
        if (timer != null) timer.stop();
        timeLeft = 60;
        timerLabel.setText(String.valueOf(timeLeft));
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));
            if (timeLeft <= 0) {
                timer.stop();
                messageLabel.setText("Time's up!");
                A.setDisable(true);
                B.setDisable(true);
                C.setDisable(true);
                D.setDisable(true);
                next.setVisible(true);
            }
        }));
        timer.setCycleCount(60);
        timer.play();
        A.setDisable(false);
        B.setDisable(false);
        C.setDisable(false);
        D.setDisable(false);
    }
    
    @FXML
    private void onAnswerClick(ActionEvent event) {
        if (timer != null) timer.stop();
        Button clickedButton = (Button) event.getSource();
        String clickedAnswer = clickedButton.getText();
        if (question.isCorrect(clickedAnswer)) {
            messageLabel.setText("Correct!");
            playerMoney += tierMap.get(tiers[currentTier]);
            playerMoneyLabel.setText("$" + playerMoney);
            next.setVisible(true);
            currentTier++;
        } else {
            messageLabel.setText("Wrong! The correct answer is: " + question.getCorrectAnswer());
            restart.setVisible(true);
            mainMenu.setVisible(true);
        }
        A.setVisible(false);
        B.setVisible(false);
        C.setVisible(false);
        D.setVisible(false);
    }
    
    @FXML
    private void onNextClick() {
        loadQuestion(question);
        A.setVisible(true);
        B.setVisible(true);
        C.setVisible(true);
        D.setVisible(true);
        next.setVisible(false);
        tiers[currentTier].getStyleClass().add("currentTier");
        tiers[currentTier-1].getStyleClass().remove("currentTier");
    }
    
    @FXML
    private void onRestartClick() {
        A.setVisible(true);
        B.setVisible(true);
        C.setVisible(true);
        D.setVisible(true);
        restart.setVisible(false);
        mainMenu.setVisible(false);
        playerMoney = 0;
        playerMoneyLabel.setText("$" + playerMoney);
        tiers[currentTier].getStyleClass().remove("currentTier");
        currentTier = 0;
        loadQuestion(question);
        tiers[currentTier].getStyleClass().add("currentTier");
    }
    
    @FXML
    private void onMainMenuClick(ActionEvent event) {
        try {
            sceneController.getInstance().switchToMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadQuestion(Question q) {
        messageLabel.setText(q.getQuestionText());
        String[] answers = q.getAnswers();
        A.setText(answers[0]);
        B.setText(answers[1]);
        C.setText(answers[2]);
        D.setText(answers[3]);
        startTimer();
    }
}
