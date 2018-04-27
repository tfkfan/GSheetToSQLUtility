package com.tfkfan.app.ui.dbwindow;

import com.tfkfan.app.App;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class DBConnectionFormController implements Initializable {
    public TextField db_user;
    public TextField db_password;
    public TextField db_url;
    public TextField db_name;

    private App app;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void saveButtonClick(MouseEvent mouseEvent) {
        getApp().getPrimaryStage().getScene().setRoot(getApp().getMainWindow());
    }

    public void checkButtonClick(MouseEvent mouseEvent) {
        
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public String getDB_User(){
        return db_user.getText();
    }

    public String getDB_Password(){
        return db_password.getText();
    }

    public String getDB_URL(){
        return db_url.getText();
    }

    public String getDB_Name(){
        return db_name.getText();
    }
}
