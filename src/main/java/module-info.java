module notes {
    requires javafx.controls;
    requires javafx.fxml;


    opens notes to javafx.fxml;
    exports notes;
}