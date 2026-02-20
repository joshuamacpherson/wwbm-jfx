package ass2.ass2_jfx;

import javafx.application.Platform;

public class menuBarHelper {

    public static void exit() {
        Platform.exit();
    }

    public static void setDark() {
        sceneController.getInstance().setTheme(
                "/ass2/ass2_jfx/styles-dark.css"
        );
    }

    public static void setLight() {
        sceneController.getInstance().setTheme(
                "/ass2/ass2_jfx/styles-light.css"
        );
    }

    public static void setEnglish() {
        languageController.getInstance().setLocale(java.util.Locale.ENGLISH);
    }

    public static void setFrench() {
        languageController.getInstance().setLocale(java.util.Locale.FRENCH);
    }
}