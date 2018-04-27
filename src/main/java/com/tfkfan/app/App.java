package com.tfkfan.app;

import com.tfkfan.app.ui.dbwindow.DBConnectionFormController;
import com.tfkfan.app.ui.mainform.MainFormController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    public final String title = "Sheets Updater";

    private Stage primaryStage;
    private Parent dbWindow;
    private Parent mainWindow;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        setPrimaryStage(primaryStage);

        final FXMLLoader dbWindowLoader = new FXMLLoader(getClass().getResource(
                "ui/dbwindow/DBConnectionForm.fxml"));

        final FXMLLoader mainWindowLoader =  new FXMLLoader(getClass().getResource(
                "ui/mainform/MainForm.fxml"));

        final Parent dbWindow = dbWindowLoader.load();
        setDbWindow(dbWindow);

        final Parent mainWindow = mainWindowLoader.load();
        setMainWindow(mainWindow);

        //////////////////////////////////////////

        final DBConnectionFormController dbWindowController = dbWindowLoader.getController();
        dbWindowController.setApp(this);

        final MainFormController mainFormController = mainWindowLoader.getController();
        mainFormController.setApp(this);

        primaryStage.setTitle(title);
        primaryStage.setScene(new Scene(dbWindow));
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Parent getDbWindow() {
        return dbWindow;
    }

    public void setDbWindow(Parent dbWindow) {
        this.dbWindow = dbWindow;
    }

    public Parent getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(Parent mainWindow) {
        this.mainWindow = mainWindow;
    }
}
