package ass2.ass2_jfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller for the Design Mode screen.
 *
 * Handles full CRUD operations for Question objects:
 * - Adding new questions
 * - Editing existing questions
 * - Saving edited questions
 * - Deleting questions
 */
public class designController {

    @FXML private Button AddQ;
    @FXML private VBox listViewContainer;
    @FXML private VBox questionForm;
    @FXML private VBox playerForm;
    @FXML private TextField questionField;
    @FXML private TextField answerA;
    @FXML private TextField answerB;
    @FXML private TextField answerC;
    @FXML private TextField answerD;
    @FXML private ComboBox<String> correctAns;
    @FXML private ListView<Question> questionListView;
    @FXML private Button questionManager;
    @FXML private Button playerManager;
    @FXML private ListView<Player> playerListView;
    @FXML private TextField playerNameField;
    @FXML private ImageView backgroundImage;
    @FXML private TextField playerMoneyField;
    @FXML private Label playerTierLabel;

    /** Stores all created players */
    private final ArrayList<Player> players = new ArrayList<>();

    /** Tracks the question currently being edited */
    private Player playerBeingEdited = null;

    /** Stores all created questions. */
    private final ArrayList<Question> questions = new ArrayList<>();

    /** Tracks the question currently being edited. */
    private Question questionBeingEdited = null;

    /**
     * Switches back to the main menu scene.
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
     * Adds a new question to the list after validating input fields.
     *
     * @param event the button click event
     */
    @FXML
    private void addQuestion(ActionEvent event) {
        String question = questionField.getText();

        String[] questionAnswers = {
                answerA.getText(),
                answerB.getText(),
                answerC.getText(),
                answerD.getText()
        };
        int correctInd = correctAns.getSelectionModel().getSelectedIndex();

        if (question.isEmpty()
                || questionAnswers[0].isEmpty()
                || questionAnswers[1].isEmpty()
                || questionAnswers[2].isEmpty()
                || questionAnswers[3].isEmpty()
                || correctInd == -1) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all fields and select the correct answer.");
            alert.showAndWait();
            return;
        }

        Question q = new Question(question, questionAnswers, correctInd);
        questions.add(q);
        questionListView.getItems().add(q);

        questionField.clear();
        answerA.clear();
        answerB.clear();
        answerC.clear();
        answerD.clear();
        correctAns.getSelectionModel().clearSelection();

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Question Added");
        success.setHeaderText(null);
        success.setContentText("Question added successfully.");
        success.showAndWait();
    }

    /**
     * Loads the selected question into the form fields for editing.
     *
     * @param event the button click event
     */
    @FXML
    private void editQuestion(ActionEvent event) {
        Question selected = questionListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a question to edit.");
            alert.showAndWait();
            return;
        }

        questionBeingEdited = selected;
        questionField.setText(selected.getQuestionText());

        String[] answers = selected.getAnswers();
        answerA.setText(answers[0]);
        answerB.setText(answers[1]);
        answerC.setText(answers[2]);
        answerD.setText(answers[3]);

        correctAns.getSelectionModel().select(selected.isCorrect(0) ? 0 :
                        selected.isCorrect(1) ? 1 : selected.isCorrect(2) ? 2 : 3
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Edit Mode");
        alert.setHeaderText(null);
        alert.setContentText("Now editing selected question.");
        alert.showAndWait();
    }

    /**
     * Saves changes made to the currently edited question.
     *
     * @param event the button click event
     */
    @FXML
    private void saveEditedQuestion(ActionEvent event) {
        if (questionBeingEdited == null) {
            return;
        }

        String question = questionField.getText();

        String[] questionAnswers = {
                answerA.getText(),
                answerB.getText(),
                answerC.getText(),
                answerD.getText()
        };

        int correctInd = correctAns.getSelectionModel().getSelectedIndex();

        if (question.isEmpty()
                || questionAnswers[0].isEmpty()
                || questionAnswers[1].isEmpty()
                || questionAnswers[2].isEmpty()
                || questionAnswers[3].isEmpty()
                || correctInd == -1) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Please fill all fields and select the correct answer."
            );
            alert.showAndWait();
            return;
        }

        int index = questions.indexOf(questionBeingEdited);
        Question updated = new Question(question, questionAnswers, correctInd);
        questions.set(index, updated);
        questionListView.getItems().set(index, updated);
        questionBeingEdited = null;

        questionField.clear();
        answerA.clear();
        answerB.clear();
        answerC.clear();
        answerD.clear();
        correctAns.getSelectionModel().clearSelection();
        questionListView.getSelectionModel().clearSelection();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Changes have been saved successfully.");
        alert.showAndWait();
    }

    /**
     * Deletes the selected question after confirmation.
     *
     * @param event the button click event
     */
    @FXML
    private void deleteQuestion(ActionEvent event) {
        Question selected = questionListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a question to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this question?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            questions.remove(selected);
            questionListView.getItems().remove(selected);
        }
    }

    /**
     * Toggles visibility between the question form and the list view.
     *
     * @param event the button click event
     */
    @FXML
    private void showQuestionForm(ActionEvent event) {
        boolean isQuestionFormVisible = questionForm.isVisible();
        boolean isListViewVisible = listViewContainer.isVisible();

        questionForm.setVisible(!isQuestionFormVisible);
        questionForm.setManaged(!isQuestionFormVisible);
        listViewContainer.setVisible(!isListViewVisible);
        listViewContainer.setManaged(!isListViewVisible);

        playerForm.setVisible(false);
        playerForm.setManaged(false);
        playerManager.setText("Player Manager");

        backgroundImage.setVisible(isQuestionFormVisible && !playerForm.isVisible());

        if (isQuestionFormVisible) {
            questionManager.setText("Question Manager");
        }
    }

    /**
     * Initializes UI components when the controller is loaded.
     */
    @FXML
    public void initialize() {
        correctAns.getItems().addAll("A", "B", "C", "D");
    }

    /**
     * Allows the admin to add a player to the game.
     *
     * @param event the button click event
     */
    @FXML
    private void addPlayer(ActionEvent event) {
        String name = playerNameField.getText();

        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a player name.");
            alert.showAndWait();
        }

        Player player = new Player(name);
        players.add(player);
        playerListView.getItems().add(player);
        playerNameField.clear();
    }

    /**
     * Allows the admin to edit an added player in the game.
     *
     * @param event the button click event
     */
    @FXML
    private void editPlayer(ActionEvent event) {
        Player selected = playerListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a player to edit");
            alert.showAndWait();
        }

        playerBeingEdited = selected;
        assert selected != null;
        playerNameField.setText(selected.getName());
        playerMoneyField.setText(String.valueOf(selected.getPlayerMoney()));
        playerTierLabel.setText("Tier: " + selected.getPlayerTier());
    }

    /**
     * Allows the admin to save an edited player in the game.
     *
     * @param event the button click event
     */
    @FXML
    private void saveEditedPlayer(ActionEvent event) {

        if (playerBeingEdited == null) {
            return;
        }

        String name = playerNameField.getText();

        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a player name.");
            alert.showAndWait();
        }

        int ind = players.indexOf(playerBeingEdited);
        Player updated = new Player(name);
        players.set(ind, updated);
        playerListView.getItems().set(ind, updated);
        playerBeingEdited = null;
        playerNameField.clear();
        playerListView.getSelectionModel().clearSelection();
    }

    /**
     * Allows the admin to delete a player from the game.
     *
     * @param event the button click event
     */
    @FXML
    private void deletePlayer(ActionEvent event) {
        Player selected = playerListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a player to delete");
            alert.showAndWait();
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this player?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            players.remove(selected);
            playerListView.getItems().remove(selected);
        }
    }

    /**
     * Toggles visibility of the Question Manager section.
     * Ensures Player Manager is hidden when Question Manager is active.
     *
     * @param event the button click event
     */
    @FXML
    private void showPlayerForm(ActionEvent event) {
        boolean isVisible = playerForm.isVisible();

        playerForm.setVisible(!isVisible);
        playerForm.setManaged(!isVisible);

        questionForm.setVisible(false);
        questionForm.setManaged(false);

        listViewContainer.setVisible(false);
        listViewContainer.setManaged(false);

        questionManager.setText("Question Manager");

        backgroundImage.setVisible(isVisible);

        if (isVisible) {
            playerManager.setText("Player Manager");
            backgroundImage.setVisible(true);
        }
    }

    /**
     * Allows the admin to add money to chosen player.
     *
     * @param event the button click event
     */
    @FXML
    private void addMoney(ActionEvent event) {
        Player selected = playerListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        try {
            int amount = Integer.parseInt((playerMoneyField.getText()));
            selected.addMoneyToPlayer(amount);
            playerMoneyField.setText(String.valueOf(selected.getPlayerMoney()));
        } catch (NumberFormatException e) {
            return;
        }
    }

    /**
     * Allows the admin to boost the tier of the player.
     *
     * @param event the button click event
     */
    @FXML
    private void boostPlayerTier(ActionEvent event) {
        Player selected = playerListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }
        selected.boostPlayerTier();
        playerTierLabel.setText("Tier: " + selected.getPlayerMoney());
    }

    /**
     * Allows the admin to reset the players' money.
     *
     * @param event the button click event
     */
    @FXML
    private void resetPlayerMoney(ActionEvent event) {
        Player selected = playerListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }
        selected.resetPlayerMoney();
        playerMoneyField.setText("0");
    }

    @FXML
    private void resetPlayerTier(ActionEvent event) {
        Player selected = playerListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        selected.resetPlayerTier();
        playerTierLabel.setText("0");
    }
}
