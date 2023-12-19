package duevdy;

import java.util.Scanner;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.UUID;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;

import duevdy.Settings;

public class DbStore {
    private String filename;
    private final Path todoDirpath = Paths.get("store/todo/");
    private final Path noteDirpath = Paths.get("store/notes/");
    private final Path settingsDirpath = Paths.get("settings/");
    private final File todoDir = new File(todoDirpath.toString());
    private final File noteDir = new File(noteDirpath.toString());
    private final File settingsDir = new File(settingsDirpath.toString());
    private LinkedList<Todo> todos = new LinkedList<Todo>();
    private LinkedList<Note> notes = new LinkedList<Note>();
    private Logger logger = new Logger();
    private static DbStore instance = null;

    private IntegerProperty completedTodoCnt = new SimpleIntegerProperty(0);

    public void setCompletedTodoCnt() {
        completedTodoCnt.set(completedTodoCnt.get() + 1);
    }
    public void setIncompletedTodoCnt() {
        completedTodoCnt.set(completedTodoCnt.get() - 1);
    }
    public int getCompletedTodoCnt() {
        return completedTodoCnt.get();
    }
    public IntegerProperty completedTodoCntProperty() {
        return completedTodoCnt;
    }

    private DbStore() {
        try {
            load(todoDir);
            load(noteDir);
            load(settingsDir);
            sortTodo(todos);
            sortList(notes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DbStore getInstance() {
        if (instance == null) {
            instance = new DbStore();
        }
        return instance;
    }

    private Boolean finder(String string) {
        int count = 0;
        for (char c : string.toCharArray()) {
            if (c == '%') {
                count++;
            }
        }

        if (count >= 2) {
            return true;
        }
        return false;
    }

    private <T extends AppElement> void sortList(LinkedList<T> elements) {
        elements.sort(Comparator.comparing(AppElement::getDate).reversed());
    }
    private void sortTodo(LinkedList todos) {
        sortList(todos);
        todos.sort(Comparator.comparing(Todo::getCompleted));
    }

    private void parseSettings(File file) {
        String fname = file.getName();

        if(fname != "settings") {
            System.out.println("settings file not loaded");
        } else System.out.println("settings loaded.");

        Settings.getInstance();
        logger.out("loaded " + file.getName());
    }

    private void parseNotes(File file) {
        String[] contents = file.getName().split("%");
        int len = contents.length;

        // set title
        String title = contents[0];
        for (int i = 1; i < len - 1; i++) {
            title = title + " " + contents[i];
        }
        // clean up on aisle title
        String uuid = title.substring(0, 7);
        title = title.trim();
        title = title.substring(8);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(contents[len - 1], format);

        // open file for title
        // take max first 25 chars of first line
        try {
            Scanner scanner = new Scanner(file);
            title = scanner.nextLine();
            if(title.length() > 25) {
                title = title.substring(0, 25) + "...";
            } 
            scanner.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } 

        this.notes.addLast(new Note(uuid, title, date));
        logger.out("loaded " + file.getName());
    }

    private void parseTodo(File file) {
        String[] contents = file.getName().split("%");
        int len = contents.length;

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(contents[len - 2], format);

        // set title
        String title = contents[0];
        for (int i = 1; i < len - 2; i++) {
            title = title + " " + contents[i];
        }
        // clean up on aisle title
        String uuid = title.substring(0, 7);
        title = title.trim();
        title = title.substring(8);

        this.todos.addLast(new Todo(uuid, title, date,
                (contents[len - 1].equals("C") ? true : false)));
        logger.out("loaded " + file.getName());
    }

    private void load(File dirpath) throws IOException {
        // read and parse files
        if (!dirpath.exists()) {
            logger.out(dirpath.toString() + " gateway closed.");
            return;
        }

        File path = new File(dirpath.toString());
        File[] files = path.listFiles();
        logger.out(path.toString() + " gateway open.");

        // parse
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().contains("%") && finder(file.getName())) {
                    String dirPath = dirpath.toString();

                    if (dirPath.equals(noteDirpath.toString())) {
                        parseNotes(file);
                    } else if (dirPath.equals(todoDirpath.toString())) {
                        parseTodo(file);  
                    } else if (dirPath.equals(settingsDirpath.toString())) {
                        parseSettings(file);
                    } else {
                        System.out.println("Error"); 
                    }
                }
            }
        } else {
            logger.out("empty store.");
        }
    }

    private String getFileName(Todo todo) {
        // TODO: refactor for Notes to be able to use it as well
        // return the filename of the file's previous state
        File file = findFile(todo.getID(), todoDir);
        if (file != null) {
            String filename = file.getName();
            return filename;
        }
        return null;
    }

    private File findFile(String uuid, File dirpath) {
        // get the uuid matching the file's previous state
        File[] list = new File(dirpath.toString()).listFiles();

        if (list != null && list.length > 0) {
            for (File f : list) {
                if (f.getName().startsWith(uuid)) {
                    logger.out("found " + f.getName());
                    return f;
                }
            }
        }
        return null;
    }

    public void update(Todo todo) throws IOException {
        String fname = getFileName(todo);
        String rename = generateName(todo);
        // System.out.println("renaming " + fname + " to " + rename);
        logger.out("renaming " + fname + " to " + rename);

        // update file
        Path file = Paths.get(this.todoDir.toString() + "/" + fname);
        Files.move(file, file.resolveSibling(rename));
    }

    public Boolean addSettings(Settings settings, String content) throws IOException {
        if (!this.settingsDir.exists()) {
            if (this.settingsDir.mkdirs()) {
                logger.out("opened " + this.settingsDir.toString() + " settings.");
            } else {
                logger.out("error opening settings.");
                return false;
            }
        }
        this.filename = "settings";
        String fname = getActiveFilename();
        logger.out("browsing settings with " + fname);
        // make file
        File file = new File(this.settingsDir.toString() + "/" + fname);
        if (!file.exists()) {
            if (file.createNewFile()) {
                logger.out("creating " + fname + "...");
                writeFile(file, content);
                return true;
            }
        }
        return false;

    }

    public Todo addTodo(String todoName, LocalDate todoDate) {
        Todo todo = new Todo(generateID(), todoName, todoDate, false);
        try {
            logger.out(todo.toString() + " todo record request sent.");
            if (storeTodo(todo)) {
                logger.out("stored " + todo.toString() + " as " + this.filename);
                this.todos.addLast(todo);
                return todo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todo;
    }

    public Note addNote(String title, LocalDate timeNow, String content) {
        Note note = new Note(generateID(), title, timeNow);
        try {
            logger.out(note.toString() + " note record request sent.");
            if (storeNote(note, content)) {
                logger.out("stored " + note.toString() + " as " + this.filename);
                this.notes.addLast(note);
                return note;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return note;
    }

    private void deleteFile(AppElement element) {
        // find file based on uuid
        // use dirpaths
        // delete file if it exists

        String uuid = element.getID();
        File[] files;

        if(element instanceof Note) {
            files = noteDir.listFiles();
        } else {
            files = todoDir.listFiles();
        }

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();

                // Check if the file name contains the ID
                if (fileName.startsWith(uuid)) {
                    // Delete the file
                    if (file.delete()) {
                        System.out.println("File with ID " + uuid + " deleted successfully.");
                    } else {
                        System.out.println("Failed to delete the file with ID " + uuid);
                    }
                    break; 
                }
            }
        }
    }

    public void deleteData(AppElement element) {
        // add robustness
        if(element instanceof Todo) {
            Todo todo = (Todo) element;
            this.todos.remove(todo);
        } 
        deleteFile(element);
    }

    public LinkedList<Todo> queryTodo() {
        return this.todos;
    }
    public LinkedList<Note> queryNotes() {
        return this.notes;
    }

    private String generateID() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        uuid = uuid.substring(0, Math.min(uuid.length(), 8));
        return uuid;
    }

    private <T extends AppElement> String generateName(T element) {
        if (element.getID() == null) {
            System.out.println("huh ?");
        }
        String output = element.toString().replaceAll(" ", "%");
        return output;
    }

    private <T extends AppElement> void setActiveFileName(T element) {
        this.filename = generateName(element);
    }

    private String getActiveFilename() {
        return this.filename;
    }
    private Boolean storeNote(Note note, String content) throws IOException {
        // make folder
        if (!this.noteDir.exists()) {
            if (this.noteDir.mkdirs()) {
                logger.out("opened " + this.noteDir.toString() + " store.");
            } else {
                logger.out("error opening store.");
                return false;
            }
        }
        setActiveFileName(note);
        String fname = getActiveFilename();
        logger.out("browsing store with " + fname);
        // make file
        File file = new File(this.noteDir.toString() + "/" + fname);
        if (!file.exists()) {
            if (file.createNewFile()) {
                logger.out("creating " + fname + "...");
                writeFile(file, content);
                return true;
            }
        }
        return false;
    }

    private Boolean writeFile(File file, String content) {
        // assumes file already exists and is opened
        try {
            // TODO: might need bufferedwriter for appending
            FileWriter writer = new FileWriter(file);
            System.out.println("WRITING: " + content);
            writer.write(content);
            writer.close();
            return true;

        } catch (IOException e) {
            logger.out("cannot write to file " + file.getName() + " for whatever reason");
            return false;
        }
    }
    int c = 0;

    public String[] getNoteTitleContent(String uuid) { // TODO: make a content class & return Content class
        /*
           output[0] = title;
           output[1] = content;
        */
        // get file based on uuid
        String[] output = new String[2];

        File file = getNote(uuid);
        StringBuilder content = new StringBuilder();
        String title = "";
        c++;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineCount = 0;
            while((line = reader.readLine()) != null) {
                if(lineCount < 1) {
                    title = line;
                }
                lineCount++;
                if(lineCount >= 1) {
                    content.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // System.out.println("note title: " + title);
        // System.out.println("content: " + content.substring(content.indexOf("\n") + 1).toString());

        output[0] = title;
        output[1] = content.substring(content.indexOf("\n") + 1).toString().trim();

        return output;
    }

    public File getNote(String uuid) {
        // gets file based on uuid
        // returns file location
        File file = findFile(uuid, noteDir);

        return file;
    }

    public void updateNote(String uuid, String title, String content, LocalDate dateModified) {
        // get file
        // rewrite file contents NOTE: not an optimal solution, but it works for now
        // change Note title if title changed

        logger.out("db note content: " + content);
        for(Note n : queryNotes()) {
            if (n.getID().equals(uuid)) {
                String oldTitle = n.getTitle();
                n.setDateModified(dateModified);
                if(!oldTitle.equals(title)) {
                    n.setTitle(title);
                }
                break;
            }
        }

        StringBuilder newFileContents = new StringBuilder(title).append("\n").append(content);

        File file = getNote(uuid);
        writeFile(file, newFileContents.toString());
    }

    private Boolean storeTodo(Todo todo) throws IOException {
        // make folder
        if (!this.todoDir.exists()) {
            if (this.todoDir.mkdirs()) {
                logger.out("opened " + this.todoDir.toString() + " store.");
            } else {
                logger.out("error opening store.");
                return false;
            }
        }
        setActiveFileName(todo);
        String fname = getActiveFilename();
        logger.out("browsing store with " + fname);
        // make file
        File file = new File(this.todoDir.toString() + "/" + fname);
        if (!file.exists()) {
            if (file.createNewFile()) {
                logger.out("creating " + fname + "...");
                return true;
            }
        }
        return false;
    }

    public Boolean deleteTodo(Todo todo) {
        System.out.println("in the db rn with " + todo.toString());
        setActiveFileName(todo);
        String fname = getActiveFilename();

        System.out.println("deleting " + fname + "...");
        deleteData(todo);
        // get the file
        // remove the file
        // remove the file from the list
        return false;
    }
}
