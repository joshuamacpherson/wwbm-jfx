package ass2.ass2_jfx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;

/**
 * Controller for the Main Menu screen.
 * Responsibilities:
 * - Initializing localized button text
 * - Handling navigation to Play mode
 * - Handling navigation to Design mode
 *
 * @author Shane O'Connell
 * @author Joshua MacPherson
 * @version Java 21
 */
public class menuController {

    /** Button used to start the game in Play mode. */
    @FXML private Button playButton;

    /** Button used to open Design mode. */
    @FXML private Button designButton;

    /**
     * Handles the Play button click.
     * Switches the scene to the Play screen.
     * @param event the button click event
     */
    @FXML
    public void onPlayClick(ActionEvent event) throws IOException {
        if (dataStore.getInstance().getQuestions().size() < 15) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(sceneController.getInstance().getStage());
            alert.setTitle("Not Enough Questions");
            alert.setHeaderText(null);
            alert.setContentText("You must have 15 questions to start the game!");
            alert.show();
            return;
        }

        ArrayList<Player> players = dataStore.getInstance().getPlayers();

        if (players.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(sceneController.getInstance().getStage());
            alert.setTitle("No Players Exist");
            alert.setHeaderText(null);
            alert.setContentText("Please Create a Player in Design Mode First.");
            alert.show();
            return;
        }

        ChoiceDialog<Player> dialog = new ChoiceDialog<>(players.get(0), players);
        dialog.initOwner(sceneController.getInstance().getStage());
        dialog.setTitle("Select Player");
        dialog.setHeaderText("Choose a player");
        dialog.setContentText("Player: ");
        Optional<Player> result = dialog.showAndWait();

        if (result.isPresent()) {
            dataStore.getInstance().setCurrentPlayer(result.get());
            sceneController.getInstance().switchToPlay(event);
        }
    }

    /**
     * Handles the Design button click.
     * Switches the scene to the Design screen.
     * @param event the button click event
     */
    @FXML
    public void onDesignClick(ActionEvent event) {
        try {
            sceneController.getInstance().switchToDesign(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Updates all UI text based on the currently selected language. */
    public void updateLanguage() {
        languageController lc = languageController.getInstance();
        playButton.setText(lc.getString("play"));
        designButton.setText(lc.getString("design"));
    }

    /** Exits the application. */
    @FXML private void onExitClick() { menuBarHelper.exit(); }
    /** Applies the dark theme. */
    @FXML private void onDarkClick() { menuBarHelper.setDark(); }
    /** Applies the light theme. */
    @FXML private void onLightClick() { menuBarHelper.setLight(); }
    /** Switches the language to English and refreshes UI text. */
    @FXML private void onENClick() {menuBarHelper.setEnglish();updateLanguage();}
    /** Switches the language to French and refreshes UI text. */
    @FXML private void onFRClick() {menuBarHelper.setFrench();updateLanguage();}
}