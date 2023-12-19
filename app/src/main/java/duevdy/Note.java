package duevdy;

import duevdy.AppElement;
import java.time.LocalDate;

public class Note implements AppElement {
    private String title = "";
    private LocalDate dateModified;
    private final String ID;
    private Boolean isSelected;

    public Note(String uuid, String title, LocalDate dateModified) {
        this.ID = uuid;
        this.title = title;
        this.dateModified = dateModified;
        this.isSelected = false;
    }

    public void setSelected(Boolean selected) {
        this.isSelected = selected;
    }
    public Boolean getSelected() {
        return this.isSelected;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public LocalDate getDate() {
        return dateModified;
    }
    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }
    public String getID() {
        return ID;
    }
    public String toString() {
        return this.getID() + " " + this.title + " " + this.dateModified.toString();
    }
}
