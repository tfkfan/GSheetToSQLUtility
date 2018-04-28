package com.tfkfan.app.ui.dbform;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DBConnectionFormController implements Initializable {
    @FXML
    public TextField db_user;

    @FXML
    public TextField db_password;

    @FXML
    public TextField db_url;

    @FXML
    public TextField db_name;

    @FXML
    public Button checkConnectionBtn;

    @FXML
    public Button saveBtn;

    private Stage dbWindow;

    private Map<String, String> properties;
    private Boolean isConnected;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setConnected(false);
    }

    @FXML
    public void saveButtonClick(MouseEvent mouseEvent) {
        updateConnection();

        if (isConnected()) {
            getDbWindow().close();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setHeaderText("Warning");
            alert.setContentText("Connection not established. Try again!");

            alert.show();
        }
    }

    @FXML
    public void checkButtonClick(MouseEvent mouseEvent) {
        updateConnection();
    }

    private void updateConnection() {
        updateProperties();
        try {
            //Test connection here

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setHeaderText("Info");
            alert.setContentText("Connection established!");
            alert.show();

            setConnected(true);
        } catch (Exception e) {
            setConnected(false);
        }
    }

    private void updateProperties() {
        properties = new HashMap<>();
        properties.put("user", db_user.getText());
        properties.put("password", db_password.getText());
        properties.put("url", db_url.getText());
        properties.put("name", db_name.getText());
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Stage getDbWindow() {
        return dbWindow;
    }

    public void setDbWindow(Stage dbWindow) {
        this.dbWindow = dbWindow;
    }

    public Boolean isConnected() {
        return isConnected;
    }

    public void setConnected(Boolean connected) {
        isConnected = connected;
    }


}
