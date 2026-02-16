package ass2.ass2_jfx;

import java.util.Locale;
import java.util.ResourceBundle;

public class languageController {
    private static languageController instance;
    private Locale locale;
    private ResourceBundle bundle;

    private languageController() {
        // Default to English
        this.locale = Locale.ENGLISH;
        loadBundle();
    }

    public static languageController getInstance() {
        if (instance == null) {
            instance = new languageController();
        }
        return instance;
    }

    private void loadBundle() {
        bundle = ResourceBundle.getBundle("ass2.ass2_jfx.QMillionaire", locale);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        loadBundle();
    }

    public String getString(String key) {
        return bundle.getString(key);
    }
}
