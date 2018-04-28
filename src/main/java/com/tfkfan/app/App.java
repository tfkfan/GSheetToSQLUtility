package com.tfkfan.app;

import com.tfkfan.app.ui.dbform.DBConnectionFormController;
import com.tfkfan.app.ui.mainform.MainFormController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    public final String title = "Sheets Updater";

    private Parent mainWindow;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        final FXMLLoader mainWindowLoader =  new FXMLLoader(getClass().getResource(
                "ui/mainform/MainForm.fxml"));

        final Parent mainWindow = mainWindowLoader.load();
        setMainWindow(mainWindow);

        //////////////////////////////////////////

        final MainFormController mainFormController = mainWindowLoader.getController();
        mainFormController.setMainStage(primaryStage);

        primaryStage.setResizable(false);
        primaryStage.setTitle(title);
        primaryStage.setScene(new Scene(mainWindow));
        primaryStage.show();
    }

    public Parent getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(Parent mainWindow) {
        this.mainWindow = mainWindow;
    }
}
