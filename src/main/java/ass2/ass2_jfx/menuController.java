package ass2.ass2_jfx;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class menuController {
    @FXML
    private Label welcomeText;

    @FXML
    public void onPlayClick(ActionEvent event) {
        try {
            sceneController controller = new sceneController();
            controller.switchToPlay(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onDesignClick(ActionEvent event) {
        try {
            sceneController controller = new sceneController();
            controller.switchToDesign(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}