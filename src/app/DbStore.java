package app;

import java.io.File;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;
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

    public DbStore() {
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                System.out.println(file);
                if(file.isFile() && file.getName().contains("%") && finder(file.getName())) {
                    String[] contents = file.getName().split("%");
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    for(String c : contents) {
                        System.out.println(c);
                    }
                    LocalDate date = LocalDate.parse(contents[1], format);
                    this.courses.addLast(new Courses(contents[0], date, 
                                (contents.length == 2 ? false : (contents[2].equals("C") ? true:false))));
                    logger.out("loaded " + file.getName());
                }
            }
        } else {
            logger.out("empty store.");
        }
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
        this.courses.remove(course.getName());
    }

    public LinkedList<Courses> queryData() {
        return this.courses;
    }

    private String generateName(Courses course) {
        // add robustness later
        String output = course.toString().replaceAll(" ", "%");
        return output;
    }

    public String getFilename(Courses course) {
        this.filename = generateName(course);
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
        String fname = getFilename(course);
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
}
