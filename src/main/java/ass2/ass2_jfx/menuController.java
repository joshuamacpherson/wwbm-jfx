package ass2.ass2_jfx;

import java.io.IOException;

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

    public void updateLanguage() {
        languageController lc = languageController.getInstance();
        playButton.setText(lc.getString("play"));
        designButton.setText(lc.getString("design")
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