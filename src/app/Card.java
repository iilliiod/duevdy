package app;

import app.Courses;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.TilePane;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.awt.Event;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*; // List

public class Card {
    private String courseName;
    private LocalDate courseDate;
    private int cardWidth = 150;
    private int cardHeight = 100;
    private Courses course;
    private TextField courseNameTextField;
    private TextField courseDateTextField;
    private DatePicker datePicker;
    private VBox checkBox;
    private VBox cardBox;
    private StackPane cardPane;
    private HBox cardHbox;
    private Rectangle cardBG = new Rectangle(this.cardWidth, this.cardHeight);

    public Card(Courses course) { //TODO : add constructor
        this.course = course;
    } 

    private TextField setCourseNameTextField (String courseName) {
        courseNameTextField = new TextField(courseName);
        courseNameTextField.setStyle("-fx-background-color: transparent;");
        return courseNameTextField;
    }

    private TextField setCourseDateTextField (LocalDate courseDate) {
        courseDateTextField = new TextField(courseDate.toString());
        courseDateTextField.setStyle("-fx-background-color: transparent;");
        return courseDateTextField;
    }

    public TextField getCourseNameTextField() {
        return courseNameTextField;
    }
    public TextField getCourseDateTextField() {
        return courseDateTextField;
    }

    private void setTextFields() {
        courseName = course.getName();
        courseDate = course.getDueDate();
        cardBG.setFill(Color.web("#a3b18a"));
        setCourseNameTextField(courseName);
        setCourseDateTextField(courseDate);
    }
    
    private void setDatePicker() {
        datePicker = new DatePicker();
        datePicker.getStyleClass().add("no-prompt");
        datePicker.setPromptText(courseDate.toString());
        datePicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            }
        });
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    private void setCheckBox() {
        checkBox = new VBox(10);
        checkBox.setPadding(new Insets(10));
        CheckBox checkCompleted = new CheckBox("completed");
        checkCompleted.setIndeterminate(false);
        int val = course.getCompleted() ? 1 : 0;
        switch (val) {
            case 1:
                checkCompleted.setSelected(true);
                course.setCompleted(true);
                break;
            default:
                checkCompleted.setSelected(false);
                course.setCompleted(false);
                break;
        }
        checkBox.getChildren().add(checkCompleted);
    }

    private void setCardBox() {
        cardBox = new VBox();
        cardBox.getChildren().addAll(getCourseNameTextField(), getCourseDateTextField(), getCheckBox());
    }
    
    public VBox getCheckBox() {
        return checkBox;
    }

    private void setCardPane() {
        cardPane = new StackPane();
        cardPane.getChildren().addAll(cardBG, cardBox);
        cardPane.setAlignment(cardBG, Pos.TOP_LEFT);
        cardPane.setAlignment(cardBox, Pos.CENTER_RIGHT);

        cardHbox = new HBox(cardPane, datePicker);
    }

    public HBox getCardHbox() {
        return cardHbox;
    }


    public void init() {
        setTextFields();
        setCheckBox();
        setDatePicker();
        setCardBox();
        setCardPane();
    }

}
