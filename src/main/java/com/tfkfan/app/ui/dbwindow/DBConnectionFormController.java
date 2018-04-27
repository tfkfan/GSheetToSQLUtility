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
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class DBConnectionFormController implements Initializable {
    private App app;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void saveButtonClick(MouseEvent mouseEvent) {
        getApp().getPrimaryStage().getScene().setRoot(getApp().getMainWindow());
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }
}
