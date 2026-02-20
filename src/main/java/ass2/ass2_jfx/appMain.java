package ass2.ass2_jfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Entry point for the QMillionaire JavaFX application.
 *
 * Responsibilities:
 * - Launching the JavaFX application
 * - Loading the initial FXML layout (menu screen)
 * - Setting the application window size and properties
 * - Applying global styles
 */
public class appMain extends Application {

    /**
     * Main method that launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Called automatically when the JavaFX application starts.
     * Loads the main menu scene, applies styling, and configures
     * the primary stage.
     *
     * @param stage the primary stage provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ass2/ass2_jfx/menu.fxml")));
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.7;
            double height = screenBounds.getHeight() * 0.7;
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ass2/ass2_jfx/styles-dark.css")).toExternalForm());

            stage.setTitle("QMillionaire");
            stage.setScene(scene);
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.show();
            sceneController.getInstance().setStage(stage);
            sceneController.getInstance().setCurrentFxml("/ass2/ass2_jfx/menu.fxml");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}