package com.tfkfan.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private final String title = "Sheets Updater";
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("ui/mainForm.fxml"));
        primaryStage.setTitle(title);
        primaryStage.setScene(new Scene(parent, 900, 600));
        primaryStage.show();
    }
}
