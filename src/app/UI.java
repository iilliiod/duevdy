package app;

import javafx.scene.layout.GridPane;
import javafx.application.Application;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.TilePane;
import java.util.*; // List

import app.DbStore;
import app.Courses;
import app.Controller;
import app.Logger;

public class UI {
    private Stage stage;
    private TilePane container;
    private DbStore dbStore;
    private Logger logger = new Logger();

    UI(Stage stage, DbStore dbStore) {
        this.stage = stage;
        this.dbStore = dbStore;
    }

    public void init() {
        TilePane cardContainer = new TilePane();
        cardContainer.setVgap(10);
        cardContainer.setHgap(10);
        cardContainer.setPadding(new Insets(20));
        cardContainer.setStyle("-fx-background-color: #dad7cd;");

        load(cardContainer);

        TextField courseNameTextField = new TextField("Course Name");
        TextField courseDateTextField = new TextField("Course Date");

        courseNameTextField.setOnMouseClicked((MouseEvent event) -> {
            if(courseNameTextField.getText().equals("Course Name")) {
                courseNameTextField.clear();
            }
            //logger.out.println("Mouse clicked");
            logger.out("Mouse clicked");
        });
        courseDateTextField.setOnMouseClicked((MouseEvent event) -> {
            if(courseDateTextField.getText().equals("Course Date")) {
                courseDateTextField.clear();
            }
            logger.out("Mouse clicked");
        });

        Button btn = new Button();
        btn.setText("Add Course");
        btn.setPadding(new Insets(20));
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String courseName = courseNameTextField.getText();
                String courseDate = courseDateTextField.getText();
                Courses newCourse = new Courses(courseName, courseDate, false);
                if (!courseName.isEmpty() && !courseDate.isEmpty()) {
                    courseNameTextField.clear();
                    courseDateTextField.clear();
                    if(dbStore.addData(newCourse)) update(cardContainer, newCourse);
                }
            }
        });

        GridPane gridPane = new GridPane();

        VBox vbox = new VBox(8);
        vbox.getChildren().addAll(cardContainer, courseNameTextField, courseDateTextField, btn);
        vbox.setStyle("-fx-background-color: #dad7cd;");

        gridPane.getChildren().addAll(vbox);

        Scene scene = new Scene(gridPane, 300, 250);

        stage.setTitle("duevdy");
        stage.setScene(scene);
        stage.show();

    }

    private void load(TilePane container) {
        dbStore.addData(new Courses("Test", "2023/01/01", false));
        dbStore.addData(new Courses("Test!", "2023/01/01", false));
        dbStore.addData(new Courses("Test2", "2023/01/01", true));

        for(Courses c : dbStore.queryData()) {
            Courses course = c;
            String labelName = course.getName();
            Rectangle cardBG = new Rectangle(50,50);
            cardBG.setFill(Color.web("#a3b18a"));
            Label cardContent = new Label(labelName);
            cardContent.setTextFill(Color.WHITE);
            cardContent
                    .setStyle("-fx-font-family: Helvetica, Arial, sans-serif; -fx-font-size: 14px; -fx-padding: 10px;");
            StackPane cardPane = new StackPane(cardBG, cardContent);
            cardPane.setStyle("-fx-background-color: #dad7cd; -fx-border-radius: 5px;");

            VBox radioBox = new VBox(10);
            radioBox.setPadding(new Insets(10));
            ToggleGroup group = new ToggleGroup();
            RadioButton btnCompleted = new RadioButton("completed");
            RadioButton btnIncomplete = new RadioButton("incomplete");
            btnCompleted.setToggleGroup(group);
            btnIncomplete.setToggleGroup(group);
            int val = course.getCompleted() ? 1 : 0;
            switch (val) {
                case 1:
                    btnCompleted.setSelected(true);
                    break;
                default:
                    btnIncomplete.setSelected(true);
                    break;
            }
            radioBox.getChildren().addAll(btnCompleted, btnIncomplete);
            HBox cardHbox = new HBox(cardPane, radioBox);

            container.getChildren().addAll(cardHbox);
            logger.out(course.toString());
        }
    }

    private void update(TilePane container, Courses course) {
        String labelName = course.getName();
        Rectangle cardBG = new Rectangle(50,50);
        cardBG.setFill(Color.web("#a3b18a"));
        Label cardContent = new Label(labelName);
        cardContent.setTextFill(Color.WHITE);
        cardContent
                .setStyle("-fx-font-family: Helvetica, Arial, sans-serif; -fx-font-size: 14px; -fx-padding: 10px;");
        StackPane cardPane = new StackPane(cardBG, cardContent);
        cardPane.setStyle("-fx-background-color: #dad7cd; -fx-border-radius: 5px;");

        VBox radioBox = new VBox(10);
        radioBox.setPadding(new Insets(10));
        ToggleGroup group = new ToggleGroup();
        RadioButton btnCompleted = new RadioButton("completed");
        RadioButton btnIncomplete = new RadioButton("incomplete");
        btnCompleted.setToggleGroup(group);
        btnIncomplete.setToggleGroup(group);
        int val = course.getCompleted() ? 1 : 0;
        switch (val) {
            case 1:
                btnCompleted.setSelected(true);
                break;
            default:
                btnIncomplete.setSelected(true);
                break;
        }
        radioBox.getChildren().addAll(btnCompleted, btnIncomplete);

        HBox cardHbox = new HBox(cardPane, radioBox);

        container.getChildren().addAll(cardHbox);
        logger.out(course.toString());
    }
}