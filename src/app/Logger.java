package app;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class Logger {
    private final File logLoc = new File("logs/");
    private final File output = new File(logLoc.toString() + "/log.txt");
    private FileWriter writer;

    public void out(String contents) {
        try {
            if(!this.logLoc.exists()) {
                if(this.logLoc.mkdirs()) {
                }
            }
            if(this.output.exists()) {
                this.writer = new FileWriter(output, true);
                this.writer.write(contents + "\n");
                this.writer.close();
            } else {
                this.writer = new FileWriter(output, false);
                this.writer.write(contents + "\n");
                this.writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
