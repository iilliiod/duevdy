package app;

import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Node;
import javafx.util.Duration;

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

    // Create a scale transition
    public static void createScaleTransition(Node node, double seconds) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(seconds), node);
        scaleTransition.setFromX(1.0);
        scaleTransition.setToX(1.5);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToY(1.5);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);

        // Start the animation on hover
        node.setOnMouseEntered(event -> scaleTransition.play());

        // Stop the animation when the mouse exits the node
        node.setOnMouseExited(event -> {
            scaleTransition.stop();
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });
    }

    public static void fadeTransition(Image image1, Image image2, ImageView imageView) {
        // Set initial opacity
        imageView.setOpacity(1.0);

        // Create a FadeTransition for the fade out animation
        FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(0.75), imageView);
        fadeOutTransition.setFromValue(1.0);
        fadeOutTransition.setToValue(0.0);
        fadeOutTransition.setOnFinished(event -> {
            // Switch to the new image once the fade out animation is complete
            imageView.setImage(image2);

            // Create a FadeTransition for the fade in animation
            FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(0.75), imageView);
            fadeInTransition.setFromValue(0.0);
            fadeInTransition.setToValue(1.0);
            fadeInTransition.play();
        });

        // Start the fade out transition
        fadeOutTransition.play();
    }
}
