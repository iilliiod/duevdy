package duevdy;

import javafx.scene.control.TextFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TextInputControl;

import java.time.LocalDate;

import duevdy.DbStore;
import duevdy.Logger;

public class Editor {
    private static Logger logger = new Logger();
    private static TextArea textArea = new TextArea();
    private static ScrollPane container = new ScrollPane();
    private static HBox titleBox = new HBox();
    private static VBox containerBox = new VBox();
    private static VBox newNoteLayout;

    public Editor() {}
    
    public static void display() {
        init();
    }

    private static void setNewNoteLayout(VBox layout) {
        newNoteLayout = layout;
    }

    public static void display(String uuid) {
        VBox.setVgrow(textArea, javafx.scene.layout.Priority.ALWAYS);
        StringProperty titleProperty = new SimpleStringProperty(DbStore.getInstance().getNoteTitleContent(uuid)[0]);
        TextField header = new TextField(titleProperty.get());
        HBox.setHgrow(header, javafx.scene.layout.Priority.ALWAYS);
        String content = DbStore.getInstance().getNoteTitleContent(uuid)[1];

        // Update header if changed
        header.setTextFormatter(new TextFormatter<>(change -> {
            titleProperty.set(change.getControlNewText());
            return change;
        }));
        header.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                logger.out("updating note title value..." + titleProperty.get());
                change(uuid, titleProperty.get());
            } else {
                logger.out("EDITOR ERROR: updating header");
            }
        });

        textArea.setText(content);
        textArea.setWrapText(true);
        textArea.setOnKeyTyped(event -> {
            change(uuid, titleProperty.get());
        });

        titleBox.getChildren().clear();
        titleBox.getChildren().add(header);

        header.setId("editor-note-title");
        titleBox.setId("editor-title-box");
        textArea.setId("editor-text-area");
        container.setId("editor-container");
        containerBox.setId("editor-container-box");

        containerBox.getChildren().clear();
        containerBox.getChildren().addAll(titleBox, textArea);
        setContainer();
    }

    private static void setContainer() {
        container.setContent(containerBox);
        container.setFitToHeight(true);
        container.setFitToWidth(true);
    }

    private static void change(String uuid, String title) {
        System.out.println("editing note " + uuid + " title is: " + title);
        String updatedContent = textArea.getText();
        if (!updatedContent.isEmpty()) {
            DbStore.getInstance().updateNote(uuid, title, updatedContent, LocalDate.now());
        } else {
            System.out.println("editor content?");
        }
    }

    public static Control getTextArea() {
        return container;
    }

    private static void clearPlaceholderText(TextInputControl control, String placeholder) {
        if (control.getText().equals(placeholder)) {
            control.setText("");
        }
    }

    private static void init() {
        VBox.setVgrow(textArea, javafx.scene.layout.Priority.ALWAYS);
        StringProperty titleProperty = new SimpleStringProperty("Title...");
        TextField header = new TextField(titleProperty.get());
        HBox.setHgrow(header, javafx.scene.layout.Priority.ALWAYS);
        StringBuilder content = new StringBuilder("Content...");

        // Clear placeholder text when header gains focus or Tab key is pressed
        header.setOnMouseClicked(event -> {
            clearPlaceholderText(header, "Title...");
            header.setId("editor-note-title");
        });

        header.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                System.out.println("tabbed in");
                clearPlaceholderText(header, "Title...");
                header.setId("editor-note-title");
            }
        });

        // Clear placeholder text when textArea gains focus or Tab key is pressed
        textArea.setOnMouseClicked(event -> {
            clearPlaceholderText(textArea, "Content...");
            textArea.setId("editor-text-area");
        });

        textArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                System.out.println("tabbed in");
                clearPlaceholderText(textArea, "Content...");
                textArea.setId("editor-text-area");
            }
        });


        // Update header if changed
        header.setTextFormatter(new TextFormatter<>(change -> {
            titleProperty.set(change.getControlNewText());
            return change;
        }));
        header.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused && !content.toString().equals("Content...") && !content.isEmpty()) {
                logger.out("updating note title value..." + titleProperty.get());
                // make new note
                content.setLength(0);
                content.append(titleProperty.get());
                String title = titleProperty.get().contains(" ") ? 
                    titleProperty.get().substring(0, titleProperty.get().indexOf(" ")) : titleProperty.get();
                System.out.println("NEW TITLE: " + title);
                Note newNote = DbStore.getInstance().addNote(title, 
                        LocalDate.now(), content.append("\n").append(textArea.getText()).toString());
                System.out.println("new note is: " + newNote.getTitle());
                if(newNote != null) {
                    System.out.println("added " + newNote.getID());
                    new NoteView().loadNote(newNote.getID());
                    logger.out(newNote.toString());
                    UI.updateScene();
                }
            } else {
                logger.out("EDITOR ERROR: updating header");
            }
        });

        textArea.setText(content.toString());
        textArea.setWrapText(true);
        textArea.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean oldVal, Boolean newVal) {
                if(!newVal && !titleProperty.get().toString().equals("Title...") && !titleProperty.get().toString().isEmpty()) {
                    // make a new note
                    System.out.println("making a new note " + titleProperty.get().toString());

                    // add to database (returns a Note object)
                    content.setLength(0);
                    content.append(titleProperty.get());
                    String title = titleProperty.get().contains(" ") ? 
                        titleProperty.get().substring(0, titleProperty.get().indexOf(" ")) : titleProperty.get();
                    System.out.println("NEW TITLE: " + title);
                    Note newNote = DbStore.getInstance().addNote(title, 
                            LocalDate.now(), content.append("\n").append(textArea.getText()).toString());
                    if(newNote != null) {
                        System.out.println("added " + newNote.getID());
                        new NoteView().loadNote(newNote.getID());
                        logger.out(newNote.toString());
                        UI.updateScene();
                    }
                } else {
                    System.out.println("invalid args");
                }
            }
        });


        titleBox.getChildren().clear();
        titleBox.getChildren().add(header);

        header.setId("editor-new-note-title");
        titleBox.setId("editor-title-box");
        textArea.setId("editor-new-text-area");
        container.setId("editor-container");
        containerBox.setId("editor-container-box");

        containerBox.getChildren().clear();
        containerBox.getChildren().addAll(titleBox, textArea);
        setContainer();
    }
}
