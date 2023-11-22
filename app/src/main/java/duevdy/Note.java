package duevdy;

import javafx.stage.Screen;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.DatePicker;

import java.awt.Event;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*; // List

import duevdy.Logger;
import duevdy.Courses;
import duevdy.DbStore;
import duevdy.UI;
import duevdy.Library;

public class Note {
    private DbStore dbStore = DbStore.getInstance();
    private Logger logger = new Logger();
    private GridPane gridPane;
    private TextArea newNoteTextArea;
    private VBox vbox;

    public Note() {
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

    private void setVbox() {
        vbox = new VBox(8);
        VBox newNoteLayout = new VBox(8);
        newNoteLayout.setPadding(new Insets(20));
        newNoteLayout.getChildren().addAll(newNoteTextArea);
        newNoteLayout.setId("note-layout");
        // TODO: noteView needs noteLayout
        vbox.getChildren().addAll(newNoteLayout);
        vbox.setId("note-vbox");
    }
    private void setGridPane() {
        gridPane = new GridPane();
        gridPane.getChildren().addAll(vbox);
    }
    public VBox getVbox() {
        return vbox;
    }

    private void init() {
        createNewNoteTextArea();
        setVbox();
        setGridPane();
    }
}
