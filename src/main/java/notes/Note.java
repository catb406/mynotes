package notes;

import java.util.UUID;

public class Note {
    private final UUID id;
    private String name;
    private String content = "";

    public Note(String name){
        this.name=name;
        this.id= UUID.randomUUID();
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setName(String name) {
        this.name = name;
    }
}
