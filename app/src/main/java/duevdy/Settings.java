package duevdy;

import java.time.LocalDate;

import duevdy.AppElement;
import duevdy.DbStore;

public class Settings implements AppElement {
    private final String uuid;
    private static Settings instance = null;
    private LocalDate dateModified;

    private Settings() {
        this.uuid = "settings";
        this.dateModified = LocalDate.now();
    }

    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }


    ProgramTheme theme; 

    private ProgramTheme getProgramTheme() {
        return this.theme;
    }
    public LocalDate getDate() {
        return dateModified;
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
