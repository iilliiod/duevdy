package app;
// import java.io.File;
import java.time.LocalDate;

public class Courses {
    private String name;
    private LocalDate dueDate;
    private Boolean completed;
    private String content;

    public Courses(String name, LocalDate dueDate, Boolean completed) {
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getCompleted() {
        return completed;
    }
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String toString() {
        return this.name + " " + this.dueDate.toString() + " " + (this.completed ? "C" : "");
    }
}
