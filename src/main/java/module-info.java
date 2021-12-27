module notes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens notes to javafx.fxml;
    exports notes;
}