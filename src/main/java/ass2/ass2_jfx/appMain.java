package ass2.ass2_jfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class appMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ass2/ass2_jfx/menu.fxml"));
            
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.7;
            double height = screenBounds.getHeight() * 0.7;
    
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource("/ass2/ass2_jfx/styles.css").toExternalForm());

            stage.setTitle("QMillionaire");
            stage.setScene(scene);
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}