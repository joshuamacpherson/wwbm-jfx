package ass2.ass2_jfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;

public class designController {

    @FXML private VBox listViewContainer;
    @FXML private TextField questionField;
    @FXML private TextField answerA;
    @FXML private TextField answerB;
    @FXML private TextField answerC;
    @FXML private TextField answerD;
    @FXML private ComboBox<String> correctAns;
    @FXML public Label welcomeText;
    @FXML private ListView<String> questionListView;
    @FXML private VBox questionForm;
    @FXML private Button questionManager;
    @FXML private Button AddQ;

    // list to store questions
    private final ArrayList<Question> questions = new ArrayList<>();

    @FXML
    private void onMainMenuClick(ActionEvent event) {
        try {
            sceneController.getInstance().switchToMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addQuestion(ActionEvent event) {
        System.out.println("was clicked");
        String question = questionField.getText();
        // read text from fields
        String[] questionAnswers = {
                answerA.getText(),
                answerB.getText(),
                answerC.getText(),
                answerD.getText()
        };
        // get correct answer index from combobox
        int correctInd = correctAns.getSelectionModel().getSelectedIndex();

        // basic validation
        if (question.isEmpty() || questionAnswers[0].isEmpty() || questionAnswers[1].isEmpty() ||
                questionAnswers[2].isEmpty() || questionAnswers[3].isEmpty() || correctInd == -1) {
            // alert triggers if u don't fill out all given fields
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all fields and select the correct answer.");
            alert.showAndWait();
            return;
        }

        Question q = new Question(question, questionAnswers, correctInd);

        questions.add(q);
        questionListView.getItems().add(q.getQuestionText());
        // clear fields at end
        questionField.clear();
        answerA.clear();
        answerB.clear();
        answerC.clear();
        answerD.clear();
        correctAns.getSelectionModel().clearSelection();

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Question Added!");
        success.setHeaderText(null);
        success.setContentText("Question added successfully!");
        success.showAndWait();

        // Optional: print to console for debugging
        System.out.println("Current questions in the game:");
        for (Question ques : questions) {
            System.out.println(ques.getQuestionText());
        }
    }

    @FXML
    private void editQuestion(ActionEvent event) {
        String editQuestion = questionListView.getSelectionModel().getSelectedItem();
        Question selectedQuestion = null;
        if (editQuestion == null) {
            return;
        }
        // check if a question is selected
        if (editQuestion != null) {
            for (Question q : questions) {
                if (q.getQuestionText().equals(editQuestion)) {
                    selectedQuestion = q;
                    break;
                }
            }


        }
    }

    @FXML
    private void deleteQuestion(ActionEvent event) {

    }

    @FXML
    private void showQuestionForm(ActionEvent event) {
        // check the current visibility of the question form and the ListView
        boolean isQuestionFormVisible = questionForm.isVisible();
        boolean isListViewVisible = listViewContainer.isVisible();

        questionForm.setVisible(!isQuestionFormVisible);
        questionForm.setManaged(!isQuestionFormVisible);

        listViewContainer.setVisible(!isListViewVisible);
        listViewContainer.setManaged(!isListViewVisible);

        // change the text of the button based on visibility
        if (isQuestionFormVisible) {
            questionManager.setText("Question Manager");
        } else {
            questionManager.setText("Back");
        }
    }

    @FXML
    public void initialize() {
        // fill the ComboBox with answer options
        correctAns.getItems().addAll("A", "B", "C", "D", "Select Correct Answer");
    }
}