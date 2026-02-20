package ass2.ass2_jfx;

import javafx.application.Platform;
import javafx.fxml.FXML;

import java.io.IOException;
import java.util.Locale;

public class menuBarController {

    @FXML
    private void onENClick() throws IOException {
        languageController.getInstance().setLocale(Locale.ENGLISH);
        sceneController.getInstance().reloadCurrentScene();
    }

    @FXML
    private void onFRClick() throws IOException {
        languageController.getInstance().setLocale(Locale.FRENCH);
        sceneController.getInstance().reloadCurrentScene();
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }
}
