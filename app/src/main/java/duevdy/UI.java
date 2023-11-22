package duevdy;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
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

import java.awt.Event;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*; // List

import duevdy.DbStore;
import duevdy.Courses;
import duevdy.Controller;
import duevdy.Logger;
import duevdy.Card;
import duevdy.Note;
import duevdy.Library;
import duevdy.NoteView;

public class UI {
    private Stage stage;
    private VBox root = new VBox();
    private Scene scene; 
    private Scene notesScene; 
    private ScrollPane scrollPane;
    private DbStore dbStore = DbStore.getInstance();
    private Logger logger = new Logger();
    private int cardWidth = 150;
    private int cardHeight = 100;
    private Card card;
    private Note note;
    private VBox toolBarBox;
    private VBox headerBox;
    private Label header;
    private final LocalDate dateToday = LocalDate.now();
    enum ProgramState {
        INIT,
        NOTES,
        TODO
    }
    ProgramState programState = ProgramState.INIT;

    UI(Stage stage) {
        this.stage = stage;
    }

    public void init() {
        // set up main container
        TilePane cardContainer = new TilePane();
        cardContainer.setId("tile-pane");
        cardContainer.setVgap(10);
        cardContainer.setHgap(10);
        cardContainer.setPadding(new Insets(20));

        root.setId("main-container");
        load(cardContainer);

        scrollPane = new ScrollPane(cardContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setId("scroll-pane");
        ToolBar toolBar = new ToolBar();
        toolBar.setId("tool-bar");

        Button todoBtn = new Button();
        boolean isHovered = false;
        Image todoIcon = new Image(getClass().getResourceAsStream("/todo-list-icon.png"));
        ImageView todoImageView = new ImageView(todoIcon);
        todoImageView.setFitWidth(16);
        todoImageView.setFitHeight(16);
        todoBtn.setGraphic(todoImageView);
        todoBtn.setId("todo-btn");
        todoBtn.setOnAction(event -> {
            System.out.println("switching to todo");
            programState = ProgramState.TODO;
            System.out.println(programState);
            updateScene();
        });

        Button notesBtn = new Button();
        Image notesIcon = new Image(getClass().getResourceAsStream("/notes-icon.png"));
        ImageView notesImageView = new ImageView(notesIcon);
        notesImageView.setFitWidth(16);
        notesImageView.setFitHeight(16);
        notesBtn.setGraphic(notesImageView);
        notesBtn.setId("notes-btn");
        notesBtn.setOnAction(event -> {
            System.out.println("switching to notes");
            programState = ProgramState.NOTES;
            System.out.println(programState);
            updateScene();
        });

        toolBar.getItems().addAll(todoBtn, new Separator(), notesBtn);
        toolBarBox = new VBox(toolBar);
        toolBarBox.setSpacing(10);
        toolBarBox.setId("tool-bar-box");

        updateScene();
    }

    private void updateScene() {
        switch(programState) {
            case INIT:
                System.out.println("state: INIT");
            case NOTES:
                System.out.println("state: NOTES");
                ScrollPane notePane = new ScrollPane();
                note = new Note();

                VBox noteBox = new VBox();
                noteBox.getChildren().addAll(new Label("idek"), note.getVbox());
                header = new Label("Notes");
                headerBox = new VBox(header);
                header.setId("header");

                // TODO: consider refactor by using a method
                root.getChildren().clear();
                root.getChildren().addAll(headerBox, noteBox, toolBarBox);

                if(scene == null) {
                    scene = new Scene(root, 300, 600);
                    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

                    stage.setScene(scene);
                }
                break;
            case TODO:
                System.out.println("state: TODO");
                header = new Label("To-Do");
                headerBox = new VBox(header);
                headerBox.getChildren().add(card.getTodoAddBtn());
                header.setId("header");

                root.getChildren().clear();
                root.getChildren().addAll(headerBox, scrollPane, toolBarBox);

                if(scene == null) {
                    scene = new Scene(root, 300, 600);
                    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

                    stage.setScene(scene);
                }
                break;
            default:
                stage.setScene(scene);
        }
        stage.show();
    }

    private void load(TilePane container) {
        if(DbStore.getInstance().queryData().size() <= 0) {
            System.out.println("no existing data found, initializing defaults...");
            dbStore.addData("Test", this.dateToday);
            dbStore.addData("Test!", this.dateToday);
            dbStore.addData("Test2", this.dateToday);
        }

        for (Courses c : dbStore.queryData()) {
            Courses course = c;
            card = new Card(container, course);

            logger.out(course.toString());
        }
    }

    public void update(TilePane container, Courses course) {
        card = new Card(container, course);

        logger.out(course.toString());
    }
}
