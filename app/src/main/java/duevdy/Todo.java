package duevdy;
import java.time.LocalDate;
import duevdy.AppElement;

public class Todo implements AppElement {
    private String name;
    private LocalDate dueDate;
    private Boolean completed;
    private String content;
    private final String ID;

    public Todo (String uuid, String name, LocalDate dueDate, Boolean completed) {
        this.ID = uuid;
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
    public String getID() {
        return ID;
    }

    public String toString() {
        return this.getID() + " " + this.name + " " + this.dueDate.toString() + " " + (this.completed ? "C" : "X");
    }
}
