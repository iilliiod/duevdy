package app;

import javafx.stage.Screen;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import javafx.scene.control.TextArea;
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
import javafx.util.Duration;

import java.awt.Event;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*; // List

import app.DbStore;
import app.Courses;
import app.Controller;
import app.Logger;
import app.Card;
import app.Note;

public class UI {
    private Stage stage;
    private ScrollPane scrollPane;
    private DbStore dbStore = DbStore.getInstance();
    private Logger logger = new Logger();
    private int cardWidth = 150;
    private int cardHeight = 100;
    private Card card;
    private Note note;
    private final LocalDate dateToday = LocalDate.now();

    UI(Stage stage, DbStore dbStore) {
        this.stage = stage;
    }

    // handy little function lolz
    public static void showMessage(String message, Duration duration) {
        Alert msg = new Alert(AlertType.INFORMATION);
        msg.setHeaderText(null);
        msg.setContentText(message);

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(duration);
        pause.setOnFinished(e -> msg.hide());

        msg.show();
        pause.play();
    }

    public void init() {
        // set up main container
        TilePane cardContainer = new TilePane();
        cardContainer.setVgap(10);
        cardContainer.setHgap(10);
        cardContainer.setPadding(new Insets(20));

        load(cardContainer);

        note = new Note(this, cardContainer);

        scrollPane = new ScrollPane(note.getVbox());
        Scene scene = new Scene(scrollPane, 50, 50);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("duevdy");
        stage.setScene(scene);
        stage.show();

    }

    private void load(TilePane container) {
        // TODO: uncomment when store empty
        // dbStore.addData(new Courses("Test", this.dateToday, false));
        // dbStore.addData(new Courses("Test!", this.dateToday, false));
        // dbStore.addData(new Courses("Test2", this.dateToday, true));

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

    public void update(TilePane container, Courses course) {
        card = new Card(course);
        card.init();

        container.getChildren().addAll(card.getCardHbox());
        logger.out(course.toString());
    }
}
