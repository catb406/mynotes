package notes;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.UUID;

public class Note {
    private Integer id;
    private String name;
    private String content;

    public Note(String name, Integer id, String content){
        this.name=name;
        this.id= id;
        this.content=content;
    }
    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public void setContent(String content, Connection connection) {
        PreparedStatement statement = null;
        Note note=null;
        try {
            statement=connection.prepareStatement("UPDATE notes SET content=? WHERE id_note=?");
            statement.setString(1, content);
            statement.setInt(2, id);
            Boolean exec=statement.execute();
            this.content=content;
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

    public void setName(String name) {
        this.name = name;
    }

    public void  setId(Integer id){
        this.id=id;
    }
}
