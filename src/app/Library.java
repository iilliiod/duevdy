package app;

import javafx.scene.control.Tooltip;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;

public class Library {

    public static void createTooltip(TextInputControl field, String prompt) {
        Tooltip tooltip = new Tooltip(prompt);
        Tooltip.install(field, tooltip);
    }

    public static void showMessage(String message, Duration duration) {
        Alert msg = new Alert(AlertType.INFORMATION);
        msg.setHeaderText(null);
        msg.setContentText(message);

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(duration);
        pause.setOnFinished(e -> msg.hide());

        msg.show();
        pause.play();
    }
}
