package duevdy;

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
    private ListView searchResultsListView;
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
        searchResultsListView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setId("nav-empty-custom-list-cell");
                } else {
                    setText(item);
                    setId("nav-custom-list-cell");
                }
            }
        });

        searchBtn.setOnAction(event -> setSearchBox(searchField));
        
        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(!newVal) {
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
        searchResultsListView.setOnMouseClicked(event -> handleSearchResultClick());
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
                searchResultsListView.getItems().add(((Todo) element).getName());
            } else if (element instanceof Note){
                searchResultsListView.getItems().add(((Note) element).getTitle());
            }
        }
        UI.addSearchToBase(getResultsContainer());
    }

    private void handleSearchResultClick() {
    //     String selectedItem = searchResultsListView.getSelectionModel().getSelectedItem();
    //     if (selectedItem != null) {
    //         // TODO : notes should open the selected file in the editor
    //         // TODO : todos should place the selected todo as the first todo in the container
    //         //        and flash/blink a color to notify the user
    // 
    //     }
    }

    private void updateSearchResults(String query) {
        searchResultsListView.getItems().clear();

        for (Todo todo : dbStore.queryTodo()) {
            if (todo.getName().toLowerCase().contains(query.toLowerCase())) {
                searchResultsListView.getItems().add(todo.getName());
            }
        }

        for (Note note : dbStore.queryNotes()) {
            if (note.getTitle().toLowerCase().contains(query.toLowerCase())) {
                searchResultsListView.getItems().add(note.getTitle());
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
