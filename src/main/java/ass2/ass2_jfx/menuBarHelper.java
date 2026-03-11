package ass2.ass2_jfx;

import javafx.application.Platform;
/**
 * Utility class for handling menu bar actions such as
 * exiting the app, switching themes, and changing languages.
 */
public class menuBarHelper {

    /** Exits the application. */
    public static void exit() {
        Platform.exit();
    }

    /** Applies the dark theme to the application. */
    public static void setDark() {
        sceneController.getInstance().setTheme(
                "/ass2/ass2_jfx/styles-dark.css"
        );
    }

    /** Applies the light theme to the application. */
    public static void setLight() {
        sceneController.getInstance().setTheme(
                "/ass2/ass2_jfx/styles-light.css"
        );
    }

    /** Switches the application language to English. */
    public static void setEnglish() {
        languageController.getInstance().setLocale(java.util.Locale.ENGLISH);
    }

    /** Switches the application language to French. */
    public static void setFrench() {
        languageController.getInstance().setLocale(java.util.Locale.FRENCH);
    }
}