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

    @Override
    public void start(Stage primaryStage) {
        this.ui = new UI(primaryStage, this.dbStore);
        this.ui.init();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
