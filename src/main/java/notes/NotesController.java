package notes;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class NotesController {
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/mynotes";
    static final String USER = "postgres";
    static final String PASS = "1";
    private Integer count = 0;

    ObservableList<Group> groups = FXCollections.observableArrayList();

    @FXML
    private Button deleteBtn;

    @FXML
    private ListView<Group> groupsList;

    @FXML
    private Button newGroupBtn;

    @FXML
    private Button newNoteBtn;

    @FXML
    private TextArea note;

    @FXML
    private ListView<Note> notesList;

    @FXML
    private Button saveBtn;

    @FXML
    private Button okGroupBtn;

    @FXML
    private Button okNoteBtn;

    @FXML
    private AnchorPane popupGroup;

    @FXML
    private AnchorPane popupNote;

    @FXML
    private AnchorPane popupGroupEdit;

    @FXML
    private AnchorPane popupNoteEdit;

    @FXML
    private TextField groupNameInput;
    @FXML
    private TextField noteNameInputField;
    @FXML
    private Label emptyNoteNameErr;

    @FXML
    private Label emptyGroupNameErr;

    @FXML
    private Label emptyNoteNameErr1;

    @FXML
    private Label emptyGroupNameErr1;

    @FXML
    private Button cancelGroupBtn;

    @FXML
    private Button cancelNoteBtn;

    @FXML
    private Button editNoteBtn;

    @FXML
    private Button editGroupBtn;

    @FXML
    private TextField noteNameInputEditField;

    @FXML
    private TextField groupNameInputEdit;

    @FXML
    protected void onEditNoteBtn(){
        Note note=notesList.getSelectionModel().getSelectedItem();
        if (note!=null){
            popupNoteEdit.setVisible(true);
            noteNameInputEditField.setText(note.getName());
        }
    }

    @FXML
    protected void onEditGroupBtn(){
        Group group=groupsList.getSelectionModel().getSelectedItem();
        if (group!=null){
            popupGroupEdit.setVisible(true);
            groupNameInputEdit.setText(group.getName());
        }
    }

    @FXML
    protected void onCancelGroupBtn(){
        emptyGroupNameErr.setVisible(false);
        popupGroup.setVisible(false);
        emptyGroupNameErr1.setVisible(false);
        popupGroupEdit.setVisible(false);
    }

    @FXML
    protected void onCancelNoteBtn(){
        emptyNoteNameErr.setVisible(false);
        popupNote.setVisible(false);
        emptyNoteNameErr1.setVisible(false);
        popupNoteEdit.setVisible(false);
    }

    @FXML
    protected void onOkGroupBtnClick(){
        String name = groupNameInput.getText();

        if (Objects.equals(name, "")){
            emptyGroupNameErr.setVisible(true);
        } else {
            emptyGroupNameErr.setVisible(false);
            addGroup(name);
            popupGroup.setVisible(false);
            groupsList.getSelectionModel().select(groups.get(groups.size()-1));
            groupNameInput.setText("");
        }
    }

    @FXML
    protected void onOkGroupEditBtnClick(){
        String name = groupNameInputEdit.getText();

        if (Objects.equals(name, "")){
            emptyGroupNameErr1.setVisible(true);
        } else {
           Group group=groupsList.getSelectionModel().getSelectedItem();
           if (group!=null){
               popupGroupEdit.setVisible(false);
               groupNameInput.setText("");
               group.setName(name);
               groupsList.setItems(groups);
           } else {
               System.out.println("NULL");
           }
        }
    }

    @FXML
    protected void onOkNoteEditBtnClick(){
        String name = noteNameInputEditField.getText();

        if (Objects.equals(name, "")){
            emptyNoteNameErr1.setVisible(true);
        } else {
            Note note=notesList.getSelectionModel().getSelectedItem();
            if (note!=null){
                note.setName(name);
                popupNoteEdit.setVisible(false);
                notesList.getSelectionModel().select(note);
                noteNameInputField.setText("");
            }
        }
    }


    @FXML
    protected void onOkNoteBtnClick(){
        String name = noteNameInputField.getText();
        if (Objects.equals(name, "")){
            emptyNoteNameErr.setVisible(true);
        } else {
            emptyGroupNameErr.setVisible(false);

            Group group=groupsList.getSelectionModel().getSelectedItem();
            if (group==null){
                group=groups.get(0);
            }
            Note note=group.addNoteToDb(name, Objects.requireNonNull(getDbConnection()));
            popupNote.setVisible(false);
            notesList.getSelectionModel().select(note);
            noteNameInputField.setText("");
        }
    }

    @FXML
    protected void onNewGroupBtnClick() {
        popupGroup.setVisible(true);
    }


    @FXML
    protected void onDeleteGroupBtnClick(){
        int selectedID=groupsList.getSelectionModel().getSelectedIndex();
        if (selectedID!=-1){
               Group group=groupsList.getItems().get(selectedID);
               removeGroup(group.getId());
        }
    }

    @FXML
    protected void onDeleteNoteBtnClick(){
        int selectedID = notesList.getSelectionModel().getSelectedIndex();
        if (selectedID!=-1){
            Note note=notesList.getItems().get(selectedID);
            int selectedGroupID=groupsList.getSelectionModel().getSelectedIndex();
            groups.get(selectedGroupID).removeNote(note.getId(), getDbConnection());
        }
    }

    @FXML
    protected void onNewNoteBtnClick(){
        popupNote.setVisible(true);
    }

    @FXML
    protected void onSaveBtnClick(){
        String content = note.getText();

        Note item = notesList.getSelectionModel().getSelectedItem();
        if (item!=null){
            item.setContent(content, getDbConnection());
        }
    }


    @FXML
    public void initialize() {
        try {
            createTables();
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return;
        }
        getGroups();
        note.setEditable(false);
        note.setText("выберите заметку...");
        groupsList.setItems(groups);
        groupsList.setCellFactory(param -> new GroupCell());
        groupsList.getSelectionModel().selectedItemProperty().addListener(this::selectEntity);

        notesList.setCellFactory(param -> new NoteCell());
        notesList.getSelectionModel().selectedItemProperty().addListener(this::selectEntity);
    }


    private static class GroupCell extends ListCell<Group> {
        @Override
        protected void updateItem(Group item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.getName());
        }
    }

    // реакция на выбор в списке
    void selectEntity(ObservableValue<? extends Group> observable, Group oldValue, Group newValue) {
        if (newValue!=null){
            notesList.setItems(newValue.getNotes());
            notesList.setCellFactory(param -> new NoteCell());
            notesList.getSelectionModel().selectedItemProperty().addListener(this::selectEntity);
        }
    }

    private static class NoteCell extends ListCell<Note> {
        @Override
        protected void updateItem(Note item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.getName());
        }
    }

    void selectEntity(ObservableValue<? extends Note> observable, Note oldValue, Note newValue) {
        Note item  = notesList.getSelectionModel().getSelectedItem();
        if (item==null){
            note.setText("выберите заметку...");
            note.setEditable(false);
        } else {
            String userData =item.getContent();
            note.setText(userData);
            note.setEditable(true);
        }
    }

    private static Connection getDbConnection(){
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PSQL JDBC Driver?");
            e.printStackTrace();
            return null;
        }
        System.out.println("PostgreSQL JDBC Driver successfully connected");
        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return null;
        }

        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
        return connection;
    }

    private void createTables() throws SQLException {
        Connection connection = null;
        Statement statement = null;

        String createNotesSQL = "CREATE TABLE IF NOT EXISTS notes ("
                + "id_note SERIAL PRIMARY KEY, "
                + "id_group INTEGER NOT NULL, "
                + "content VARCHAR(20), "
                + "name VARCHAR(20) NOT NULL, "
                + "CONSTRAINT fk_group FOREIGN KEY(id_group)  REFERENCES groups(id_group)" +
                ")";
        String createGroupsSQL = "CREATE TABLE IF NOT EXISTS groups ("
                + "id_group SERIAL PRIMARY KEY, "
                + "name VARCHAR(20) NOT NULL "
                + ")";

        try {
            connection=getDbConnection();
            statement = connection.createStatement();

            // выполнить SQL запрос
            statement.execute(createGroupsSQL);
            System.out.println("Table \"groups\" is created!");
            statement.execute(createNotesSQL);
            System.out.println("Table \"notes\" is created!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private void getGroups() {
        Connection connection = null;
        Statement statement =  null;

        try {
            connection=getDbConnection();
            assert connection != null;
            statement=connection.createStatement();
            String selectGroupsQuery = "SELECT * FROM groups";
            ResultSet rs = statement.executeQuery(selectGroupsQuery);
            while (rs.next()){
                String name = rs.getString("name");
                Integer id =  rs.getInt("id_group");
                Group group=new Group(name, id);
                String selectNotesQuery = "SELECT * FROM notes WHERE id_group="+id;
                Statement newStatement = connection.createStatement();
                ResultSet newrs = newStatement.executeQuery(selectNotesQuery);
                while (newrs.next()){
                    String noteName =newrs.getString("name");
                    Integer idNote = newrs.getInt("id_note");
                    String content=newrs.getString("content");
                    Note note = new Note(noteName, idNote, content);
                    group.addNote(note);
                }
                groups.add(group);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            try {
                statement.close();
            } catch (SQLException e){
                System.out.println(e.getMessage());
            }
            try {
                connection.close();
            } catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private void addGroup(String name){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection=getDbConnection();
            statement=connection.prepareStatement("INSERT INTO groups (name) VALUES (?) RETURNING id_group");
            statement.setString(1,   name );
            ResultSet rs=statement.executeQuery();
            while (rs.next()){
                Group group=new Group(name, rs.getInt("id_group"));
                groups.add(group);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void removeGroup(Integer id){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection=getDbConnection();
            statement=connection.prepareStatement("DELETE FROM notes WHERE id_group IN (SELECT ? FROM groups)");
            statement.setInt(1, id);
            statement.execute();
            statement=connection.prepareStatement("DELETE FROM groups WHERE id_group=?");
            statement.setInt(1, id);
            statement.execute();
            groups.removeIf(note -> note.getId() == id);

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