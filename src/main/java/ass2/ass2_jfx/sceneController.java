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
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Handles scene switching throughout the application.
 *
 * This class follows the Singleton pattern to ensure only one
 * sceneController instance exists during runtime.
 *
 * Responsibilities:
 * - Loading FXML files
 * - Applying CSS styles
 * - Resizing scenes relative to screen dimensions
 * - Switching between Play, Menu, and Design scenes
 */
public class sceneController {

    private String currentTheme = "/ass2/ass2_jfx/styles-dark.css";
    private static sceneController instance;
    private Stage stage;
    private String currentFxml;

    private sceneController() {}

    public static sceneController getInstance() {
        if (instance == null) {
            instance = new sceneController();
        }
        return instance;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setTheme(String cssPath) {
        this.currentTheme = cssPath;
        if (stage != null && stage.getScene() != null) {
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            applyStyles(scene);
        }
    }

    private void applyStyles(Scene scene) {
        URL css = getClass().getResource(currentTheme);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    private void loadScene(String fxml) throws IOException {
        this.currentFxml = fxml;
        ResourceBundle bundle = ResourceBundle.getBundle("ass2.ass2_jfx.QMillionaire", languageController.getInstance().getLocale());
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)), bundle);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double width = screenBounds.getWidth() * 0.7;
        double height = screenBounds.getHeight() * 0.7;

        Scene scene = new Scene(root, width, height);
        applyStyles(scene);

        stage.setScene(scene);
        stage.show();
    }

    public void reloadCurrentScene() throws IOException {
        if (currentFxml != null) {
            loadScene(currentFxml);
        }
    }

    public void switchToPlay(ActionEvent event) throws IOException {
        grabStageFromEvent(event);
        loadScene("/ass2/ass2_jfx/play.fxml");
    }

    public void switchToMenu(ActionEvent event) throws IOException {
        grabStageFromEvent(event);
        loadScene("/ass2/ass2_jfx/menu.fxml");
    }

    public void switchToDesign(ActionEvent event) throws IOException {
        grabStageFromEvent(event);
        loadScene("/ass2/ass2_jfx/design.fxml");
    }

    private void grabStageFromEvent(ActionEvent event) {
        if (stage == null) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        }
    }

    public void setCurrentFxml(String fxml) {
        this.currentFxml = fxml;
    }
}