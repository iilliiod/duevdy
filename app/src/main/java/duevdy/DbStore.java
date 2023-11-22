package duevdy;

import java.io.File;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.UUID;

import duevdy.Courses;
import duevdy.Logger;
import java.util.regex.*;

public class DbStore {
    private String filename;
    private final File dirpath = new File("store/");
    private LinkedList<Courses> courses = new LinkedList<Courses>();
    private Logger logger = new Logger();
    private static DbStore instance = null;

    private DbStore() {
        try {
            load();
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

    private File[] sortFiles(File[] files) {
        Arrays.sort(files, Comparator.comparing(File::getName));
        return files;
    }

    private void parseFile(File file) {
        String[] contents = file.getName().split("%");
        int len = contents.length;

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(contents[len - 2], format);

        // set title
        String title = contents[0];
        for(int i = 1; i < len - 2; i++) {
            title = title + " " + contents[i];
        }
        // clean up on aisle title
        String uuid = title.substring(0, 7);
        title = title.trim();
        title = title.substring(8);

        this.courses.addLast(new Courses(uuid, title, date, 
                    (contents[len - 1].equals("C") ? true:false)));
        logger.out("loaded " + file.getName());
    }

    private void load() throws IOException {
        // read and parse files
        if(!this.dirpath.exists()) {
            logger.out(this.dirpath.toString() + " gateway closed.");
            return;
        } 

        File path = new File(this.dirpath.toString());
        File[] files = path.listFiles();
        files = sortFiles(files);
        logger.out(path.toString() + " gateway open.");
        
        // parse 
        if(files != null) {
            for(File file : files) {
                if(file.isFile() && file.getName().contains("%") && finder(file.getName())) {
                    parseFile(file);
                }
            }
        } else {
            logger.out("empty store.");
        }
    }

    private String getFileName(Courses course) {
        // return the filename of the file's previous state 
        File file = findFile(course.getID());
        if(file != null) {
            String filename = file.getName();
            return filename;
        }
        return null;
    }

    private File findFile(String uuid) {
        // get the uuid matching the file's previous state
        File[] list = new File(dirpath.toString()).listFiles();

        if (list != null && list.length > 0) {
            for (File f : list) { 
                if(f.getName().startsWith(uuid)) {
                    logger.out("found " + f.getName());
                    return f;
                }
            } 
        } 
        return null;
    }

    public void update(Courses course) throws IOException {
        String fname = getFileName(course);
        String rename = generateName(course);
        // System.out.println("renaming " + fname + " to " + rename);
        logger.out("renaming " + fname + " to " + rename);

        // update file
        Path file = Paths.get(this.dirpath.toString()+"/"+fname);
        Files.move(file, file.resolveSibling(rename));
    }

    public Courses addData(String courseName, LocalDate courseDate) {
        Courses course = new Courses (generateID(), courseName, courseDate, false);
        try {
            logger.out(course.toString() + " record request sent.");
            if(storeData(course)) {
                logger.out("stored " + course.toString() + " as " + this.filename);
                this.courses.addLast(course);
                return course;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return course;
    }

    public void deleteData(Courses course) {
        // add robustness
        this.courses.remove(course);
    }

    public LinkedList<Courses> queryData() {
        return this.courses;
    }
    private String generateID() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        uuid = uuid.substring(0, Math.min(uuid.length(), 8));
        return uuid;
    }

    private String generateName(Courses course) {
        if (course.getID() == null) {
            System.out.println("huh ?");
        }
        String output = course.toString().replaceAll(" ", "%");
        return output;
    }

    private void setActiveFileName(Courses course) {
        this.filename = generateName(course);
    }

    private String getActiveFilename() {
        return this.filename;
    }

    private Boolean storeData(Courses course) throws IOException {
        // make folder
        if (!this.dirpath.exists()) {
            if (this.dirpath.mkdirs()) {
                logger.out("opened " + this.dirpath.toString() + " store.");
            } else {
                logger.out("error opening store.");
                return false;
            }
        }
        setActiveFileName(course);
        String fname = getActiveFilename();
        logger.out("browsing store with " + fname);
        // make file
        File file = new File(this.dirpath.toString()+"/"+fname);
        if(!file.exists()){
            if(file.createNewFile()){
                logger.out("creating " + fname + "...");
                return true;
            }
        }
        return false;
    }

    private Boolean writeFile(File file, String content) {
        // assumes file already exists and is opened
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
            return true;

        } catch (IOException e) {
            logger.out("cannot write to file " + file.getName() + " for whatever reason");
            return false;
        }
    }

    public Boolean deleteTodo(Courses course) {
        System.out.println("in the db rn with " + course.toString());
        setActiveFileName(course);
        String fname = getActiveFilename();

        System.out.println("deleting " + fname + "...");
        deleteData(course);
        // get the file
        // remove the file
        // remove the file from the list
        return false;
    }
}
