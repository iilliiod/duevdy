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
    
    public static void display(VBox newNoteLayout) {
        // TODO default display centered new note
        setNewNoteLayout(newNoteLayout);
        containerBox.getChildren().clear();
        containerBox.getChildren().add(newNoteLayout);
        setContainer();
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
        containerBox.getChildren().addAll(titleBox, textArea, newNoteLayout);
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
}
