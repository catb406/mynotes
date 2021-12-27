package notes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Group {
    private final Integer id;
    private String name;
    private ObservableList<Note> notes = FXCollections.observableArrayList();


    public Group(String name, Integer id){
        this.id=id;
        this.name=name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ObservableList<Note> getNotes() {
        return notes;
    }

    public Note addNoteToDb (String name, Connection connection){
        PreparedStatement statement = null;
        Note note=null;
        try {
            statement=connection.prepareStatement("INSERT INTO notes (name, id_group) VALUES (?,?) RETURNING id_note");
            statement.setString(1,  name );
            statement.setInt(2, this.getId());
            ResultSet rs=statement.executeQuery();
           while (rs.next()){
               note=new Note(name, rs.getInt("id_note"),"");
               this.notes.add(note);
           }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return note;
    }

    public void addNote(Note note){
        this.notes.add(note);
    }

    public void removeNote(Integer id, Connection connection){
        PreparedStatement statement = null;
        try {
            statement=connection.prepareStatement("DELETE FROM notes WHERE id_note=?");
            statement.setInt(1, id);
            statement.execute();
            notes.removeIf(note -> note.getId() == id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
