package duevdy;

import javafx.scene.Scene;
import duevdy.UI;

public enum ProgramTheme {

    LIGHT {
        @Override
        public void setTheme(Scene scene) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(UI.class.getResource("/light-mode.css").toExternalForm());
        }
    },
    DARK {
        @Override
        public void setTheme(Scene scene) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(UI.class.getResource("/dark-mode.css").toExternalForm());
        }
    };

    public abstract void setTheme(Scene scene);
}
