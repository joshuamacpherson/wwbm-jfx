package ass2.ass2_jfx;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Manages application localization and language settings.
 *
 * This class follows the Singleton pattern to ensure that
 * only one languageController instance exists.
 *
 * Responsibilities:
 * - Storing the current Locale
 * - Loading the appropriate ResourceBundle
 * - Providing localized strings by key
 */
public class languageController {

    /** Singleton instance of languageController. */
    private static languageController instance;

    /** The currently selected locale. */
    private Locale locale;

    /** Resource bundle containing localized strings. */
    private ResourceBundle bundle;

    /**
     * Private constructor to prevent external instantiation.
     * Defaults the application language to English.
     */
    private languageController() {
        this.locale = Locale.ENGLISH;
        loadBundle();
    }

    /**
     * Returns the singleton instance of languageController.
     *
     * @return the single languageController instance
     */
    public static languageController getInstance() {
        if (instance == null) {
            instance = new languageController();
        }
        return instance;
    }

    /**
     * Loads the ResourceBundle corresponding to the current locale.
     */
    private void loadBundle() {
        bundle = ResourceBundle.getBundle("ass2.ass2_jfx.QMillionaire", locale);
    }

    /**
     * Sets the application locale and reloads the resource bundle.
     *
     * @param locale the new locale to apply
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        loadBundle();
    }

    /**
     * Retrieves a localized string from the resource bundle.
     *
     * @param key the key associated with the desired string
     * @return the localized string corresponding to the key
     */
    public String getString(String key) {
        return bundle.getString(key);
    }
}