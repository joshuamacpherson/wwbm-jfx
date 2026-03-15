package ass2.ass2_jfx;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.Window;
import javafx.util.Duration;
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

    /** Path to the currently active CSS theme. */
    private String currentTheme = "/ass2/ass2_jfx/styles-dark.css";
    /** Singleton instance of the sceneController. */
    private static sceneController instance;
    /** Primary application stage. */
    private Stage stage;
    /** Tracks the last loaded FXML file for reload operations. */
    private String currentFxml;
    /** Private constructor for Singleton pattern, */
    private sceneController() {
    }

    /**
     * Returns the Singleton instance of the sceneController.
     * @return the shared controller instance
     */
    public static sceneController getInstance() {
        if (instance == null) {
            instance = new sceneController();
        }
        return instance;
    }

    /**
     * Sets the primary stage used for scene switching.
     * @param stage the main application stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Updates the active CSS theme and reapplies it to the current scene.
     * @param cssPath path to the new stylesheet
     */
    public void setTheme(String cssPath) {
        this.currentTheme = cssPath;
        if (stage != null && stage.getScene() != null) {
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            applyStyles(scene);
        }
    }

    /**
     * Applies the current CSS theme to the given scene.
     * @param scene the scene to style
     */
    private void applyStyles(Scene scene) {
        URL css = getClass().getResource(currentTheme);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    /**
     * Loads and displays the specified FXML file.
     * @param fxml the FXML resource path
     * @throws IOException if loading fails
     */
    private void loadScene(String fxml) throws IOException {
        this.currentFxml = fxml;
        double width = stage.getWidth();
        double height = stage.getHeight();

        ResourceBundle bundle = ResourceBundle.getBundle("ass2.ass2_jfx.QMillionaire", languageController.getInstance().getLocale());
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)), bundle);

        Scene scene = new Scene(root, width, height);
        applyStyles(scene);

        stage.setScene(scene);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.show();
    }

    /**
     * Reloads the most recently displayed FXML scene.
     * @throws IOException if loading fails
     */
    public void reloadCurrentScene() throws IOException {
        if (currentFxml != null) {
            loadScene(currentFxml);
        }
    }

    /**
     * Switches to the Play Mode scene.
     * @param event the triggering UI event
     * @throws IOException if loading fails
     */
    public void switchToPlay(ActionEvent event) throws IOException {
        grabStageFromEvent(event);
        loadScene("/ass2/ass2_jfx/play.fxml");
    }

    /**
     * Switches to the Main Menu scene.
     * @param event the triggering UI event
     * @throws IOException if loading fails
     */
    public void switchToMenu(ActionEvent event) throws IOException {
        grabStageFromEvent(event);
        loadScene("/ass2/ass2_jfx/menu.fxml");
    }

    /**
     * Switches to the Design Mode scene.
     * @param event the triggering UI event
     * @throws IOException if loading fails
     */
    public void switchToDesign(ActionEvent event) throws IOException {
        grabStageFromEvent(event);
        loadScene("/ass2/ass2_jfx/design.fxml");
    }

    /**
     * Retrieves and stores the window (Stage) from the event source
     * if it has not already been set.
     * @param event the action event triggered by a UI control
     */
    private void grabStageFromEvent(ActionEvent event) {
        if (stage == null) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        }
    }

    /**
     * Manually sets the current FXML path for reload operations.
     * @param fxml the FXML resource path
     */
    public void setCurrentFxml(String fxml) {
        this.currentFxml = fxml;
    }

    /**
     * Shows the splash screen and switches to the main menu after a delay.
     * Loads splash.fxml, applies styles, displays it on the primary stage,
     * then uses a timed transition to load the main menu scene.
     * @throws IOException if the splash FXML cannot be loaded
     */
    public void showSplash() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ass2/ass2_jfx/splash.fxml")));

        Scene scene = new Scene(root);
        applyStyles(scene);
        stage.setScene(scene);
        stage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            appMain app = new appMain();
            app.loadMainMenu(stage);
        });
        delay.play();
    }

    public Stage getStage() {
        return stage;
    }
}