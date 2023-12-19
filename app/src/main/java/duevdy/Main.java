package duevdy;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private UI ui;
    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);

        this.ui = new UI(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
