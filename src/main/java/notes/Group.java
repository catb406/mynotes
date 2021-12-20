package notes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.UUID;

public class Group {
    private final UUID id;
    private String name;
    private ObservableList<Note> notes = FXCollections.observableArrayList();


    public Group(String name){
        this.id=UUID.randomUUID();
        this.name=name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ObservableList<Note> getNotes() {
        return notes;
    }

    public void addNote (Note note){
        this.notes.add(note);
    }

    public void removeNote(UUID id){
        notes.removeIf(note -> note.getId() == id);
    }
}
