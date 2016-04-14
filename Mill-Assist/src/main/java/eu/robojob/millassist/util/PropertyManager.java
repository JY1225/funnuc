package eu.robojob.millassist.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class is responsible for reading the settings.properties file. All possible settings are defined in the enum Setting.
 * The class is a final class that thus cannot be extended. There is one big method, namely the static method readPropertyFile which
 * should be called when the application is started.
 */
public final class PropertyManager {

    //File containing the mapping between property and value
    private static final File propertiesFile = new File("settings.properties");
    private static final Properties properties = new Properties();

    public enum Setting {

        MOUSE_VISIBLE("mouse-visible"),
        LANGUAGE("locale"),
        KEYBOARD("keyboard-type"),
        MEMORY("monitor-memory"),
        CLAMP_ORIENTATION("use-clamp-orientation"),
        AIRBLOW("robot-airblow"),
        TITLEBAR("title-bar"),
        ALIGN_RIGHT("align-right"),
        CUSTOM_POS("to-custom-pos"),
        REVERSAL_WIDTH("reversal-width"),
        SINGLE_CYCLE("single-cycle"),
        CLAMPING_MANNER_ALLOWED("clamping-manner-allowed"),
        CONVEYOR_SETUP("conveyor-setup"),
        SIDE_LOAD("side-load"),
        ROUND_PIECES("round-pieces"),
        FULL_SCREEN("full-screen"),
        SCREEN_WIDTH("screen-width"),
        SCREEN_HEIGHT("screen-height"),
        EMAIL_HOST("email-host"),
        EMAIL_PORT("email-port"),
        EMAIL_USERNAME("email-username"),
        EMAIL_PASSWORD("email-password"),
        FROM_EMAIL_ADDRESS("from-email-address"),
        EMAIL_OPTION("has-email-option");

        private String settingsText;

        Setting (final String settingsText) {
            this.settingsText = settingsText;
        }

        public String getSettingsText() {
            return this.settingsText;
        }
    }

    private PropertyManager() {
        //Do nothing
    }

    /**
     * Read all the properties from the settings file and store them in a map.
     */
    public static final void readPropertyFile() {
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Check whether the requested setting has the given value. In case the given setting is not part of the properties
     * file, than the result will always be false.
     *
     * @param setting
     * @param value
     * @return
     */
    public static final boolean hasSettingValue(final Setting setting, final String value) {
        if (properties.containsKey(setting.getSettingsText()) && properties.getProperty(setting.getSettingsText()).equals(value)) {
            return true;
        }
        return false;
    }

    public static final String getValue(final Setting setting) {
        return properties.getProperty(setting.getSettingsText());
    }

    public static final void addProperty(final String key, final String value) {
        properties.put(key, value);
    }

}
