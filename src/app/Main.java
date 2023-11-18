package app;

import javafx.application.Application;
import javafx.stage.Stage;
import app.Courses;
import app.DbStore;
import app.UI;
import app.Controller;

public class Main extends Application {
    private DbStore dbStore = DbStore.getInstance(); 
    private UI ui;
    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);

        this.ui = new UI(primaryStage, this.dbStore);
        this.ui.init();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
