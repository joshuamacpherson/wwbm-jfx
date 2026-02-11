package ass2.ass2_jfx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class playController {
    @FXML
    private Label messageLabel;
    
    @FXML
    public void onButtonPress(ActionEvent event) {
        messageLabel.setVisible(true);
    }
}
