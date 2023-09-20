package app;
import java.util.Date;

public class Courses {
    private String name;
    private String dueDate;
    private Boolean completed;

    public Courses(String name, String dueDate, Boolean completed) {
        this.name = name;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getCompleted() {
        return completed;
    }
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String toString() {
        return this.name + " " + this.dueDate + " " + (this.completed ? "C" : "");
    }
}
