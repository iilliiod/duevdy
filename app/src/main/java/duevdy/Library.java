package duevdy;

import javafx.scene.input.MouseButton;
import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.Timeline;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import java.util.concurrent.CompletableFuture;

public class Library {

    public static void createTooltip(Node node, String prompt) {
        Tooltip tooltip = new Tooltip(prompt);
        Tooltip.install(node, tooltip);
    }

    public static void showMessage(String message, double seconds) {
        Alert msg = new Alert(AlertType.INFORMATION);
        msg.setHeaderText(null);
        msg.setContentText(message);

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(seconds));
        pause.setOnFinished(e -> msg.hide());

        msg.show();
        pause.play();
    }
    public static CompletableFuture<Boolean> showConfirmationDialog(String message) {
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setHeaderText(null);
        dialog.setContentText(message);

        ButtonType yesBtn = new ButtonType("Yes", ButtonData.YES);
        ButtonType noBtn = new ButtonType("No", ButtonData.NO);

        dialog.getDialogPane().getButtonTypes().addAll(noBtn, yesBtn);

        CompletableFuture<Boolean> confirmed = new CompletableFuture<>();
        dialog.showAndWait().ifPresent(response -> {
            if (response == yesBtn) {
                confirmed.complete(true);
            } else if (response == noBtn) {
                confirmed.complete(false);
            } else {
                confirmed.complete(false);
            }
        });
        return confirmed;
    }

    public static void createScaleTransition(Node node, double seconds) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(seconds), node);
        scaleTransition.setFromX(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToY(1.2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(Timeline.INDEFINITE);
        scaleTransition.setInterpolator(Interpolator.SPLINE(0.25, 0.1, 0.25, 1.0));
        scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

        // Start the animation on hover
        node.setOnMouseEntered(event -> scaleTransition.play());

        // Stop the animation when the mouse exits the node
        node.setOnMouseExited(event -> {
            scaleTransition.stop();
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });
    }
    public static void createSelectScaleTransition(Node node, double seconds) {
        Boolean selectMode = false;
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(seconds), node);
        scaleTransition.setFromX(1.0);
        scaleTransition.setToX(1.075);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToY(1.075);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(Timeline.INDEFINITE);
        scaleTransition.setInterpolator(Interpolator.SPLINE(0.25, 0.1, 0.25, 1.0));
        scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

        if(!selectMode) {
            scaleTransition.play();
            selectMode = true;
        } else {
            node.setOnMouseClicked(event -> {
                scaleTransition.stop();
                node.setScaleX(1.0);
                node.setScaleY(1.0);
            });
        }
    }

    public static void fadeOutTransition(Node node) {
        // Set initial opacity
        node.setOpacity(0.8);

        // Create a FadeTransition for the fade out animation
        FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(0.75), node);
        fadeOutTransition.setFromValue(0.8);
        fadeOutTransition.setToValue(0.0);
        fadeOutTransition.play();
    }

    public static void fadeInTransition(Node node) {
        // Set initial opacity
        node.setOpacity(0.0);

        // Create a FadeTransition for the fade in animation
        FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(0.75), node);
        fadeInTransition.setFromValue(0.0);
        fadeInTransition.setToValue(0.8);
        fadeInTransition.play();
    }
}
