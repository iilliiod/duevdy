package app;

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
import app.Courses;
import app.Logger;
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
        String title = "";
        String[] contents = file.getName().split("%");
        int len = contents.length;

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(contents[len - 2], format);

        // set title
        for(int i = 0; i < len - 2; i++) {
            title = title + " " + contents[i];
        }
        // clean up on aisle title
        title = title.trim();

        this.courses.addLast(new Courses(title, date, 
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
        
        // parse and handle empty contents[2]
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

    private String getFilename(Courses course) {
        // return the filename of the file's previous state 
        String filename = generateName(course);
        File file = findFile(filename);
        filename = file.getName();
        return filename;
    }

    private File findFile(String filename) {
        // get the filename matching the file's previous state
        File file = new File(filename);
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(filename.substring(0, filename.length() - 2));
            }
        };

        File[] list = new File(dirpath.toString()).listFiles(filter);

        if (list != null) {
            for (File f : list) { 
                if(f.getName().startsWith(filename.substring(0, filename.length() - 2))) {
                    return f;
                }
            } 
        } 
        return file;
    }

    public void update(Courses course) throws IOException {

        // TODO clean this up a little
        String fname = getFilename(course);
        String rename = generateName(course);
        logger.out("renaming " + fname + " to " + rename);
        // logger.out("renaming " + fname + " to " + rename);

        // update file
        Path file = Paths.get(this.dirpath.toString()+"/"+fname);
        Files.move(file, file.resolveSibling(rename));
    }

    public Boolean addData(Courses course) {
        try {
            logger.out(course.toString() + " record request sent.");
            if(storeData(course)) {
                logger.out("stored " + course.toString() + " as " + this.filename);
                this.courses.addLast(course);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteData(Courses course) {
        // add robustness
        this.courses.remove(course);
    }

    public LinkedList<Courses> queryData() {
        return this.courses;
    }

    private String generateName(Courses course) {
        // add robustness later
        String output = course.toString().replaceAll(" ", "%");
        return output;
    }

    private void setFileName(Courses course) {
        this.filename = generateName(course);
    }

    private String getFilename() {
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
        setFileName(course);
        String fname = getFilename();
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
}
