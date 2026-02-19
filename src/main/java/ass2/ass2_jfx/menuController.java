package ass2.ass2_jfx;

import java.io.IOException;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the Main Menu screen.
 *
 * Responsibilities:
 * - Initializing localized button text
 * - Handling navigation to Play mode
 * - Handling navigation to Design mode
 */
public class menuController {

    /** Button used to start the game in Play mode. */
    @FXML private Button playButton;

    /** Button used to open Design mode. */
    @FXML private Button designButton;

    /**
     * Initializes the menu screen.
     * Sets the application language and updates button text
     * using the languageController.
     */
    @FXML
    private void initialize() {
        playButton.setText(
                languageController.getInstance().getString("play")
        );
        designButton.setText(
                languageController.getInstance().getString("design")
        );
    }

    /**
     * Handles the Play button click.
     * Switches the scene to the Play screen.
     *
     * @param event the button click event
     */
    @FXML
    public void onPlayClick(ActionEvent event) {
        try {
            sceneController.getInstance().switchToPlay(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the Design button click.
     * Switches the scene to the Design screen.
     *
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
}