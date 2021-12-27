package notes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

//STEP 1. Import required packages
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
public class NotesApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(NotesApplication.class.getResource("notes.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 835, 668);
        stage.setTitle("Заметки");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

