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
    private Nav nav;
    private Stage stage;
    private BorderPane root = new BorderPane();
    private Scene scene; 
    private ScrollPane scrollPane;
    private DbStore dbStore = DbStore.getInstance();
    private Logger logger = new Logger();
    private Card card;
    private NoteView noteView = new NoteView();
    private HBox headerBox;
    private Label header;
    private Settings settings;
    private final LocalDate dateToday = LocalDate.now();
    
    enum ProgramState {
        INIT,
        NOTES,
        TODO
    }
    public ProgramState state = ProgramState.INIT;

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
        //         scene.getStylesheets().add(getClass().getResource("/dark-mode.css").toExternalForm());
        //         break;
        //     case 0:
        //         theme = ProgramTheme.LIGHT;
        //         scene.getStylesheets().add(getClass().getResource("/light-mode.css").toExternalForm());
        //         break;
        //     default:
        //         theme = ProgramTheme.DARK;
        //         scene.getStylesheets().add(getClass().getResource("/dark-mode.css").toExternalForm());
        // }
    }

    public void updateScene() {
        switch(state) {
            case INIT:
                System.out.println("state: INIT");
            case NOTES:
                System.out.println("state: NOTES");
                // noteView = new NoteView();

                VBox noteBox = new VBox();
                noteBox.getChildren().add(noteView.getVbox());
                header = new Label("Notes");
                headerBox = new HBox(header);
                header.setId("header");

                // TODO: consider refactor by using a method
                root.getChildren().clear();
                root.setTop(headerBox);
                root.setCenter(Editor.getTextArea());
                root.setBottom(noteBox);
                root.setLeft(nav.getContainer());
                nav.setNavBarContent(noteView.getLayout());

                if(scene == null) {
                    scene = new Scene(root, 300, 600);
                    // setColorScheme();
                    scene.getStylesheets().add(getClass().getResource("/light-mode.css").toExternalForm());

                    stage.setScene(scene);
                }
                break;
            case TODO:
                System.out.println("state: TODO");
                header = new Label("To-Do");
                headerBox = new HBox(header);
                HBox cardTodoBox = new  HBox(card.getTodoAddBtn());
                cardTodoBox.setId("card-todo-btn-box");
                Region spacer = new Region();
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
                        System.out.println("updating progress bar. .. .");
                        nav.setProgressBar();
                    }
                };
                DbStore.getInstance().completedTodoCntProperty().addListener(listener);

                if(scene == null) {
                    scene = new Scene(root, 300, 600);
                    // setColorScheme();
                    scene.getStylesheets().add(getClass().getResource("/light-mode.css").toExternalForm());

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
            dbStore.addTodo("Test", this.dateToday);
            dbStore.addTodo("Test!", this.dateToday);
            dbStore.addTodo("Test2", this.dateToday);
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
