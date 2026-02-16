package ass2.ass2_jfx;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class menuController {

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