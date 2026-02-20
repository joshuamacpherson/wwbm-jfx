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
 * Handles full CRUD operations for Question objects:
 * - Adding new questions
 * - Editing existing questions
 * - Saving edited questions
 * - Deleting questions
 */
public class designController {

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
    @FXML private Button AddQ;
    @FXML private Menu fileMenu;
    @FXML private Menu configMenu;
    @FXML private Menu lookFeelMenu;
    @FXML private Menu languageMenu;
    @FXML private MenuItem exitItem;
    @FXML private MenuItem settingsItem;
    @FXML private MenuItem darkItem;
    @FXML private MenuItem lightItem;
    @FXML private MenuItem englishItem;
    @FXML private MenuItem frenchItem;
    @FXML private Label designMode;
    @FXML private Label playerManagerLabel;
    @FXML private Button mainMenu;
    @FXML private Button editQuestion;
    @FXML private Button saveEdits;
    @FXML private Button deleteQuestion;
    @FXML private Button addPlayer;
    @FXML private Button editPlayer;
    @FXML private Button saveChanges;
    @FXML private Button deletePlayer;
    @FXML private Label questionsLabel;

    /**  Used for internationalization */
    languageController lc = languageController.getInstance();

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
     */
    @FXML
    private void addQuestion() {
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
            alert.setTitle(lc.getString("validationError"));
            alert.setHeaderText(null);
            alert.setContentText(lc.getString("fillAllFields"));
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
        success.setTitle(lc.getString("questionAdded"));
        success.setHeaderText(null);
        success.setContentText(lc.getString("questionAddedSuccess"));
        success.showAndWait();
    }

    /**
     * Loads the selected question into the form fields for editing.
     */
    @FXML
    private void editQuestion() {
        Question selected = questionListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(lc.getString("noSelection"));
            alert.setHeaderText(null);
            alert.setContentText(lc.getString("selectQuestionEdit"));
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
        alert.setTitle(lc.getString("editMode"));
        alert.setHeaderText(null);
        alert.setContentText(lc.getString("nowEditing"));
        alert.showAndWait();
    }

    /**
     * Saves changes made to the currently edited question.
     */
    @FXML
    private void saveEditedQuestion() {
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
            alert.setTitle(lc.getString("validationError"));
            alert.setHeaderText(null);
            alert.setContentText(lc.getString("fillAllFields"));
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
        alert.setTitle(lc.getString("success"));
        alert.setHeaderText(null);
        alert.setContentText(lc.getString("changesSaved"));
        alert.showAndWait();
    }

    /**
     * Deletes the selected question after confirmation.
     */
    @FXML
    private void deleteQuestion() {
        if (questions.isEmpty()) {;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(lc.getString("noQuestions"));
            alert.setHeaderText(null);
            alert.setContentText(lc.getString("noQuestionsToDelete"));
            alert.showAndWait();
            return;
        }

        Question selected = questionListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(lc.getString("noSelectionS"));
            alert.setHeaderText(null);
            alert.setContentText(lc.getString("selectQuestionDelete"));
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(lc.getString("confirmDelete"));
        confirm.setHeaderText(null);
        confirm.setContentText(lc.getString("confirmDeleteQuestion"));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            questions.remove(selected);
            questionListView.getItems().remove(selected);
        }
    }

    /**
     * Toggles visibility between the question form and the list view.
     */
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

        if (isQuestionFormVisible) {
            questionManager.setText(lc.getString("questionManager"));
        }
    }

    /**
     * Initializes UI components when the controller is loaded.
     */
    @FXML
    public void initialize() {
        correctAns.getItems().addAll(
                lc.getString("answerA"),
                lc.getString("answerB"),
                lc.getString("answerC"),
                lc.getString("answerD")
        );
        updateLanguage();
    }

    /**
     * Allows the admin to add a player to the game.
     */
    @FXML
    private void addPlayer() {
        String name = playerNameField.getText();

        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(lc.getString("validationError"));
            alert.setHeaderText(null);
            alert.setContentText(lc.getString("enterPlayerNamePrompt"));
            alert.showAndWait();
        }

        Player player = new Player(name);
        players.add(player);
        playerListView.getItems().add(player);
        playerNameField.clear();
    }

    /**
     * Allows the admin to edit an added player in the game.
     */
    @FXML
    private void editPlayer() {
        Player selected = playerListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(lc.getString("noSelection"));
            alert.setHeaderText(null);
            alert.setContentText(lc.getString("selectPlayerEdit"));
            alert.showAndWait();
            return;
        }

        playerBeingEdited = selected;
        playerNameField.setText(selected.getName());
    }

    /**
     * Allows the admin to save an edited player in the game.
     */
    @FXML
    private void saveEditedPlayer() {

        if (playerBeingEdited == null) {
            return;
        }

        String name = playerNameField.getText();

        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(lc.getString("validationError"));
            alert.setHeaderText(null);
            alert.setContentText(lc.getString("enterPlayerNamePrompt"));
            alert.showAndWait();
            return;
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
     */
    @FXML
    private void deletePlayer() {
        Player selected = playerListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(lc.getString("noSelection"));
            alert.setHeaderText(null);
            alert.setContentText(lc.getString("selectPlayerDelete"));
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(lc.getString("confirmDelete"));
        confirm.setHeaderText(null);
        confirm.setContentText(lc.getString("confirmDeletePlayer"));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            players.remove(selected);
            playerListView.getItems().remove(selected);
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

    /**
     * Function to update all language fields
     */
    public void updateLanguage() {
        languageController lc = languageController.getInstance();
        designMode.setText(lc.getString("designMode"));
        mainMenu.setText(lc.getString("mainMenu"));
        questionManager.setText(lc.getString("questionManager"));
        playerManager.setText(lc.getString("playerManager"));
        questionsLabel.setText(lc.getString("questions"));
        AddQ.setText(lc.getString("addQuestion"));
        editQuestion.setText(lc.getString("editQuestion"));
        saveEdits.setText(lc.getString("saveChanges"));
        deleteQuestion.setText(lc.getString("deleteQuestion"));
        playerManagerLabel.setText(lc.getString("playerManager"));
        addPlayer.setText(lc.getString("addPlayer"));
        editPlayer.setText(lc.getString("editPlayer"));
        saveChanges.setText(lc.getString("saveChanges"));
        deletePlayer.setText(lc.getString("deletePlayer"));
        fileMenu.setText(lc.getString("file"));
        configMenu.setText(lc.getString("configuration"));
        lookFeelMenu.setText(lc.getString("lookFeel"));
        languageMenu.setText(lc.getString("language"));
        exitItem.setText(lc.getString("exit"));
        settingsItem.setText(lc.getString("settings"));
        darkItem.setText(lc.getString("dark"));
        lightItem.setText(lc.getString("light"));
        englishItem.setText(lc.getString("english"));
        frenchItem.setText(lc.getString("french"));
        questionField.setPromptText(lc.getString("enterQuestion"));
        playerNameField.setPromptText(lc.getString("enterPlayerName"));
        correctAns.setPromptText(lc.getString("selectCorrect"));
        answerA.setPromptText(lc.getString("answerA"));
        answerB.setPromptText(lc.getString("answerB"));
        answerC.setPromptText(lc.getString("answerC"));
        answerD.setPromptText(lc.getString("answerD"));
        questionsLabel.setText(lc.getString("questions"));
        correctAns.getItems().setAll(
                lc.getString("answerA"),
                lc.getString("answerB"),
                lc.getString("answerC"),
                lc.getString("answerD")
        );
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

