package duevdy;

import javafx.stage.Screen;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.javafx.FontIcon;

public class Note {
    private Logger logger = new Logger();
    private GridPane gridPane;
    private TextArea newNoteTextArea;
    private VBox vbox;
    private FontIcon icon = new FontIcon("mdi-plus");

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
        Button btn = new Button("", icon);
        btn.setId("note-btn");
        newNoteLayout.setPadding(new Insets(20));
        newNoteLayout.getChildren().addAll(newNoteTextArea, btn );
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
