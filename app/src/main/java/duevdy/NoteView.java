package duevdy;

import javafx.stage.Screen;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
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
    private GridPane gridPane;
    private TextArea newNoteTextArea;
    private ObservableList<Note> notes = FXCollections.observableArrayList();
    private ListView<Note> notesList = new ListView<>(notes);
    private String noteUUID;
    private VBox vbox;
    private HBox layout;
    private FontIcon icon = new FontIcon("mdi-plus");
    private FontIcon deleteIcon = new FontIcon("mdi-delete");
    private final LocalDate dateToday = LocalDate.now();

    public NoteView() {
        container.setId("note-container");
        container.setPrefWrapLength(20);
        container.setOrientation(Orientation.HORIZONTAL);
        init();
    }

    private void createNewNoteTextArea() {
        newNoteTextArea = new TextArea("New note...");
        newNoteTextArea.setPrefRowCount(1);
        newNoteTextArea.setEditable(true);
        newNoteTextArea.setMaxHeight(100);
        newNoteTextArea.setMaxWidth(Screen.getPrimary().getVisualBounds().getWidth() / 4);
        Library.createTooltip(newNoteTextArea, "Got anything to say?");
        newNoteTextArea.textProperty().addListener((observable, oldVal, newVal) -> {
            int row = newNoteTextArea.getText().split("\n").length;
            int col = newNoteTextArea.getText().split(" ").length;
            newNoteTextArea.setPrefRowCount(row);
            newNoteTextArea.setPrefColumnCount(col);
        });
        newNoteTextArea.setOnMouseClicked((MouseEvent event) -> {
            if(newNoteTextArea.getText().equals("New note...")) {
                newNoteTextArea.clear();
            }
            logger.out("Mouse clicked @newNoteTextArea");
        });
        // TODO: fix later
        // newNoteTextArea.setOnMouseExited((MouseEvent event) -> {
        //     if(newNoteTextArea.getText().equals("")) {
        //         newNoteTextArea.setText("New note...");
        //     }
        //     logger.out("Mouse exited @newNoteTextArea");
        //});
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

        System.out.println(currentNote.toString());
        String content = "";
        String title = "";
        try {
            Scanner scanner = new Scanner(file);
            content = scanner.nextLine();
            System.out.println("content: " + content);
            if(content.length() > 25) {
                title = content.substring(0, 25) + "...";
            } else {
                title = content;
            }
            logger.out("note title: " + title);
            scanner.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } 
        notes.add(currentNote);
        notesList.setItems(notes);
        notesList.setId("nav-notes-list");
        Button deleteButton = new Button("Delete", deleteIcon);
        notesList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            System.out.println("selected: " + newSelection.getID());
            // show note contents

            deleteButton.setOnAction(event -> {
                System.out.println("clicked button");
                notesList.getItems().remove(newSelection);
            });
            // show delete button
             // Get selected cell

        });

        deleteButton.setOnAction(event -> {
            System.out.println("clicked button");
        });
        Label label = new Label(title);
        label.setId("note-label");
        layout = new HBox(20);
        layout.setId("note-layout");
        layout.getChildren().addAll(label, deleteButton);

        updateContainer();
    }

    public Node getLayout() {
        return notesList;
    }

    private void setVbox() {
        vbox = new VBox(8);
        VBox newNoteLayout = new VBox(8);
        Button newNoteBtn = new Button("New note", icon);
        newNoteBtn.setId("new-note-btn");
        newNoteBtn.setOnAction(event -> {
            System.out.println("adding a new note");
            String content = newNoteTextArea.getText();
            String title = content.substring(0, content.indexOf(" "));
            if (!content.isEmpty()) {
                newNoteTextArea.clear();
                // add to database (returns a Note object)
                Note newNote = DbStore.getInstance().addNote(title, this.dateToday, content);
                if(newNote != null) {
                    logger.out("added " + newNote.getID());
                    loadNote(newNote.getID());
                    logger.out(newNote.toString());
                }
            } else {
                System.out.println("content?");
            }
        });
        newNoteLayout.setPadding(new Insets(20));
        newNoteLayout.getChildren().addAll(newNoteTextArea, newNoteBtn);
        newNoteLayout.setId("note-layout");
        // TODO: noteView needs noteLayout
        System.out.println("adding flowpane " + container.getChildren());
        vbox.getChildren().addAll(notesList, container, newNoteLayout);
        vbox.setId("note-vbox");
    }

    private void setGridPane() {
        gridPane = new GridPane();
        gridPane.getChildren().addAll(vbox);
    }
    public VBox getVbox() {
        return vbox;
    }

    private void updateContainer() {
        // container.getChildren().clear();
        if(container.getChildren().size() > 0 ) {
            container.getChildren().add(layout);
        }
    }

    private void init() {
        createNewNoteTextArea();
        setVbox();
        setGridPane();
        updateContainer();
    }
}
