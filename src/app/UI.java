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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.TilePane;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;

import java.awt.Event;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*; // List

import app.DbStore;
import app.Courses;
import app.Controller;
import app.Logger;
import app.Card;

public class UI {
    private Stage stage;
    private ScrollPane scrollPane;
    // private TilePane container;
    private DbStore dbStore;
    private Logger logger = new Logger();
    private int cardWidth = 150;
    private int cardHeight = 100;
    private Card card;
    private final LocalDate dateToday = LocalDate.now();

    UI(Stage stage, DbStore dbStore) {
        this.stage = stage;
        this.dbStore = dbStore; // TODO : make Singleton
    }

    public void init() {
        TilePane cardContainer = new TilePane();
        cardContainer.setVgap(10);
        cardContainer.setHgap(10);
        cardContainer.setPadding(new Insets(20));

        load(cardContainer);

        TextField courseNameTextField = new TextField("Course Name");
        TextField courseDateTextField = new TextField("Course Date");
        courseDateTextField.setEditable(true); // NOTE: set this to false to disable editing
        // courseDateTextField.getStyleClass().add("unavailable-cursor"); // NOTE: uncomment after setting courseDateTextField to false
        courseNameTextField.setMaxWidth(100);
        courseNameTextField.setMaxHeight(50);
        courseDateTextField.setMaxWidth(100);
        courseDateTextField.setMaxHeight(50);


        courseNameTextField.setOnMouseClicked((MouseEvent event) -> {
            if(courseNameTextField.getText().equals("Course Name")) {
                courseNameTextField.clear();
            }
            logger.out("Mouse clicked");
        });
        // courseDateTextField.setOnMouseEntered((MouseEvent event) -> {
        //     courseDateTextField.setText("Aurelius.");
        //     logger.out("Mouse clicked @courseDateTextField.");
        // });

        courseDateTextField.setOnMouseClicked((MouseEvent event) -> {
            if (courseDateTextField.getText().equals("Course Date")) {
                courseDateTextField.clear();
            }
            logger.out("Mouse clicked @courseDateTextField.");
        });

        Button btn = new Button();
        btn.setText("Add Course");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String courseName = courseNameTextField.getText();
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate courseDate = LocalDate.parse(courseDateTextField.getText(), format);
                Courses newCourse = new Courses(courseName, courseDate, false);
                if (!courseName.isEmpty()) {
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
        scrollPane = new ScrollPane(gridPane);

        Scene scene = new Scene(scrollPane, 50, 50);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("duevdy");
        stage.setScene(scene);
        stage.show();

    }

    private void load(TilePane container) {
        dbStore.addData(new Courses("Test", this.dateToday, false));
        dbStore.addData(new Courses("Test!", this.dateToday, false));
        dbStore.addData(new Courses("Test2", this.dateToday, true));

        for (Courses c : dbStore.queryData()) {
            Courses course = c;
            card = new Card(course);
            card.init();
            // courseNameTextField.setOnMouseExited((MouseEvent event) -> {
            // course.setName(courseNameTextField.getText());
            // });
            // courseDateTextField.setOnMouseExited((MouseEvent event) -> {
            // course.setDueDate(courseDateTextField.getText());
            // });

            container.getChildren().addAll(card.getCardHbox());
            logger.out(course.toString());
        }
    }

    private void update(TilePane container, Courses course) {
        card = new Card(course);
        card.init();

        container.getChildren().addAll(card.getCardHbox());
        logger.out(course.toString());
    }
}
