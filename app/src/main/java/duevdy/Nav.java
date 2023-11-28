package duevdy;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.collections.FXCollections;
import org.kordamp.ikonli.javafx.FontIcon;

import duevdy.UI;

public class Nav {
    private VBox content;
    public VBox sideBar;
    private Button todoBtn;
    private Button notesBtn;
    private FontIcon todoIcon = new FontIcon("mdi-format-list-bulleted-type");
    private FontIcon noteIcon = new FontIcon("mdi-note-text");
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

    private void init() {
        setNoteBtn();
        setTodoBtn();
        content.getChildren().addAll(todoBtn, notesBtn);
        sideBar.getChildren().add(content);
        sideBar.setId("nav-side-bar");
    }

}
