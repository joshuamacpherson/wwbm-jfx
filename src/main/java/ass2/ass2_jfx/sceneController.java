package ass2.ass2_jfx;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

public class sceneController {
    private Stage stage;
    private Scene scene;
    
    private void applyStyles(Scene scene) {
        URL css = getClass().getResource("/ass2/ass2_jfx/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    private void setStageScene(ActionEvent event, Parent root) {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double width = screenBounds.getWidth() * 0.7;
        double height = screenBounds.getHeight() * 0.7;
        scene = new Scene(root, width, height);
        applyStyles(scene);
        stage.setScene(scene);
        stage.show();
    }
    
    public void switchToPlay(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/ass2/ass2_jfx/play.fxml"));
        setStageScene(event, root);
    }

    public void switchToMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/ass2/ass2_jfx/menu.fxml"));
        setStageScene(event, root);
    }
    
    public void switchToDesign(ActionEvent event) throws IOException  {
        Parent root = FXMLLoader.load(getClass().getResource("/ass2/ass2_jfx/design.fxml"));
        setStageScene(event, root);
    }
}
