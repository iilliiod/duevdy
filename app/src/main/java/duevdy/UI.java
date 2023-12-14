package duevdy;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.layout.TilePane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.File;
import java.time.LocalDate;
import duevdy.Nav;


public class UI {
    private static Nav nav;
    private static Stage stage;
    private static BorderPane root = new BorderPane();
    private static Scene scene; 
    private static ScrollPane scrollPane;
    private static DbStore dbStore = DbStore.getInstance();
    private static Logger logger = new Logger();
    private static Card card;
    private static NoteView noteView = new NoteView();
    private static HBox headerBox;
    private static Label header;
    private static Settings settings;
    private static Region spacer = new Region();
    private static final LocalDate dateToday = LocalDate.now();
    
    static enum ProgramState {
        INIT,
        NOTES,
        TODO
    }
    public static ProgramState state = ProgramState.INIT;

    UI(Stage stage) {
        this.stage = stage;
    }

    public void init() {
        // set up main cardContainer
        TilePane cardContainer = new TilePane();
        cardContainer.setId("tile-pane");
        cardContainer.setVgap(10);
        cardContainer.setHgap(10);
        cardContainer.setPadding(new Insets(20));

        root.setId("main-card-container");
        load(cardContainer);

        scrollPane = new ScrollPane(cardContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setId("scroll-pane");
        nav = new Nav(this);

        updateScene();
    }

    private void setColorScheme() {
        // read from settings file
        // int val = 0;
        // switch (val) {
        //     case 1:
        //         theme = ProgramTheme.DARK;
        //         scene.getStylesheets().add(UI.class().getResource("/light-mode.css").toExternalForm());
        //         break;
        //     case 0:
        //         theme = ProgramTheme.LIGHT;
        //         scene.getStylesheets().add(UI.class().getResource("/light-mode.css").toExternalForm());
        //         break;
        //     default:
        //         theme = ProgramTheme.DARK;
        //         scene.getStylesheets().add(UI.class().getResource("/light-mode.css").toExternalForm());
        // }
    }

    public static void updateScene() {
        System.out.println("UPDATING SCENE");
        switch(state) {
            case INIT:
                System.out.println("state: INIT");
                // TODO: create a splash screen
            case NOTES:
                System.out.println("state: NOTES");
                // noteView = new NoteView();

                header = new Label("Notes");
                headerBox = new HBox(header);
                HBox noteTodoBox = new HBox(noteView.getDeleteButton(), noteView.getAddNewNoteButton());
                noteTodoBox.setId("header-btn-box");
                HBox.setHgrow(spacer, Priority.ALWAYS);
                headerBox.getChildren().addAll(spacer, noteTodoBox);
                header.setId("header");
                headerBox.setId("header-box");

                // TODO: consider refactor by using a method
                root.getChildren().clear();
                root.setTop(headerBox);
                root.setCenter(Editor.getTextArea());
                root.setLeft(nav.getContainer());
                nav.setNavBarContent(noteView.getLayout());

                if(scene == null) {
                    scene = new Scene(root, 300, 600);
                    // setColorScheme();
                    scene.getStylesheets().add(UI.class.getResource("/light-mode.css").toExternalForm());

                    stage.setScene(scene);
                }
                break;
            case TODO:
                System.out.println("state: TODO");
                header = new Label("To-Do");
                headerBox = new HBox(header);
                HBox cardTodoBox = new HBox(card.getTodoAddBtn());
                cardTodoBox.setId("header-btn-box");
                HBox.setHgrow(spacer, Priority.ALWAYS);
                headerBox.getChildren().addAll(spacer, cardTodoBox);
                header.setId("header");
                headerBox.setId("header-box");

                root.getChildren().clear();
                root.setTop(headerBox);
                root.setCenter(scrollPane);
                root.setLeft(nav.getContainer());
                nav.setProgressBar();

                ChangeListener<Number> listener = new ChangeListener<>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
                        System.out.println("updating progress bar...");
                        nav.setProgressBar();
                    }
                };
                DbStore.getInstance().completedTodoCntProperty().addListener(listener);

                if(scene == null) {
                    scene = new Scene(root, 300, 600);
                    // setColorScheme();
                    scene.getStylesheets().add(UI.class.getResource("/light-mode.css").toExternalForm());

                    stage.setScene(scene);
                }
                break;
            default:
                stage.setScene(scene);
        }
        stage.show();
    }

    private void load(TilePane cardContainer) {
        if(DbStore.getInstance().queryTodo().size() <= 0) {
            System.out.println("no existing data found, initializing defaults...");
            dbStore.addTodo("New Todo...", this.dateToday);
        }

        for (Todo c : dbStore.queryTodo()) {
            Todo todo = c;
            card = new Card(cardContainer, todo);

            logger.out(todo.toString());
        }

        for (Note n : dbStore.queryNotes()) {
            System.out.println("in load: " + n.toString());
            noteView.loadNote(n.getID());
        }
    }

}
