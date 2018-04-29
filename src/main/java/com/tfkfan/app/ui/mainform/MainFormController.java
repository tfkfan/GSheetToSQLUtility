package com.tfkfan.app.ui.mainform;

import com.tfkfan.app.helpers.DbHelper;
import com.tfkfan.app.ui.dbform.DBConnectionFormController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.*;

import static com.tfkfan.app.helpers.AppHelper.*;

public class MainFormController implements Initializable {

    @FXML
    public Button stopBtn;

    @FXML
    public Button startBtn;

    @FXML
    public Slider timeSlider;

    @FXML
    public Label timeLabel;

    @FXML
    public TextField spreadsheetUrlField;

    @FXML
    public ProgressBar progressBar;

    private DBConnectionFormController dbWindowController;
    private Parent dbWindow;
    private Stage dbWindowModal;
    private Stage mainStage;

    private Map<String, String> properties;
    private Connection connection;

    private static String[] tables = {"invoice", "salesorder", "salesorderlinedetail"};

    @FXML
    public void startBtnClick(ActionEvent actionEvent) {
        if (getConnection() == null) {
            setConnection(DbHelper.getConnection(getProperties().get("host"), Integer.valueOf(getProperties().get("port")),
                    getProperties().get("name"), getProperties().get("user"), getProperties().get("password")));
            if (getConnection() == null)
                processConnectionWindow();
        }

        if (getConnection() == null) {
            stopBtn.setDisable(true);
            startBtn.setDisable(true);
            timeSlider.setDisable(true);
        } else {
            stopBtn.setDisable(false);
            startBtn.setDisable(false);
            timeSlider.setDisable(false);
        }

        if (spreadsheetUrlField.getText().isEmpty()) {
            showAlert("info", "You have not specified Spreadsheet Url to update.");
            return;
        }

        processApp(progressBar, getProperties().get("name"), tables, getConnection(), spreadsheetUrlField.getText());
    }

    @FXML
    public void stopBtnClick(ActionEvent actionEvent) {
        progressBar.setProgress(0.33d);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeLabel.setText("Every " + (int) timeSlider.getValue() + " minutes...");
        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Integer val = newValue.intValue();
            timeLabel.setText("Every " + val + " minutes...");
            timeSlider.setValue(val);

        });

        final FXMLLoader dbWindowLoader = new FXMLLoader(getClass().getResource(
                "../dbform/DBConnectionForm.fxml"));

        try {
            dbWindow = dbWindowLoader.load();
            dbWindowController = dbWindowLoader.getController();


            startBtn.setDisable(true);
            stopBtn.setDisable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void dbConnClick(ActionEvent actionEvent) throws IOException {
        processConnectionWindow();
        if (getConnection() != null) {
            startBtn.setDisable(false);
            stopBtn.setDisable(false);
        }
    }

    private void processConnectionWindow() {
        dbWindowController.setProperties(new HashMap<>());
        if (dbWindowModal == null) {
            dbWindowModal = new Stage();
            dbWindowModal.setResizable(false);
            dbWindowModal.setTitle("DB Connection");
            dbWindowModal.setScene(new Scene(dbWindow));
            dbWindowModal.initModality(Modality.WINDOW_MODAL);
            dbWindowModal.initOwner(getMainStage());

            dbWindowController.setDbWindow(dbWindowModal);
        }
        dbWindowModal.showAndWait();
        updateConnectionProperties(dbWindowController.getConnection(), dbWindowController.getProperties());
    }

    private void updateConnectionProperties(Connection connection, Map<String, String> properties) {
        setConnection(connection);
        setProperties(properties);
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
