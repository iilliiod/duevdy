package duevdy;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.DatePicker;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import javafx.collections.FXCollections;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Card {
    private Logger logger = new Logger();
    private String todoName;
    private LocalDate todoDate;
    private int cardWidth = 150;
    private int cardHeight = 100;
    private Todo todo;
    private TextField todoNameTextField;
    private TextField todoDateTextField;
    private DatePicker datePicker;
    private VBox checkBox;
    private VBox cardBox;
    private Button delBtn;
    private Button datePickerBtn;
    private StackPane cardPane;
    private HBox cardHbox;
    private Rectangle cardBG = new Rectangle(this.cardWidth, this.cardHeight);
    // private Rectangle cardHover = new Rectangle(this.cardWidth, this.cardHeight);
    private CheckBox checkCompleted = new CheckBox();
    private StackPane cardContent = new StackPane();
    private Pane container;
    private FontIcon addIcon = new FontIcon("mdi-plus");
    private FontIcon datePickerIcon = new FontIcon("mdi-calendar-text");
    private FontIcon newTodoDatePickerIcon = new FontIcon("mdi-calendar-text");
    private FontIcon deleteIcon = new FontIcon("mdi-delete");
    private FontIcon unCheckedBoxIcon = new FontIcon("mdi-checkbox-blank-circle-outline");
    private FontIcon checkedBoxIcon = new FontIcon("mdi-checkbox-marked-circle");

    // public enum HoverState {
    //     NORMAL,
    //     HOVERED,
    //     DISABLED
    // }
    //
    // private HoverState hoverState = HoverState.NORMAL;

    private TextField newTodoNameTextField;
    private TextField newTodoDateTextField;
    private DatePicker newTodoDatePicker;
    private Button addBtn;
    private VBox newTodoLayout;
    private int todoViewInstance = 0;
    private Button newTodoDatePickerBtn = new Button();

    public Card(Pane container, Todo todo) {
        setTodo(todo);
        setContainer(container);
        init();
    }

    private void setTodo(Todo todo) {
        this.todo = todo;
    }

    private void setContainer(Pane container) {
        this.container = container;
    }

    private TextField setTodoNameTextField(String todoName) {
        todoNameTextField = new TextField(todoName);
        todoNameTextField.setId("todo-name-field");
        return todoNameTextField;
    }

    private TextField setTodoDateTextField(LocalDate todoDate) {
        todoDateTextField = new TextField(todoDate.toString());
        todoDateTextField.setStyle("-fx-background-color: transparent;");
        todoDateTextField.setEditable(false); // NOTE: set this to false to disable editing
        return todoDateTextField;
    }

    public TextField getTodoNameTextField() {
        return todoNameTextField;
    }

    public TextField getTodoDateTextField() {
        return todoDateTextField;
    }

    private void setTextFields() {
        todoName = todo.getName();
        todoDate = todo.getDate();
        cardBG.setId("cardBG");
        // TODO: rethink the hover functionality
        // cardHover.setId("cardHover");
        // cardHover.setOpacity(0);
        // cardHover.setOnMouseEntered(event -> {
        //     System.out.println("in the event");
        //     Library.cardHoverTransition(cardHover, cardBG);
        // });
        // if(!cardContent.getChildren().contains(cardBG) &&
        // !cardContent.getChildren().contains(cardHover)) {
        // cardContent.getChildren().clear();
        // }
        cardContent.getChildren().add(cardBG);
        setTodoNameTextField(todoName);
        setTodoDateTextField(todoDate);
        cardContent.setId("card-content");

        // Update todoName if changed
        todoNameTextField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                System.out.println("in here!!!");
                String newName = todoNameTextField.getText();
                // logger.out("updating todoName value...");
                if (!newName.equals(todo.getName())) {
                    System.out.println("updating todoName value...");
                    todo.setName(newName);
                    try {
                        DbStore.getInstance().update(todo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                logger.out("ERROR: updating todoName");
            }
        });
    }

    private void setDatePicker() {
        datePicker = new DatePicker();
        datePicker.setPromptText(todoDate.toString());
        datePicker.getStyleClass().add("no-prompt");
        datePicker.setMaxWidth(5);
        datePicker.setShowWeekNumbers(false);
        datePicker.getEditor().setDisable(true);
        datePicker.getEditor().setVisible(false);
        datePicker.setVisible(false);
        datePicker.getStyleClass().add("date-picker");

        // Update todoDate based on the selected date
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            logger.out("updating datePicker value...");
            if (newVal != null) {
                todoDate = newVal;
                try {
                    todo.setDueDate(todoDate);
                    DbStore.getInstance().update(todo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                logger.out("ERROR: updating datePicker");
            }
            todoDateTextField.textProperty().bind(datePicker.valueProperty().asString());
        });

        // most likely be dead code
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                logger.out("unbinding datePicker");
                todoDateTextField.textProperty().unbind();
                todoDateTextField.setText("");
            }
        });
    }

    private void createDatePickerBtn() {
        datePickerBtn = new Button();
        datePickerBtn.setOnAction(event -> {
            datePicker.show();
            System.out.println("clicked datePickerIcon");
        });
        datePickerBtn.setId("date-picker-btn");
        datePickerBtn.setGraphic(datePickerIcon);
        Library.createScaleTransition(datePickerBtn, 1.0);
        Library.createTooltip(datePickerBtn, "Yes, this changes the date.");
    }

    private void createDelBtn() {
        delBtn = new Button();
        delBtn.setOnAction(event -> {
            Library.showConfirmationDialog("Are you sure you want to delete this?\nThere's no coming back from this...")
                    .thenAccept(result -> {
                        if (result) {
                            System.out.println("clicked delete on " + todo.toString());
                            DbStore.getInstance().deleteTodo(todo);
                            container.getChildren().remove(cardHbox);
                        }
                    });
        });
        delBtn.setId("del-btn");
        delBtn.setGraphic(deleteIcon);
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

        checkCompleted.setSelected(todo.getCompleted());
        setCheckBoxIcon();

        checkBox.getChildren().add(checkCompleted);
    }

    private void setCheckBoxIcon() {
        // TODO : change box color!
        if (checkCompleted.isSelected()) {
            System.out.println("int it to " + DbStore.getInstance().getCompletedTodoCnt());
            DbStore.getInstance().setCompletedTodoCnt();
            todo.setCompleted(true);
            cardContent.setId("card-complete");
            // Library.fadeTransition(uncheckedBoxIcon, checkedBoxIcon, iconView);
        } else {
            todo.setCompleted(false);
            System.out.println("winner " + DbStore.getInstance().getCompletedTodoCnt());
            cardContent.setId("card-incomplete");
            // Library.fadeTransition(checkedBoxIcon, uncheckedBoxIcon, iconView);
        }
        checkCompleted.setGraphic(checkCompleted.isSelected() ? checkedBoxIcon : unCheckedBoxIcon);
    }

    private void setCardBox() {
        cardBox = new VBox();
        cardBox.getChildren().addAll(getTodoNameTextField(), getTodoDateTextField(), getCheckBox());
        cardBox.setId("card-box");
    }

    public VBox getCheckBox() {
        return checkBox;
    }

    private void setCardPane() {
        cardPane = new StackPane();
        cardPane.getChildren().addAll(cardContent, cardBox);
        StackPane.setAlignment(cardBG, Pos.TOP_LEFT);
        StackPane.setAlignment(cardBox, Pos.CENTER_RIGHT);
        cardPane.setId("card-pane");

        StackPane datePickerElements = new StackPane(datePicker, datePickerBtn);
        cardHbox = new HBox(cardPane, datePickerElements, delBtn);
        cardHbox.setId("card-hbox");
        checkCompleted.setSelected(todo.getCompleted());
        cardHbox.setOnMouseClicked(event -> {
            boolean selected = checkCompleted.isSelected();
            try {
                System.out.println(selected);
                if (selected) {
                    todo.setCompleted(false);
                    checkCompleted.setSelected(false);
                    System.out.println("in ???");
                    DbStore.getInstance().setIncompletedTodoCnt();
                    logger.out("set to: not completed");
                    // System.out.println("set to: completed");

                } else {
                    todo.setCompleted(true);
                    checkCompleted.setSelected(true);
                    logger.out("set to: completed");
                    // System.out.println("set to: not completed");
                }
                DbStore.getInstance().update(todo);
                setCheckBoxIcon();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void createNewTodo() {
        createNameTextField();
        createDateTextField();
        createTodoDatePicker();
    }

    private void createNewTodoView() {
        if (todoViewInstance == 0) {
            newTodoLayout = new VBox(10);
            Button submitBtn = new Button("Submit");
            submitBtn.setId("submit-btn");
            createNewTodo();
            System.out.println("in the bayou");
            newTodoDatePickerBtn.setOnAction(event -> {
                newTodoDatePicker.show();
                System.out.println("clicked datePickerIcon");
            });
            newTodoDatePickerBtn.setId("date-picker-btn");
            newTodoDatePickerBtn.setGraphic(newTodoDatePickerIcon);
            Library.createScaleTransition(newTodoDatePickerBtn, 1.0);
            Library.createTooltip(newTodoDatePickerBtn, "Yes, this changes the date.");

            StackPane newTodoDatePickerElements = new StackPane(newTodoDatePicker, newTodoDatePickerBtn);
            newTodoLayout.getChildren().addAll(newTodoNameTextField, newTodoDateTextField, newTodoDatePickerElements,
                    submitBtn);
            container.getChildren().add(newTodoLayout);
            todoViewInstance++;
            submitBtn.setOnAction(event -> {
                String todoName = newTodoNameTextField.getText();
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate todoDate = LocalDate.parse(newTodoDateTextField.getText(), format);
                if (!todoName.isEmpty()) {
                    newTodoNameTextField.clear();
                    newTodoDateTextField.clear();
                    newTodoDatePicker.setValue(null);
                    // add to database (returns a Todo object)
                    Todo newTodo = DbStore.getInstance().addTodo(todoName, todoDate);
                    if (newTodo != null) {
                        Card addedCard = new Card(container, newTodo);
                        System.out.println("out the bayou");
                        container.getChildren().remove(newTodoLayout);
                        todoViewInstance--;
                        logger.out(todo.toString());
                    }
                }
            });
        }
    }

    private void createAddButton() {
        addBtn = new Button("", addIcon);
        // addBtn.setText("Add");
        addBtn.setId("add-todo-btn");
        addBtn.setOnAction(event -> {
            // might need to ensure only a single view is created
            System.out.println("add button click registered.");
            createNewTodoView();
        });
    }

    private void createTodoDatePicker() {
        newTodoDatePicker = new DatePicker();
        newTodoDatePicker.setMaxWidth(25);
        newTodoDatePicker.setShowWeekNumbers(false);
        newTodoDatePicker.getEditor().setManaged(false);
        newTodoDatePicker.setVisible(false);
        newTodoDatePicker.setOnAction(e -> {
            // bind the text property of the textfield to the datepicker
            newTodoDateTextField.textProperty().bind(newTodoDatePicker.valueProperty().asString());
            newTodoDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null) {
                    newTodoDateTextField.textProperty().unbind();
                    newTodoDateTextField.setText("");
                }
            });
        });
    }

    private void createNameTextField() {
        newTodoNameTextField = new TextField("Todo Name");
        newTodoNameTextField.setMaxWidth(100);
        newTodoNameTextField.setMaxHeight(50);
        Library.createTooltip(newTodoNameTextField, "Gotta have a name buddy.");
        newTodoNameTextField.setOnMouseClicked((MouseEvent event) -> {
            if (newTodoNameTextField.getText().equals("Todo Name")) {
                newTodoNameTextField.clear();
            }
            logger.out("Mouse clicked @newTodoNameTextField");
        });

    }

    private void createDateTextField() {
        newTodoDateTextField = new TextField("Todo Date");
        newTodoDateTextField.setEditable(false); // NOTE: set this to false to disable editing
        newTodoDateTextField.setMaxHeight(50);
        newTodoDateTextField.setMaxWidth(100);
        Library.createTooltip(newTodoDateTextField, "Select a date from the date-picker below.");
        newTodoDateTextField.setOnMouseEntered((MouseEvent event) -> {
            logger.out("Mouse hover @newTodoDateTextField.");
        });
        newTodoDateTextField.setOnMouseExited((MouseEvent event) -> {
            newTodoDateTextField.setText("Todo Date");
            logger.out("Mouse clicked @newTodoDateTextField.");
        });

        newTodoDateTextField.setOnMouseClicked((MouseEvent event) -> {
            if (newTodoDateTextField.getText().equals("Todo Date")) {
                newTodoDateTextField.clear();
            }
            logger.out("Mouse clicked @newTodoDateTextField.");
        });
    }

    public HBox getCardHbox() {
        return cardHbox;
    }

    public Button getTodoAddBtn() {
        return addBtn;
    }

    public void init() {
        setTextFields();
        setCheckBox();
        setDatePicker();
        createDelBtn();
        createDatePickerBtn();
        setCardBox();
        setCardPane();
        createAddButton();
        container.getChildren().addAll(getCardHbox());
    }

}
