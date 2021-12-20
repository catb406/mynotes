package notes;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class NotesController {
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
            Group group=new Group(name);
            groups.add(group);
            popupGroup.setVisible(false);
            groupsList.getSelectionModel().select(group);
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

            Note note=new Note(name);
            Group group=groupsList.getSelectionModel().getSelectedItem();
            if (group==null){
                group=groups.get(0);
            }
            group.addNote(note);
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
                groupsList.getItems().remove(selectedID);
        }
    }

    @FXML
    protected void onDeleteNoteBtnClick(){
        int selectedID = notesList.getSelectionModel().getSelectedIndex();
        if (selectedID!=-1){
            notesList.getItems().remove(selectedID);
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
            item.setContent(content);
        }
    }


    @FXML
    public void initialize() {
        note.setEditable(false);
        note.setText("выберите заметку...");
        groups.add(new Group("моя группа"));
        groupsList.setItems(groups);
        groupsList.setCellFactory(param -> new GroupCell());
        groupsList.getSelectionModel().selectedItemProperty().addListener(this::selectEntity);

        notesList.setItems(groups.get(0).getNotes());
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
}