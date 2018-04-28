package com.tfkfan.app.ui.mainform;

import com.tfkfan.app.ui.dbform.DBConnectionFormController;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainFormController implements Initializable {

    @FXML
    public Button stopBtn;

    @FXML
    public Button startBtn;

    @FXML
    public Slider timeSlider;

    @FXML
    public Label timeLabel;

    DBConnectionFormController dbWindowController;

    Parent dbWindow;

    Stage dbWindowModal;
    Stage mainStage;

    private Map<String, String> properties;
    private Connection connection;

    @FXML
    public void startButtonClick(Event event) {
        if (getConnection() == null) {
            processConnectionWindow();

            if (getConnection() == null) {
                stopBtn.setDisable(true);
                startBtn.setDisable(true);
                timeSlider.setDisable(true);
            } else {
                stopBtn.setDisable(false);
                startBtn.setDisable(false);
                timeSlider.setDisable(false);
            }
        }

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

            processConnectionWindow();

            if (getConnection() == null) {
                stopBtn.setDisable(true);
                startBtn.setDisable(true);
            } else {
                stopBtn.setDisable(false);
                startBtn.setDisable(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void dbConnClick(ActionEvent actionEvent) throws IOException {
        processConnectionWindow();
    }

    private void processConnectionWindow() {
        dbWindowController.setProperties(new HashMap<>());
        if (dbWindowModal == null) {
            dbWindowModal = new Stage();
            dbWindowModal.setResizable(false);
            dbWindowModal.setTitle("DB Connection");
            dbWindowModal.setScene(new Scene(dbWindow));
            dbWindowModal.initModality(Modality.WINDOW_MODAL);
            dbWindowModal.initOwner(mainStage);

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
