package com.tfkfan.app.ui.dbform;

import com.tfkfan.app.helpers.DbHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

    @FXML
    public CheckBox hostCheckbox;

    @FXML
    public CheckBox portCheckbox;


    private Stage dbWindow;

    private Map<String, String> properties;
    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        portCheckbox.setSelected(true);
        hostCheckbox.setSelected(true);
        db_host.setText("localhost");
        db_host.setDisable(true);
        db_port.setText("1433");
        db_port.setDisable(true);
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

    @FXML
    public void localCheckboxValueChanged(ActionEvent actionEvent) {
        Boolean isSelected = ((CheckBox) actionEvent.getSource()).isSelected();
        String val = isSelected ? "localhost" : "";
        db_host.setText(val);
        db_host.setDisable(isSelected);
    }

    @FXML
    public void defaultCheckboxValueChanged(ActionEvent actionEvent) {
        Boolean isSelected = ((CheckBox) actionEvent.getSource()).isSelected();
        String val = isSelected ? "1433" : "";
        db_port.setText(val);
        db_port.setDisable(isSelected);
    }

    private void updateConnection() {
        if (getConnection() != null)
            return;

        updateProperties();
        try {
            setConnection(DbHelper.getConnection(getProperties().get("host"), Integer.valueOf(getProperties().get("port")),
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
        properties.put("host", hostCheckbox.isSelected() ? "localhost" : db_host.getText());
        properties.put("port", portCheckbox.isSelected() ? String.valueOf(1433) : db_port.getText());
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
