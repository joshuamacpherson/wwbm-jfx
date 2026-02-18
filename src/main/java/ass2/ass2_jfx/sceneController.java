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

    /** Singleton instance of sceneController. */
    private static sceneController instance;

    /**
     * Private constructor to prevent external instantiation.
     */
    private sceneController() {}

    /**
     * Returns the singleton instance of sceneController.
     *
     * @return the single sceneController instance
     */
    public static sceneController getInstance() {
        if (instance == null) {
            instance = new sceneController();
        }
        return instance;
    }

    /**
     * Applies the global stylesheet to a scene if available.
     *
     * @param scene the Scene to apply styling to
     */
    private void applyStyles(Scene scene) {
        URL css = getClass().getResource("/ass2/ass2_jfx/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    /**
     * Sets a new scene on the current stage.
     * The scene size is set to 70% of the screen's visual bounds.
     *
     * @param event the ActionEvent triggering the scene change
     * @param root the root node of the new scene
     */
    private void setStageScene(ActionEvent event, Parent root) {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        double width = screenBounds.getWidth() * 0.7;
        double height = screenBounds.getHeight() * 0.7;

        Scene scene = new Scene(root, width, height);

        applyStyles(scene);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Switches to the Play screen.
     *
     * @param event the ActionEvent triggering the switch
     * @throws IOException if the FXML file cannot be loaded
     */
    public void switchToPlay(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ass2/ass2_jfx/play.fxml")));
        setStageScene(event, root);
    }

    /**
     * Switches to the Main Menu screen.
     *
     * @param event the ActionEvent triggering the switch
     * @throws IOException if the FXML file cannot be loaded
     */
    public void switchToMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ass2/ass2_jfx/menu.fxml")));
        setStageScene(event, root);
    }

    /**
     * Switches to the Design Mode screen.
     *
     * @param event the ActionEvent triggering the switch
     * @throws IOException if the FXML file cannot be loaded
     */
    public void switchToDesign(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ass2/ass2_jfx/design.fxml")));
        setStageScene(event, root);
    }
}