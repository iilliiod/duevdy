package duevdy;

import duevdy.AppElement;
import duevdy.DbStore;

public class Settings implements AppElement {
    private final String uuid;
    private static Settings instance = null;

    private Settings() {
        this.uuid = "settings";
    }

    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    enum ProgramTheme {
        LIGHT,
        DARK
    }

    ProgramTheme theme; 

    private ProgramTheme getProgramTheme() {
        return this.theme;
    }

    public void updateSettings(String setting) {
        // check if file exists
        // if not create
        // write current settings
        // close 

    }

    public String getID() {
        return uuid;
    }
    public String toString() {
        return this.getID();
    }
}
