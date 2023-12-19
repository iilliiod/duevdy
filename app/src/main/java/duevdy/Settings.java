package duevdy;

import java.time.LocalDate;

import duevdy.AppElement;
import duevdy.DbStore;

public class Settings implements AppElement {
    private final String uuid;
    private static Settings instance = null;
    private LocalDate dateModified;
    private static ProgramTheme theme; 
    private static ProgramState state;
    private static final Settings INSTANCE = new Settings();

    private Settings() {
        this.uuid = "settings";
        this.dateModified = LocalDate.now();
        state = ProgramState.TODO;
        theme = ProgramTheme.LIGHT;
    }

    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public static void setTheme(ProgramTheme theme) {
        Settings.theme = theme;
    }

    private static ProgramTheme getProgramTheme() {
        return theme;
    }
    public LocalDate getDate() {
        return dateModified;
    }
    public static ProgramState getState() {
        return state;
    }
    public static void setState(ProgramState state) {
        Settings.state = state;
    }

    public void updateSettings(String settings) {
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
