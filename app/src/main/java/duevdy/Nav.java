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
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
    private Region spacer = new Region();
    private static StringProperty progressText = new SimpleStringProperty();
    private static DoubleProperty completedTasks;
    private static DoubleProperty totalTasks;

    public Nav() {
        todoIcon.setId("icon-todo"); 
        noteIcon.setId("icon-note"); 
        progressIcon.setId("icon-progress"); 

        content = new VBox();
        sideBar = new VBox();
        init();
    }

    private void setTodoBtn() {
        todoBtn = new Button("To-Do", todoIcon);
        todoBtn.setId("nav-todo-btn");
        todoBtn.setOnAction(event -> {
            Settings.setState(ProgramState.TODO);
            UI.updateScene();
        });
    }
    public void setNoteBtn() {
        notesBtn = new Button("Notes", noteIcon);
        notesBtn.setId("nav-notes-btn");
        notesBtn.setOnAction(event -> {
            Settings.setState(ProgramState.NOTES);
            UI.updateScene();
        });
    }

    private Button getTodoBtn() {
        return todoBtn;
    }
    private Button getNotesBtn() {
        return notesBtn;
    }

    public VBox getContainer() {
        switch (Settings.getState()) {
            case NOTES -> {
                setTodoBtn();
                content.getChildren().clear();
                content.getChildren().addAll(todoBtn, spacer);
                sideBar.getChildren().clear();
                sideBar.getChildren().add(content);
            }
            case TODO -> {
                setNoteBtn();
                content.getChildren().clear();
                content.getChildren().addAll(notesBtn, spacer);
                sideBar.getChildren().clear();
                sideBar.getChildren().add(content);
            }
        }
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

    private static Integer[] updateData() {
        Integer[] data = new Integer[2];
        data[0] = DbStore.getInstance().getCompletedTodoCnt();
        data[1] = DbStore.getInstance().queryTodo().size();
        return data;
    }

    public static void loadProgressBarProperties() {
        completedTasks = new SimpleDoubleProperty(updateData()[0]);
        totalTasks = new SimpleDoubleProperty(updateData()[1]);

        progressText.bind(Bindings.when(Bindings.greaterThan(totalTasks,(0.0)))
                .then(completedTasks.divide(totalTasks).multiply(100).asString("%.0f").concat("%"))
                .otherwise("--"));
    }

    public void setProgressBar() {
        Label label = new Label();
        label.setGraphic(progressIcon);
        label.setId("nav-progress-label");

        loadProgressBarProperties();

        label.textProperty().bind(Bindings.concat("To-Do Progress: \t\t\t", progressText));

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
        // content.getChildren().addAll(todoBtn, notesBtn); // TODO : fix 
        spacer.setId("nav-spacer");
        content.getChildren().addAll(todoBtn, spacer);
        sideBar.getChildren().add(content);
        sideBar.setId("nav-side-bar");
    }


}
