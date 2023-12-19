package duevdy;

import javafx.scene.input.MouseButton;
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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
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
    private AtomicBoolean selectable = new AtomicBoolean(false);
    private AtomicBoolean selectModeActive = new AtomicBoolean(false);
    private ArrayList<Note> buffer = new ArrayList<>(notes);

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
        // updateContainer();
    }

    public Button getDeleteButton() {
        deleteButton.setId("delete-btn");
        return deleteButton;
    }

    private void refreshNotesList() {
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
                    if(item.getSelected()) {
                        setId("note-selected-multi");
                    }
                }
            }
        });
    }

    private void activateLongPressListener() {
        // TODO : fix logic to implement
        if(!selectable.get() && !selectModeActive.get()) {
            AtomicLong startTime = new AtomicLong(0);

            notesList.setOnMousePressed(event -> {
                System.out.println("pressed button");
                System.out.println("selectable: " + selectable.get());

                if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
                    System.out.println("registered click");
                    startTime.set(System.currentTimeMillis());
                }
            });

            notesList.setOnMouseReleased(releaseEvent -> {
                if(!selectable.get()) {
                    System.out.println("registered release");
                    long endTime = System.currentTimeMillis();
                    long pressDuration = endTime - startTime.get();
                    System.out.println(startTime.get() + " - " + endTime + " = " + pressDuration);

                    if (pressDuration >= 1000) { // 1.5s
                        pressDuration = 0;

                        Note selectedItem = notesList.getSelectionModel().getSelectedItem();
                        if (selectedItem != null) {
                            selectable.set(true);
                            selectedItem.setSelected(true);
                            if(!buffer.contains(selectedItem)) buffer.add(selectedItem);
                            System.out.println("calling select mode");
                            refreshNotesList();

                            selectMode();
                            // Reset the event handlers to their original state
                            notesList.setOnMousePressed(null);
                            notesList.setOnMouseReleased(null);
                        }
                    } else {
                        pressDuration = 0;
                        selectable.set(false);
                    }
                }

            });
        }
    }

    private void activateShiftClickListener() {
        notesList.setOnMouseClicked(event -> {
            if(event.isShiftDown() && event.getButton() == MouseButton.PRIMARY) {
                if(!selectable.get() && !selectModeActive.get()) {
                    System.out.println("registered release");

                    Note selectedItem = notesList.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        selectable.set(true);
                        selectedItem.setSelected(true);
                        if(!buffer.contains(selectedItem)) buffer.add(selectedItem);
                        refreshNotesList();
                        selectMode();
                    }
                }
            }

        });
    }

    private void setNotesListBehavior() {
        notesList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // show note contents
            if(notesList.getItems().isEmpty()) {
                Editor.display();
            }

            if(newSelection != null) {
                Editor.display(newSelection.getID());
            }

            deleteButton.setOnAction(event -> {
                System.out.println("clicked delete button");
                if(buffer.isEmpty()) {
                    notesList.getItems().remove(newSelection);
                    DbStore.getInstance().deleteData(newSelection);
                    Editor.display();
                    selectable.set(false);
                } else {
                    for(Note note : buffer) {
                        System.out.println("removing ..." + note.toString());
                        notesList.getItems().remove(note);
                        DbStore.getInstance().deleteData(note);
                        refreshNotesList();
                        Editor.display();
                    }
                    buffer.clear();
                    selectable.set(false);
                    selectModeActive.set(false);
                }
            });
        });
    }

    private void setNoteList(Note currentNote, String title, String content) {
        // display note in list
        if(!notes.contains(currentNote)) {
            notes.add(currentNote);
        }
        notesList.setItems(notes);
        notesList.setId("nav-notes-list");
        Editor.display();

        setNotesListBehavior();

        // activateLongPressListener();
        activateShiftClickListener();

        refreshNotesList();

        Label label = new Label(title);
        label.setId("note-label");
        layout = new HBox(20);
        layout.setId("note-layout");
        layout.getChildren().addAll(label);
    }

    private void selectMode() {
        selectModeActive.set(true);
        System.out.println(selectable.get());
        notesList.setOnMousePressed(event -> {
            if(selectModeActive.get()) {
                System.out.println("sel mode: pressed button");
                Note selectedItem = notesList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    if(!selectedItem.getSelected()) {
                        System.out.println("sel mode: Selected item: " + selectedItem.getTitle());

                        selectedItem.setSelected(true);
                        if(!buffer.contains(selectedItem)) buffer.add(selectedItem);

                        System.out.println(selectedItem.getSelected() + buffer.toString());
                        refreshNotesList();
                    } else {
                        selectedItem.setSelected(false);
                        if(buffer.contains(selectedItem)) buffer.remove(selectedItem);
                        refreshNotesList();
                    }
                }
            }
        });
    }

    public Node getLayout() {
        return notesList;
    }

    public Button getAddNewNoteButton() {
        return addBtn;
    }

    private void updateContainer() {
        // container.getChildren().clear();
        if(container.getChildren().size() > 0 ) {
            container.getChildren().add(layout);
            System.out.println("UPDATING CONTAINER with " + layout.getChildren());
        }
        // TODO refresh container
        // UI.reload();
    }

    private void init() {
        createAddButton();
        // updateContainer();
        Editor.display();
    }

}
