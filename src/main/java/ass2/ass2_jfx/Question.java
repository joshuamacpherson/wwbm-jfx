package ass2.ass2_jfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class Question {
    private final String questionText;
    private final String[] answers;
    private final int correctIndex;

    public Question(String questionText, String[] answers, int correctIndex) {
        this.questionText = questionText;
        this.answers = answers;
        this.correctIndex = correctIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getAnswers() {
        return answers;
    }

    public String getCorrectAnswer() {
        return answers[correctIndex];
    }

    public boolean isCorrect(String answer) {
        return answers[correctIndex].equals(answer);
    }

    public boolean isCorrect(int index) {
        return index == correctIndex;
    }
}