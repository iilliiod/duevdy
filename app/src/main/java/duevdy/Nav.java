package duevdy;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.Node;
import javafx.collections.FXCollections;
import org.kordamp.ikonli.javafx.FontIcon;

import duevdy.Logger;
import duevdy.DbStore;
import duevdy.UI;

public class Nav {
    private VBox content;
    private Logger logger = new Logger();
    public VBox sideBar;
    private Button todoBtn;
    private StackPane sideBarStack = new StackPane();
    private Button notesBtn;
    private FontIcon todoIcon = new FontIcon("mdi-format-list-bulleted-type");
    private FontIcon noteIcon = new FontIcon("mdi-note-text");
    private FontIcon progressIcon = new FontIcon("mdi-creation");
    private UI ui;

    public Nav(UI ui) {
        this.ui = ui;
        content = new VBox();
        sideBar = new VBox();
        init();
    }

    private void setTodoBtn() {
        todoBtn = new Button("To-Do", todoIcon);
        todoBtn.setId("nav-todo-btn");
        todoBtn.setOnAction(event -> {
            ui.state = UI.ProgramState.TODO;
            ui.updateScene();
        });
    }
    public void setNoteBtn() {
        notesBtn = new Button("Notes", noteIcon);
        notesBtn.setId("nav-notes-btn");
        notesBtn.setOnAction(event -> {
            ui.state = UI.ProgramState.NOTES;
            ui.updateScene();
        });
    }

    public Button getTodoBtn() {
        return todoBtn;
    }
    public Button getNotesBtn() {
        return notesBtn;
    }

    public VBox getContainer() {
        return sideBar;
    }

    public void setNavBarContent(Node contentContainer) {
        contentContainer.setId("scroll-pane");
        sideBarStack.setId("nav-content-notes");
        sideBarStack.getChildren().clear();
        sideBarStack.getChildren().add(contentContainer);
        VBox.setVgrow(sideBarStack, javafx.scene.layout.Priority.ALWAYS);
        sideBar.getChildren().remove(sideBarStack);
        sideBar.getChildren().add(sideBarStack);
    }
    public void setProgressBar() {
        Label label = new Label("To-Do Progress:", progressIcon);
        DoubleProperty completedTasks = new SimpleDoubleProperty(DbStore.getInstance().getCompletedTodoCnt());
        DoubleProperty totalTasks = new SimpleDoubleProperty(DbStore.getInstance().queryTodo().size());
        logger.out(DbStore.getInstance().getCompletedTodoCnt() + "/" + DbStore.getInstance().queryTodo().size());
        ProgressBar progressBar = new ProgressBar();
        progressBar.setId("nav-progress-bar");
        progressBar.setPrefWidth(200);
        progressBar.progressProperty().bind(completedTasks.divide(totalTasks));
        VBox progressBox = new VBox(label, progressBar);
        progressBox.setId("nav-progress-box");

        sideBarStack.setId("nav-content-progress");
        sideBarStack.getChildren().clear();
        sideBarStack.getChildren().add(progressBox);
        sideBar.getChildren().remove(sideBarStack);
        sideBar.getChildren().add(sideBarStack);
    }

    private void init() {
        setNoteBtn();
        setTodoBtn();
        content.getChildren().addAll(todoBtn, notesBtn);
        sideBar.getChildren().add(content);
        sideBar.setId("nav-side-bar");
    }

}
