package duevdy;

import javafx.scene.Parent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.collections.FXCollections;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.File;
import java.time.LocalDate;
import duevdy.Nav;


public class UI {
    private static Nav nav;
    private static Stage stage;
    private static BorderPane root;
    private static AnchorPane base;
    private static Scene scene; 
    private static ScrollPane scrollPane;
    private static DbStore dbStore = DbStore.getInstance();
    private static Logger logger = new Logger();
    private static Card card;
    private static NoteView noteView;
    private static HBox headerBox;
    private static Label header;
    private static Settings settings;
    private static Region spacer = new Region();
    private static Search searchBar;
    private static final LocalDate dateToday = LocalDate.now();
    private static ProgramTheme currentTheme = ProgramTheme.LIGHT;
    private static Button changeThemeBtn;
    private static FontIcon darkModeIcon;
    private static FontIcon lightModeIcon;
    
    UI(Stage stage) {
        this.stage = stage;
        init();
    }

    public static void addSearchToBase(Node node) {
        base.getChildren().remove(node);

        AnchorPane.setTopAnchor(node, 60.0);
        AnchorPane.setRightAnchor(node, 0.0);

        base.getChildren().add(node);
    }
    public static void removeSearchFromBase(Node node) {
        // TODO : review, kinda weird
        base.getChildren().clear();
        base.getChildren().add(root);
    }

    public static void init() {
        root = new BorderPane(); 
        base = new AnchorPane(); 
        lightModeIcon = new FontIcon("mdi-white-balance-sunny");
        darkModeIcon = new FontIcon("mdi-weather-night");
        noteView = new NoteView();

        lightModeIcon.setId("icon-light-mode");
        darkModeIcon.setId("icon-dark-mode");

        root.setId("root");
        base.setId("base");
        base.getChildren().clear();
        root.getChildren().clear();

        setColorSchemeBtn();

        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);

        base.getChildren().add(root);
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
        nav = new Nav();

        updateScene();
    }

    private static void setColorSchemeBtn() {
        // read from settings file
        changeThemeBtn = new Button();
        changeThemeBtn.setId("change-theme-btn");

        if (currentTheme == ProgramTheme.LIGHT) {
            changeThemeBtn.setGraphic(darkModeIcon);
        } else {
            changeThemeBtn.setGraphic(lightModeIcon);
        }

        Library.createScaleTransition(changeThemeBtn, 0.5);

        changeThemeBtn.setOnAction(event -> {
            // monitoring changes
            System.out.println(currentTheme.toString());

            currentTheme = (currentTheme == ProgramTheme.LIGHT) ? ProgramTheme.DARK : ProgramTheme.LIGHT;
            currentTheme.setTheme(scene);

            if (currentTheme == ProgramTheme.LIGHT) {
                changeThemeBtn.setGraphic(darkModeIcon);
            } else {
                changeThemeBtn.setGraphic(lightModeIcon);
            }

        });
    }

    public static void addToHeaderContainer(Node node) {
        if(headerBox != null) {
            System.out.println("ADDING TO CONTAINER");
            headerBox.getChildren().remove(node);
            headerBox.getChildren().add(3, node);
            // updateScene();
        }
        System.out.println(headerBox.getChildren());
    }
    public static void removeFromHeaderContainer(Node node) {
        if(headerBox != null) {
            headerBox.getChildren().remove(node);
            updateScene();
        }
    }

    public static void updateScene() {
        System.out.println("UPDATING SCENE");
        switch(Settings.getState()) {
            case INIT:
                System.out.println("state: INIT");
                // TODO: create a splash screen
            case NOTES:
                System.out.println("state: NOTES");
                // noteView = new NoteView();

                header = new Label("Notes");
                headerBox = new HBox(header);
                searchBar = new Search(ProgramState.NOTES.toString());
                HBox noteTodoBox = new HBox(noteView.getDeleteButton(), noteView.getAddNewNoteButton());
                noteTodoBox.setId("header-btn-box");
                HBox.setHgrow(spacer, Priority.ALWAYS);
                headerBox.getChildren().addAll(changeThemeBtn, spacer, noteTodoBox, searchBar.getSearch());
                header.setId("header");
                headerBox.setId("header-box");

                // TODO: consider refactor by using a method
                root.getChildren().clear();
                root.setTop(headerBox);
                root.setCenter(Editor.getTextArea());
                root.setLeft(nav.getContainer());
                nav.setNavBarContent(noteView.getLayout());

                if(scene == null) {
                    base.getChildren().clear();
                    base.getChildren().add(root);
                    scene = new Scene(base, 300, 600);
                    currentTheme.setTheme(scene);
                    // scene.getStylesheets().add(UI.class.getResource("/light-mode.css").toExternalForm());

                    stage.setScene(scene);
                }
                break;
            case TODO:
                System.out.println("state: TODO");
                header = new Label("To-Do");
                headerBox = new HBox(header);
                searchBar = new Search(ProgramState.TODO.toString());
                HBox cardTodoBox = new HBox(card.getTodoAddBtn());
                cardTodoBox.setId("header-btn-box");
                HBox.setHgrow(spacer, Priority.ALWAYS);
                headerBox.getChildren().addAll(changeThemeBtn, spacer, cardTodoBox, searchBar.getSearch());
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
                    base.getChildren().clear();
                    base.getChildren().add(root);
                    scene = new Scene(base, 300, 600);
                    currentTheme.setTheme(scene);
                    // scene.getStylesheets().add(UI.class.getResource("/light-mode.css").toExternalForm());

                    stage.setScene(scene);
                }
                break;
            default:
                stage.setScene(scene);
        }
        stage.show();
    }

    public static Scene getScene() {
        return scene;
    }

    private static void load(TilePane cardContainer) {
        if(DbStore.getInstance().queryTodo().size() <= 0) {
            System.out.println("no existing data found, initializing defaults...");
            dbStore.addTodo("Title...", dateToday);
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

    public static void reload() {
        scene = null;
        root.getChildren().clear();
        base.getChildren().clear();
        init();
    }

    public static Stage getStage() {
        return stage;
    }
}
