package ass2.ass2_jfx;

import java.io.IOException;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class menuController {

    @FXML private Button playButton;
    @FXML private Button designButton;

    @FXML
    private void initialize() {
        languageController.getInstance().setLocale(Locale.FRENCH);
        playButton.setText(languageController.getInstance().getString("play"));
        designButton.setText(languageController.getInstance().getString("design"));
    }

    @FXML
    public void onPlayClick(ActionEvent event) {
        try {
            sceneController.getInstance().switchToPlay(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onDesignClick(ActionEvent event) {
        try {
            sceneController.getInstance().switchToDesign(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}