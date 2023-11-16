package app;

import javafx.stage.Screen;
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
import javafx.scene.layout.GridPane;
import javafx.scene.control.DatePicker;
import javafx.util.Duration;

import java.awt.Event;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*; // List

import app.Logger;
import app.Courses;
import app.DbStore;
import app.UI;

public class Note {
    private DbStore dbStore = DbStore.getInstance();
    private Logger logger = new Logger();
    private TilePane cardContainer;
    private TextField courseNameTextField;
    private TextField courseDateTextField;
    private DatePicker courseDatePicker;
    private TextArea newNoteTextArea;
    private Button addBtn;
    private GridPane gridPane;
    private VBox vbox;
    private UI ui;

    public Note(UI ui, TilePane pane) {
        this.ui = ui;
        this.cardContainer = pane;
        init();
    }

    private void createNameTextField() {
        courseNameTextField = new TextField("Course Name");
        courseNameTextField.setMaxWidth(100);
        courseNameTextField.setMaxHeight(50);
        courseNameTextField.setOnMouseClicked((MouseEvent event) -> {
            if(courseNameTextField.getText().equals("Course Name")) {
                courseNameTextField.clear();
            }
            logger.out("Mouse clicked @courseNameTextField");
        });

    }
    private void createDateTextField() {
        courseDateTextField = new TextField("Course Date");
        courseDateTextField.setEditable(false); // NOTE: set this to false to disable editing
        courseDateTextField.getStyleClass().add("unavailable-cursor"); // NOTE: uncomment after setting courseDateTextField to false
        courseDateTextField.setMaxHeight(50);
        courseDateTextField.setMaxWidth(100);
        courseDateTextField.setOnMouseEntered((MouseEvent event) -> {
            courseDateTextField.setText("Please select a date below.");
            logger.out("Mouse clicked @courseDateTextField.");
        });
        courseDateTextField.setOnMouseExited((MouseEvent event) -> {
            courseDateTextField.setText("Course Date");
            logger.out("Mouse clicked @courseDateTextField.");
        });

        courseDateTextField.setOnMouseClicked((MouseEvent event) -> {
            if (courseDateTextField.getText().equals("Course Date")) {
                courseDateTextField.clear();
            }
            logger.out("Mouse clicked @courseDateTextField.");
        });
    }
    private void createCourseDatePicker() {
        courseDatePicker = new DatePicker();
        courseDatePicker.setMaxWidth(25);
        courseDatePicker.setShowWeekNumbers(false);
        courseDatePicker.getEditor().setManaged(false);
        courseDatePicker.getEditor().setVisible(false);
        courseDatePicker.setOnAction(e -> {
            // bind the text property of the textfield to the datepicker
            courseDateTextField.textProperty().bind(courseDatePicker.valueProperty().asString());
            courseDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null) {
                    courseDateTextField.textProperty().unbind();
                    courseDateTextField.setText("");
                }
            });
        });
    }
    private void createNewNoteTextArea() {
        newNoteTextArea = new TextArea("New note...");
        newNoteTextArea.setPrefRowCount(1);
        newNoteTextArea.setEditable(true);
        newNoteTextArea.setMaxHeight(100);
        newNoteTextArea.setMaxWidth(Screen.getPrimary().getVisualBounds().getWidth() / 4);
        newNoteTextArea.textProperty().addListener((observable, oldVal, newVal) -> {
            int row = newNoteTextArea.getText().split("\n").length;
            int col = newNoteTextArea.getText().split(" ").length;
            newNoteTextArea.setPrefRowCount(row);
            newNoteTextArea.setPrefColumnCount(col);
        });
        newNoteTextArea.setOnMouseClicked((MouseEvent event) -> {
            if(newNoteTextArea.getText().equals("New note...")) {
                newNoteTextArea.clear();
            }
            logger.out("Mouse clicked @newNoteTextArea");
        });
    }

    private void createAddButton() {
        addBtn = new Button();
        addBtn.setText("Add Course");
        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String noteContent = newNoteTextArea.getText();
                String courseName = courseNameTextField.getText();
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate courseDate = LocalDate.parse(courseDateTextField.getText(), format);
                Courses newCourse = new Courses(courseName, courseDate, false);
                if (!courseName.isEmpty()) {
                    courseNameTextField.clear();
                    courseDateTextField.clear();
                    courseDatePicker.setValue(null);
                    newNoteTextArea.clear();
                    UI.showMessage(noteContent, Duration.seconds(3));
                    if(dbStore.addData(newCourse)) ui.update(cardContainer, newCourse);
                }
            }
        });
    }

    private void setVbox() {
        vbox = new VBox(8);
        vbox.getChildren().addAll(cardContainer, courseNameTextField, courseDateTextField, courseDatePicker, newNoteTextArea, addBtn);
        vbox.setStyle("-fx-background-color: #dad7cd;");
    }
    private void setGridPane() {
        gridPane = new GridPane();
        gridPane.getChildren().addAll(vbox);
    }
    public VBox getVbox() {
        return vbox;
    }

    private void init() {
        createNameTextField();
        createDateTextField();
        createCourseDatePicker();
        createNewNoteTextArea();
        createAddButton();
        setVbox();
        setGridPane();
    }
}



