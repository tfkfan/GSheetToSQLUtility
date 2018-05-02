package com.tfkfan.app.ui.mainform;

import com.google.api.services.sheets.v4.model.*;
import com.tfkfan.app.helpers.DbHelper;
import com.tfkfan.app.services.DbService;
import com.tfkfan.app.services.SheetsService;
import com.tfkfan.app.services.impl.DbServiceImpl;
import com.tfkfan.app.services.impl.SheetsServiceImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.tfkfan.app.helpers.AppHelper.*;

public class MainFormController implements Initializable, Runnable {

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
    public ProgressIndicator progressBar;

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
    public CheckBox hostCheckbox;

    @FXML
    public CheckBox portCheckbox;

    @FXML
    public Label resultsLabel;

    @FXML
    public CheckComboBox<String> tablesList;

    @FXML
    public Label infoLabel;

    private final SheetsService sheetsService = new SheetsServiceImpl();
    private final DbService dbService = new DbServiceImpl();


    private Stage mainStage;

    private Map<String, String> properties;
    private Connection connection;

    private static final List<String> tables = new ArrayList<>();
    private static final String[] defaultTables = {"invoice", "salesorder", "salesorderlinedetail"};
    private static final Integer page = 1000;
    private static final Integer maxRows = 100000;

    private ScheduledFuture<?> task = null;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void run() {
        try {
            if (tables == null || tables.isEmpty())
                return;

            if (getConnection() != null && getConnection().isClosed())
                setConnection(DbHelper.getConnection(getProperties().get("host"), Integer.valueOf(getProperties().get("port")),
                        getProperties().get("name"), getProperties().get("user"), getProperties().get("password")));

            String spreadsheetId = sheetsService.getSpreadsheetIdFromUrl(spreadsheetUrlField.getText());
            int totalUpdated = 0;
            for (int i = 0; i < tables.size(); i++) {
                final String table = tables.get(i);
                int rows = 0;

                sheetsService.createSheetIfNotExist(spreadsheetId, table);
                sheetsService.clearSheet(spreadsheetId, table);

                while (rows <= maxRows) {
                    List<List<Object>> values = dbService.getValues(connection, table, rows, page);
                    rows += values.size() + 1;
                    if (values.size() == 0)
                        break;

                    AppendValuesResponse  response =  sheetsService.appendValues(values, spreadsheetId, table);

                    Platform.runLater(()->{

                        infoLabel.setVisible(true);
                        infoLabel.setText(response.getUpdates().getUpdatedRows() + " rows were appended");
                    });
                   //allValues.addAll(values);
                    //totalUpdated += response.getTotalUpdatedRows();
                }

               //totalUpdated += response.getTotalUpdatedRows();
                progressBar.setProgress((i + 1) / (double) tables.size());
            }
            progressBar.setProgress(1);
        } catch (GeneralSecurityException e) {
            Platform.runLater(() -> {

                try {
                    showAlert("error", "Spreadsheet access error occured.", "Make sure all input data is correct and try again. Error message: " + e.toString());
                    if (task != null)
                        task.cancel(true);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
        } catch (IOException e) {
            Platform.runLater(() -> {

                try {
                    showAlert("error", "Input/Output error occured.", "Make sure all input data is correct and try again. Error message: " + e.toString());
                    if (task != null)
                        task.cancel(true);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
        } catch (SQLException e) {
            Platform.runLater(() -> {

                try {
                    showAlert("error", "Error occured.",  "Error message: " + e.toString());
                    if (task != null)
                        task.cancel(true);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
        } catch(Exception e){
            Platform.runLater(() -> {


                try {
                    showAlert("error", "Error occured.",  "Error message: " + e.toString());
                    if (task != null)
                        task.cancel(true);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception e) {
            }
        }
    }

    @FXML
    public void startBtnClick(ActionEvent actionEvent) {
        try {
            progressBar.setProgress(0);
            progressBar.setVisible(true);
            resultsLabel.setVisible(false);

            updateProperties();
            setConnection(DbHelper.getConnection(getProperties().get("host"), Integer.valueOf(getProperties().get("port")),
                    getProperties().get("name"), getProperties().get("user"), getProperties().get("password")));
            if (getConnection() == null) {
                showAlert("info", "Database connection not established.", "Check properties and try again.");
                return;
            }

            if (spreadsheetUrlField.getText().isEmpty()) {
                showAlert("info", "Spreadsheet url is empty.", "You have not specified Spreadsheet Url to update.");
                return;
            }

            final Integer minutes = (int) timeSlider.getValue();

            if (task != null)
                task.cancel(true);


            task = scheduler.scheduleAtFixedRate(this, 0, minutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            showAlert("error", "Unknown error", e.getMessage());
        }
    }

    @FXML
    public void stopBtnClick(ActionEvent actionEvent) {
        progressBar.setProgress(0);
        resultsLabel.setVisible(false);

        try {
            if (task != null)
                task.cancel(true);
        } catch (Exception e) {

        }
    }

    @FXML
    public void checkButtonClick(MouseEvent mouseEvent) {
        updateProperties();

        setConnection(DbHelper.getConnection(getProperties().get("host"), Integer.valueOf(getProperties().get("port")),
                getProperties().get("name"), getProperties().get("user"), getProperties().get("password")));
        if (getConnection() == null) {
            showAlert("info", "Database connection not established.", "Check properties and try again.");
        } else {
            showAlert("info", "Database connection established.", "");
            try {
                tablesList.getItems().setAll(FXCollections.observableArrayList(dbService.getTables(getConnection())));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeLabel.setText("Every " + (int) timeSlider.getValue() + " minutes...");
        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Integer val = newValue.intValue();
            timeLabel.setText("Every " + val + " minutes...");
            timeSlider.setValue(val);

        });

        portCheckbox.setSelected(true);
        hostCheckbox.setSelected(true);
        db_host.setText("localhost");
        db_host.setDisable(true);
        db_port.setText("1433");
        db_port.setDisable(true);

        tablesList.getItems().setAll(FXCollections.observableArrayList(defaultTables));
        tablesList.getCheckModel().setSelectionMode(SelectionMode.MULTIPLE);
        tablesList.getCheckModel().getSelectedItems().addListener((ListChangeListener<? super String>) c -> {
            tables.clear();
            tables.addAll(c.getList());
        });
    }

    private void updateProperties() {
        properties = new HashMap<>();
        properties.put("user", db_user.getText());
        properties.put("password", db_password.getText());
        properties.put("host", hostCheckbox.isSelected() ? "localhost" : db_host.getText());
        properties.put("port", portCheckbox.isSelected() ? String.valueOf(1433) : db_port.getText());
        properties.put("name", db_name.getText());
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
