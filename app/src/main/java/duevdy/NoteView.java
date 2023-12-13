package duevdy;

import javafx.stage.Screen;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.geometry.Orientation;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Scanner;

import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ListCell;
import javafx.collections.FXCollections;
import org.kordamp.ikonli.javafx.FontIcon;

public class NoteView {
    private Logger logger = new Logger();
    private FlowPane container = new FlowPane();
    private TextArea newNoteTextArea;
    private ObservableList<Note> notes = FXCollections.observableArrayList();
    private ListView<Note> notesList = new ListView<>(notes);
    private String noteUUID;
    private HBox layout;
    private FontIcon icon = new FontIcon("mdi-plus");
    private FontIcon addIcon = new FontIcon("mdi-plus");
    private FontIcon deleteIcon = new FontIcon("mdi-delete");
    private final LocalDate dateToday = LocalDate.now();
    private Button addBtn;
    private Button deleteButton = new Button("", deleteIcon);

    public NoteView() {
        icon.setId("icon-add");
        addIcon.setId("icon-add");
        deleteIcon.setId("icon-delete");

        container.setId("note-container");
        container.setPrefWrapLength(20);
        container.setOrientation(Orientation.HORIZONTAL);
        init();
    }

    private void createAddButton() {
        addBtn = new Button("", addIcon);
        // addBtn.setText("Add");
        addBtn.setId("add-new-btn");
        addBtn.setOnAction(event -> {
            // might need to ensure only a single view is created
            System.out.println("add button click registered.");
            Editor.display();
        });
    }

    public void loadNote(String uuid) {
        // get file based on uuid
        System.out.println("adding " + uuid);
        Note currentNote = null;
        File file = DbStore.getInstance().getNote(uuid);

        for(Note note : DbStore.getInstance().queryNotes()) {
            if(note.getID().equals(uuid)) {
                currentNote = note; 
                break;
            }
        }

        // set title based on content length
        String content = "";
        String title = "";
        try {
            Scanner scanner = new Scanner(file);
            content = scanner.nextLine();
            System.out.println("content: " + content);
            if(currentNote.getTitle().equals("")) {
                if(content.length() > 25) {
                    title = content.substring(0, 25) + "...";
                } else {
                    title = content;
                }
            } else {
                title = currentNote.getTitle();
            }
            logger.out("note title: " + title);
            scanner.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } 

        // update note title
        currentNote.setTitle(title);

        // update display
        System.out.println(currentNote.toString() + ": " + title + " " + content);
        setNoteList(currentNote, title, content);
        updateContainer();
    }

    public Button getDeleteButton() {
        deleteButton.setId("delete-btn");
        return deleteButton;
    }

    private void setNoteList(Note currentNote, String title, String content) {
        // display note in list
        notes.add(currentNote);
        notesList.setItems(notes);
        notesList.setId("nav-notes-list");
        Editor.display();

        notesList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            System.out.println("selected: " + newSelection.getID());
            // show note contents
            Editor.display(newSelection.getID());

            deleteButton.setOnAction(event -> {
                System.out.println("clicked button");
                notesList.getItems().remove(newSelection);
            });
        });

        notesList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Note item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setId("nav-empty-custom-list-cell");
                } else {
                    setText(item.getTitle());
                    setId("nav-custom-list-cell");
                }
            }
        });

        deleteButton.setOnAction(event -> {
            System.out.println("clicked button");
        });

        Label label = new Label(title);
        label.setId("note-label");
        layout = new HBox(20);
        layout.setId("note-layout");
        layout.getChildren().addAll(label);
    }

    public Node getLayout() {
        return notesList;
    }

    public Button getAddNewNoteButton() {
        return addBtn;
    }

    private void updateContainer() {
        System.out.println("UPDATING CONTAINER");
        // container.getChildren().clear();
        if(container.getChildren().size() > 0 ) {
            container.getChildren().add(layout);
        }
        // TODO refresh container
    }

    private void init() {
        createAddButton();
        updateContainer();
        Editor.display();
    }
}
