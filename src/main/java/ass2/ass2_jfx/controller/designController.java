package ass2.ass2_jfx.controller;

import ass2.ass2_jfx.model.Player;
import ass2.ass2_jfx.model.Question;
import ass2.ass2_jfx.model.dataStore;
import ass2.ass2_jfx.model.wwtbmExceptions;
import ass2.ass2_jfx.view.menuBarHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller for the Design Mode screen.
 * Handles full CRUD operations for Question objects:
 * - Adding new questions
 * - Editing existing questions
 * - Saving edited questions
 * - Deleting questions
 *
 * @author Shane O'Connell
 * @author Joshua MacPherson
 * @version Java 21
 */
public class designController {
    /** Lists used to store questions and players */
    @FXML private VBox listViewContainer, questionForm, playerForm;
    /** Text fields to update questions and player name */
    @FXML private TextField questionField, answerA, answerB, answerC, answerD, playerNameField;
    /** ComboBox used to choose correct answer */
    @FXML private ComboBox<String> correctAns;
    /** View all the default questions form dataStore + new questions added */
    @FXML private ListView<Question> questionListView;
    /** View all the default players from dataStore + new players added */
    @FXML private ListView<Player> playerListView;
    /** Changes a scene to question manager or player manager */
    @FXML private Button questionManager, playerManager;
    /** Background image */
    @FXML private ImageView backgroundImage;
    /** The table of players */
    @FXML private TableView<Player> playerTable;
    /** Used to update player points */
    @FXML private TextField pointsField;

    /** Used to control the language of the scene */
    private final languageController lc = languageController.getInstance();
    /** Pulls the questions from dataStore */
    private final ArrayList<Question> questions = dataStore.getInstance().getQuestions();
    /** Pulls the players from dataStore */
    private final ArrayList<Player> players = dataStore.getInstance().getPlayers();
    /** Checks for null edits */
    private Question questionBeingEdited = null;
    /** Checks for null edits */
    private Player playerBeingEdited = null;

    /**
     * Called the instant the program runs and executes.
     */
    @FXML
    public void initialize() {
        for (int i = 0; i < 15; i++) questionListView.getItems().add(questions.get(i));
        correctAns.getItems().addAll(
                lc.getString("answerA"),
                lc.getString("answerB"),
                lc.getString("answerC"),
                lc.getString("answerD")
        );
    }

    /**
     * Switches back to the main menu scene.
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

    /** Adds a new question to the list after validating input fields. */
    @FXML
    private void addQuestion() {
        try {
            String question = questionField.getText();
            String[] questionAnswers = {
                    answerA.getText(), answerB.getText(),
                    answerC.getText(), answerD.getText()
            };
            int correctInd = correctAns.getSelectionModel().getSelectedIndex();

            if (question.isEmpty()
                    || questionAnswers[0].isEmpty()
                    || questionAnswers[1].isEmpty()
                    || questionAnswers[2].isEmpty()
                    || questionAnswers[3].isEmpty()
                    || correctInd == -1) {
                throw new wwtbmExceptions("Please fill all fields before adding a question.");
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
            success.setTitle(lc.getString("questionAdded"));
            success.setHeaderText(null);
            success.setContentText(lc.getString("questionAddedSuccess"));
            success.showAndWait();

        } catch (wwtbmExceptions e) {
            showError(e.getMessage());
        }
    }

    /** Loads the selected question into the form fields for editing. */
    @FXML
    private void editQuestion() {
        try {
            Question selected = questionListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new wwtbmExceptions("Select a question to edit.");
            }

            questionBeingEdited = selected;
            questionField.setText(selected.getQuestionText());

            String[] answers = selected.getAnswers();
            answerA.setText(answers[0]);
            answerB.setText(answers[1]);
            answerC.setText(answers[2]);
            answerD.setText(answers[3]);

            correctAns.getSelectionModel().select(
                    selected.isCorrect(0) ? 0
                            : selected.isCorrect(1) ? 1
                            : selected.isCorrect(2) ? 2 : 3
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(lc.getString("editMode"));
            alert.setHeaderText(null);
            alert.setContentText(lc.getString("nowEditing"));
            alert.showAndWait();

        } catch (wwtbmExceptions e) {
            showError(e.getMessage());
        }
    }

    /** Saves changes made to the currently edited question. */
    @FXML
    private void saveEditedQuestion() {
        try {
            if (questionBeingEdited == null) {
                throw new wwtbmExceptions("No question is currently being edited.");
            }
            String question = questionField.getText();
            String[] questionAnswers = {
                    answerA.getText(), answerB.getText(),
                    answerC.getText(), answerD.getText()
            };
            int correctInd = correctAns.getSelectionModel().getSelectedIndex();

            if (question.isEmpty()
                    || questionAnswers[0].isEmpty()
                    || questionAnswers[1].isEmpty()
                    || questionAnswers[2].isEmpty()
                    || questionAnswers[3].isEmpty()
                    || correctInd == -1) {
                throw new wwtbmExceptions("Please fill all fields before saving.");
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
            alert.setTitle(lc.getString("success"));
            alert.setHeaderText(null);
            alert.setContentText(lc.getString("changesSaved"));
            alert.showAndWait();
        } catch (wwtbmExceptions e) {
            showError(e.getMessage());
        }
    }

    /** Deletes the selected question after confirmation. */
    @FXML
    private void deleteQuestion() {
        try {
            if (questions.isEmpty()) {
                throw new wwtbmExceptions("There are no questions to delete.");
            }

            Question selected = questionListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new wwtbmExceptions("Select a question to delete.");
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this question?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                questions.remove(selected);
                questionListView.getItems().remove(selected);
            }

        } catch (wwtbmExceptions e) {
            showError(e.getMessage());
        }
    }

    /**
     * Handles the Add Player button click.
     * Creates a new player with name and starting money.
     * Validates input and shows error messages if needed.
     */
    @FXML
    private void addPlayer() {
        try {
            String name = playerNameField.getText().trim();
            if (name.isEmpty()) {
                throw new wwtbmExceptions("Enter a player name.");
            }

            int money = 0;
            String moneyText = pointsField.getText().trim();
            if (!moneyText.isEmpty()) {
                try {
                    money = Integer.parseInt(moneyText);
                    if (money < 0) {
                        throw new wwtbmExceptions("Money cannot be negative.");
                    }
                } catch (NumberFormatException e) {
                    throw new wwtbmExceptions("Enter a valid number for money.");
                }
            }

            Player newPlayer = new Player(name);
            newPlayer.addMoneyToPlayer(money);
            dataStore.getInstance().getPlayers().add(newPlayer);
            playerListView.getItems().add(newPlayer);

            playerNameField.clear();
            pointsField.clear();
        } catch (wwtbmExceptions e) {
            showError(e.getMessage());
        }
    }

    /** Allows the admin to edit an added player in the game. */
    @FXML
    private void editPlayer() {
        Player selected = playerListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a player to edit.");
            return;
        }
        playerNameField.setText(selected.getName());
        pointsField.setText(String.valueOf(selected.getPlayerMoney()));
    }

    /** Allows the admin to save an edited player in the game. */
    @FXML
    private void saveEditedPlayer() {
        try {
            Player selected = playerListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new wwtbmExceptions("No player selected to save.");
            }

            String newName = playerNameField.getText().trim();
            if (newName.isEmpty()) {
                throw new wwtbmExceptions("Enter a player name.");
            }

            int newMoney;
            try {
                newMoney = Integer.parseInt(pointsField.getText().trim());
                if (newMoney < 0) {
                    throw new wwtbmExceptions("Money cannot be negative.");
                }
            } catch (NumberFormatException e) {
                throw new wwtbmExceptions("Enter a valid number for money.");
            }

            selected.resetPlayerMoney(); // reset old money
            selected.addMoneyToPlayer(newMoney); // apply new money

            playerListView.refresh();
            playerNameField.clear();
            pointsField.clear();

        } catch (wwtbmExceptions e) {
            showError(e.getMessage());
        }
    }

    /**
     * Allows the admin to delete a player from the game.
     * Validates selection and shows confirmation before deleting.
     */
    @FXML
    private void deletePlayer() {
        try {
            Player selected = playerListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new wwtbmExceptions("Select a player to delete.");
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this player?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                dataStore.getInstance().getPlayers().remove(selected);
                playerListView.getItems().remove(selected);
            }
        } catch (wwtbmExceptions e) {
            showError(e.getMessage());
        }
    }

    /** Toggles visibility between the question form and the list view. */
    @FXML
    private void showQuestionForm() {
        boolean isQuestionFormVisible = questionForm.isVisible();
        boolean isListViewVisible = listViewContainer.isVisible();

        questionForm.setVisible(!isQuestionFormVisible);
        questionForm.setManaged(!isQuestionFormVisible);
        listViewContainer.setVisible(!isListViewVisible);
        listViewContainer.setManaged(!isListViewVisible);

        playerForm.setVisible(false);
        playerForm.setManaged(false);
        playerManager.setText(lc.getString("playerManager"));
        backgroundImage.setVisible(isQuestionFormVisible && !playerForm.isVisible());

        if (isQuestionFormVisible) {questionManager.setText(lc.getString("questionManager"));
        }
    }

    /**
     * Toggles visibility of the Question Manager section.
     * Ensures Player Manager is hidden when Question Manager is active.
     */
    @FXML
    private void showPlayerForm() {
        boolean isVisible = playerForm.isVisible();

        playerForm.setVisible(!isVisible);
        playerForm.setManaged(!isVisible);

        questionForm.setVisible(false);
        questionForm.setManaged(false);
        listViewContainer.setVisible(false);
        listViewContainer.setManaged(false);

        questionManager.setText(lc.getString("questionManager"));
        backgroundImage.setVisible(isVisible);

        if (isVisible) {
            playerManager.setText(lc.getString("playerManager"));
            backgroundImage.setVisible(true);
        }
    }

    /** Exits the game. */
    @FXML private void onExitClick()  { menuBarHelper.exit(); }
    /** Switches the UI to dark mode. */
    @FXML private void onDarkClick()  { menuBarHelper.setDark(); }
    /** Switches the UI to light mode. */
    @FXML private void onLightClick() { menuBarHelper.setLight(); }

    /**
     * Switches the language to English and reloads the scene.
     * @throws IOException if the scene fails to reload
     */
    @FXML
    private void onENClick() throws IOException {
        menuBarHelper.setEnglish();
        sceneController.getInstance().reloadCurrentScene();
    }

    /**
     * Switches the language to French and reloads the scene.
     * @throws IOException if the scene fails to reload
     */
    @FXML
    private void onFRClick() throws IOException {
        menuBarHelper.setFrench();
        sceneController.getInstance().reloadCurrentScene();
    }

    /**
     * Helper function for cleaner code.
     * @param message shows an error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}