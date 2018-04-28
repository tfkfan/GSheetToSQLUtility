package com.tfkfan.app.ui.mainform;

import com.tfkfan.app.App;
import com.tfkfan.app.ui.dbform.DBConnectionFormController;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.event.HyperlinkEvent;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainFormController implements Initializable {

    @FXML
    Slider timeSlider;

    @FXML
    Label timeLabel;

    DBConnectionFormController dbWindowController;
    Parent dbWindow;
    Stage dbWindowModal;
    Stage mainStage;

    private Map<String, String> properties;

    @FXML
    public void startButtonClick(Event event) {
        if(dbWindowController.isConnected()){

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void dbConnClick(ActionEvent actionEvent) throws IOException {
        dbWindowController.setProperties(new HashMap<>());
        showDBWindow();
        dbWindowModal.showAndWait();

        dbWindowModal.setOnCloseRequest(event -> {
            properties = dbWindowController.getProperties();
            if(dbWindowController.isConnected()){

            }
        });
    }

    private void showDBWindow(){
        if(dbWindowModal != null)
            return;

        dbWindowModal = new Stage();
        dbWindowModal.setTitle("DB Connection");
        dbWindowModal.setScene(new Scene(dbWindow));
        dbWindowModal.initModality(Modality.WINDOW_MODAL);
        dbWindowModal.initOwner(mainStage);

        dbWindowController.setDbWindow(dbWindowModal);
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
}
