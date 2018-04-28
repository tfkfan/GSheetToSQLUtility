package com.tfkfan.app.ui.dbform;

import com.tfkfan.app.db.utils.DbUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DBConnectionFormController implements Initializable {
    @FXML
    public TextField db_user;

    @FXML
    public TextField db_password;

    @FXML
    public TextField db_host;

    @FXML
    public TextField db_port;

    @FXML
    public TextField db_name;

    @FXML
    public Button checkConnectionBtn;

    @FXML
    public Button saveBtn;


    private Stage dbWindow;

    private Map<String, String> properties;
    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void saveButtonClick(MouseEvent mouseEvent) {
        updateConnection();

        if (getConnection() != null) {
            getDbWindow().close();
        }
    }

    @FXML
    public void checkButtonClick(MouseEvent mouseEvent) {
        updateConnection();
    }

    private void updateConnection() {
        updateProperties();
        try {
            setConnection(DbUtils.getConnection(getProperties().get("host"), Integer.valueOf(getProperties().get("port")),
                    getProperties().get("name"), getProperties().get("user"), getProperties().get("password")));

            showAlert("info", "Connection established.");

            checkConnectionBtn.setDisable(true);
        } catch (Exception e) {
            showAlert("warning", "Connection not established. Try again!");
            setConnection(null);
        }
    }

    private void showAlert(String header, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private void updateProperties() {
        properties = new HashMap<>();
        properties.put("user", db_user.getText());
        properties.put("password", db_password.getText());
        properties.put("host", db_host.getText());
        properties.put("port", db_port.getText());
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

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
