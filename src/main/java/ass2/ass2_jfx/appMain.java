package ass2.ass2_jfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Main entry point for wwtbm game, loads main menu plus splash and runs the program.
 *
 * @author Shane O'Connell
 * @author Joshua MacPherson
 * @version Java 21
 */
public class appMain extends Application {

    /** Toggles debug mode visibility. */
    public static boolean DEBUG = false;

    /**
     * Main method that launches the JavaFX application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Called automatically when the JavaFX application starts.
     * First shows a splash screen, then loads the main menu.
     * @param stage the primary stage provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            sceneController sc = sceneController.getInstance();
            sc.setStage(stage);
            sc.showSplash();
            stage.setTitle("Who Wants to Be a Millionaire");
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the main menu scene after the splash screen finishes.
     * @param stage the primary stage
     */
    public void loadMainMenu(Stage stage) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "ass2.ass2_jfx.QMillionaire",
                    languageController.getInstance().getLocale()
            );

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ass2/ass2_jfx/menu.fxml")), bundle);
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.7;
            double height = screenBounds.getHeight() * 0.7;

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ass2/ass2_jfx/styles-dark.css")).toExternalForm());

            stage.setScene(scene);
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.initOwner(stage);
                    alert.setTitle("Debug Mode");
                    alert.setHeaderText(null);
                    alert.setContentText("Start with debug mode?");

                    Optional<ButtonType> result = alert.showAndWait();
                    DEBUG = result.isPresent() && result.get() == ButtonType.OK;
            });

            // Register stage with sceneController
            sceneController.getInstance().setStage(stage);
            sceneController.getInstance().setCurrentFxml("/ass2/ass2_jfx/menu.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}