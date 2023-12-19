package duevdy;

import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.control.ListCell;
import javafx.scene.Node;
import javafx.stage.Screen;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ScrollPane;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import org.kordamp.ikonli.javafx.FontIcon;

public class Search {
    private HBox searchBox = new HBox();
    private ScrollPane resultsPane;
    private ListView<AppElement> searchResultsListView;
    private TextField searchField;
    private DbStore dbStore = DbStore.getInstance();
    private String flag;
    private FontIcon searchIcon = new FontIcon("mdi-magnify");
    private Button searchBtn;
    private Double searchPosY;
    private Double searchPosX;

    public Search(String flag) {
        this.flag = flag;
        searchBtn = new Button("", searchIcon);
        searchBtn.setId("search-btn");
        searchIcon.setId("icon-search");
        searchField = new TextField();
        searchField.setId("search-field");
        searchResultsListView = new ListView<>();
        searchResultsListView.setId("search-result-list");
        searchBox.setId("search-box");

        searchField.setPromptText("Search...");

        setBehavior();
        setSearchBox(searchBtn);
    }


    public void setBehavior() {
        AtomicBoolean isHover = new AtomicBoolean(false);

        searchResultsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(AppElement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setId("nav-empty-custom-list-cell");
                } else {
                    if (item instanceof Note) {
                        Note note = (Note) item;
                        setText(note.getTitle());
                    } else {
                        Todo todo = (Todo) item;
                        setText(todo.getName());
                    }
                    setId("nav-custom-list-cell");
                }

                setOnMouseEntered(event -> {
                    isHover.set(true);
                    System.out.println("hovering");
                });
                setOnMouseExited(event -> {
                    isHover.set(false);
                    System.out.println("not hovering");
                });
            }
        });

        searchBtn.setOnAction(event -> setSearchBox(searchField));
        
        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(!newVal && !isHover.get() || oldVal && !isHover.get()) {
                setSearchBox(searchBtn);
                // remove results
                UI.removeSearchFromBase(getResultsContainer());
            } else {
                // TODO : i don't really like the behavior on this
            }
        });


        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSearchResults(newValue);
        });
        searchField.setOnKeyReleased(event -> handleSearch());

        searchResultsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                if(flag.equals("NOTES")) {
                    // searchField.clear();
                    Editor.display(newSelection.getID());
                    // show note contents
                    UI.removeSearchFromBase(getResultsContainer());
                } else {
                    System.out.println("working on the todos");
                }           
            }
        });
    }

    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        if(flag.equals("NOTES")) {
            LinkedList<Note> noteResults = searchNoteItems(query);
            displaySearchResults(noteResults);
        } else if (flag.equals("TODO")) {
            LinkedList<Todo> todoResults = searchTodoItems(query);
            displaySearchResults(todoResults);
        }
    }

    private LinkedList<Todo> searchTodoItems(String query) {
        LinkedList<Todo> results = new LinkedList<>();

        for (Todo todo : dbStore.queryTodo()) {
            if (todo.getName().toLowerCase().contains(query)) {
                results.add(todo);
            }
        }
        return results;
    }

    private LinkedList<Note> searchNoteItems(String query) {
        // TODO : make a trash bin
        LinkedList<Note> results = new LinkedList<>();
        for (Note note : dbStore.queryNotes()) {
            if (note.getTitle().toLowerCase().contains(query)) {
                results.add(note);
            }
        }
        return results;
    }

    private <T extends AppElement> void displaySearchResults(LinkedList<T> results) {
        searchResultsListView.getItems().clear();

        for (AppElement element : results) {
            if(element instanceof Todo) {
                searchResultsListView.getItems().add(((Todo) element));
            } else if (element instanceof Note){
                searchResultsListView.getItems().add(((Note) element));
            }
        }
        UI.addSearchToBase(getResultsContainer());
    }

    private void updateSearchResults(String query) {
        searchResultsListView.getItems().clear();

        for (Todo todo : dbStore.queryTodo()) {
            if (todo.getName().toLowerCase().contains(query.toLowerCase())) {
                searchResultsListView.getItems().add(todo);
            }
        }

        for (Note note : dbStore.queryNotes()) {
            if (note.getTitle().toLowerCase().contains(query.toLowerCase())) {
                searchResultsListView.getItems().add(note);
            }
        }
    }

    public ScrollPane getResultsContainer() {
        // TODO : fix bar
        resultsPane = new ScrollPane(searchResultsListView);
        resultsPane.setId("scroll-pane");
        // resultsPane.setMaxHeight(200);
        resultsPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return resultsPane;
    }

    private void setSearchBox(Node node) {
        searchBox.getChildren().clear();
        searchBox.getChildren().add(node);
    }
    public HBox getSearch() {
        return searchBox;
    }

}
