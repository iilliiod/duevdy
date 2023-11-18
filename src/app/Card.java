package app;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.TilePane;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.IOException;

import java.awt.Event;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*; // List

import app.Courses;
import app.Logger;

public class Card {
    private Logger logger = new Logger();
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
    private Button delBtn;
    private Button datePickerBtn;
    private StackPane cardPane;
    private HBox cardHbox;
    private Rectangle cardBG = new Rectangle(this.cardWidth, this.cardHeight);
    private Image checkedBoxIcon = new Image("/img/checked-checkbox.png");
    private Image uncheckedBoxIcon = new Image("/img/unchecked-checkbox.png");
    private CheckBox checkCompleted = new CheckBox();

    public Card(Courses course) { //TODO : add constructor
        this.course = course;
    } 

    private TextField setCourseNameTextField (String courseName) {
        courseNameTextField = new TextField(courseName);
        courseNameTextField.setStyle("-fx-background-color: transparent;");
        courseNameTextField.setId("course-name-field");
        return courseNameTextField;
    }

    private TextField setCourseDateTextField (LocalDate courseDate) {
        courseDateTextField = new TextField(courseDate.toString());
        courseDateTextField.setStyle("-fx-background-color: transparent;");
        courseDateTextField.setEditable(false); // NOTE: set this to false to disable editing
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
        cardBG.setId("cardBG");
        setCourseNameTextField(courseName);
        setCourseDateTextField(courseDate);

        // Update courseName if changed
        courseNameTextField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if(!isFocused) {
                String newName = courseNameTextField.getText();
            // logger.out("updating courseName value...");
                if(!newName.equals(course.getName())) {
                    System.out.println("updating courseName value...");
                    course.setName(newName);
                    try {
                        DbStore.getInstance().update(course);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                logger.out("ERROR: updating courseName");
            }
        });
    }
    
    private void setDatePicker() {
        datePicker = new DatePicker();
        datePicker.setPromptText(courseDate.toString());
        datePicker.getStyleClass().add("no-prompt");
        datePicker.setMaxWidth(5);
        datePicker.setShowWeekNumbers(false);
        datePicker.getEditor().setDisable(true);
        datePicker.getEditor().setVisible(false);
        datePicker.setVisible(false);
        datePicker.getStyleClass().add("date-picker");

        // Update courseDate based on the selected date
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            logger.out("updating datePicker value...");
            if (newVal != null) {
                courseDate = newVal;
                try {
                    course.setDueDate(courseDate);
                    DbStore.getInstance().update(course);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                logger.out("ERROR: updating datePicker");
            }
            courseDateTextField.textProperty().bind(datePicker.valueProperty().asString());
        });

        // might be dead code i think lolz
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                logger.out("unbinding datePicker");
                courseDateTextField.textProperty().unbind();
                courseDateTextField.setText("");
            }
        });
    }

    private void createDatePickerBtn() {
        Image createDatePickerIcon = new Image(getClass().getResourceAsStream("/img/date-picker-icon.png"));
        ImageView datePickereteIconView = new ImageView(createDatePickerIcon);
        datePickereteIconView.getStyleClass().add("image-btn");
        datePickereteIconView.setFitWidth(10);
        datePickereteIconView.setFitHeight(10);
        datePickerBtn = new Button();
        datePickerBtn.setOnAction(event -> {
            datePicker.show();
            System.out.println("clicked datePickerIcon");
        });
        datePickerBtn.setId("datePickerBtn");
        datePickerBtn.setGraphic(datePickereteIconView);
        Library.createScaleTransition(datePickerBtn, 1.0);
        Library.createTooltip(datePickerBtn, "Yes, this changes the date.");
    }

    private void createDelBtn() {
        Image deleteIcon = new Image(getClass().getResourceAsStream("/img/del-icon2.png"));
        ImageView deleteIconView = new ImageView(deleteIcon);
        deleteIconView.getStyleClass().add("image-btn");
        deleteIconView.setFitWidth(10);
        deleteIconView.setFitHeight(10);
        delBtn = new Button();
        delBtn.setOnAction(event -> {
            Library.showMessage("Are you sure you want to delete this?\nThere's no coming back from this...", 5);
            System.out.println("clicked delete");
        });
        delBtn.setId("delBtn");
        delBtn.setGraphic(deleteIconView);
        Library.createScaleTransition(delBtn, 1.0);
        Library.createTooltip(delBtn, "You know what a delete button does, right?");
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    private void setCheckBox() {
        checkBox = new VBox(10);
        checkBox.setPadding(new Insets(10));
        checkCompleted.setIndeterminate(false);
        checkCompleted.getStyleClass().add("checkbox");

        checkCompleted.setSelected(course.getCompleted());
        setCheckBoxIcon();

        checkCompleted.setOnAction(event -> {
            boolean selected = checkCompleted.isSelected();
            course.setCompleted(selected);
            try {
                if (selected) {
                    logger.out("set to: completed");
                    // System.out.println("set to: completed");

                } else {
                    logger.out("set to: not completed");
                    // System.out.println("set to: not completed");
                }
                DbStore.getInstance().update(course);
                setCheckBoxIcon();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        checkBox.getChildren().add(checkCompleted);
    }

    private void setCheckBoxIcon() {
        ImageView iconView = new ImageView(checkCompleted.isSelected() ? checkedBoxIcon : uncheckedBoxIcon);
        iconView.getStyleClass().add("image-btn");
        iconView.setFitWidth(16);
        iconView.setFitHeight(16);
        if(checkCompleted.isSelected()) {
            course.setCompleted(true);
            Library.fadeTransition(uncheckedBoxIcon, checkedBoxIcon, iconView);
        } else {
            course.setCompleted(false);
            Library.fadeTransition(checkedBoxIcon, uncheckedBoxIcon, iconView);
        }
        checkCompleted.setGraphic(iconView);
    }

    private void setCardBox() {
        cardBox = new VBox();
        cardBox.getChildren().addAll(getCourseNameTextField(), getCourseDateTextField(), getCheckBox());
        cardBox.setId("card-box");
    }
    
    public VBox getCheckBox() {
        return checkBox;
    }

    private void setCardPane() {
        cardPane = new StackPane();
        cardPane.getChildren().addAll(cardBG, cardBox);
        cardPane.setAlignment(cardBG, Pos.TOP_LEFT);
        cardPane.setAlignment(cardBox, Pos.CENTER_RIGHT);
        cardPane.setId("card-pane");

        cardHbox = new HBox(cardPane, datePicker, datePickerBtn, delBtn);
        cardHbox.setId("card-hbox");
        checkCompleted.setSelected(course.getCompleted());
        cardHbox.setOnMouseClicked(event -> {
            boolean selected = checkCompleted.isSelected();
            try {
                System.out.println(selected);
                if (selected) {
                    course.setCompleted(false);
                    checkCompleted.setSelected(false);
                    logger.out("set to: not completed");
                    // System.out.println("set to: completed");

                } else {
                    course.setCompleted(true);
                    checkCompleted.setSelected(true);
                    logger.out("set to: completed");
                    // System.out.println("set to: not completed");
                }
                DbStore.getInstance().update(course);
                setCheckBoxIcon();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public HBox getCardHbox() {
        return cardHbox;
    }


    public void init() {
        setTextFields();
        setCheckBox();
        setDatePicker();
        createDelBtn();
        createDatePickerBtn();
        setCardBox();
        setCardPane();
    }

}
