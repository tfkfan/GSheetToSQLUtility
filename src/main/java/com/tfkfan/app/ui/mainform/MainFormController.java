package com.tfkfan.app.ui.mainform;

import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.tfkfan.app.helpers.DbHelper;
import com.tfkfan.app.services.DbService;
import com.tfkfan.app.services.SheetsService;
import com.tfkfan.app.services.impl.DbServiceImpl;
import com.tfkfan.app.services.impl.SheetsServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import static com.tfkfan.app.helpers.AppHelper.*;
import static com.tfkfan.app.helpers.SheetsHelper.getSpreadsheets;

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
    public Label tableLabel;

    private final SheetsService sheetsService = new SheetsServiceImpl();
    private final DbService dbService = new DbServiceImpl();

    private Stage mainStage;

    private Map<String, String> properties;
    private Connection connection;

    private static String[] tables = {"invoice", "salesorder", "salesorderlinedetail"};
    private static Integer pageLimit = 1000;

    @FXML
    public void startBtnClick(ActionEvent actionEvent) {
        if (getConnection() == null) {
            updateProperties();
            setConnection(DbHelper.getConnection(getProperties().get("host"), Integer.valueOf(getProperties().get("port")),
                    getProperties().get("name"), getProperties().get("user"), getProperties().get("password")));
            if (getConnection() == null) {
                showAlert("info", "Database connection not established.");
                return;
            }
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

        processApp(spreadsheetUrlField.getText());
    }

    @FXML
    public void stopBtnClick(ActionEvent actionEvent) {
        progressBar.setProgress(0.33d);
    }

    @FXML
    public void checkButtonClick(MouseEvent mouseEvent) {
        updateProperties();

        setConnection(DbHelper.getConnection(getProperties().get("host"), Integer.valueOf(getProperties().get("port")),
                getProperties().get("name"), getProperties().get("user"), getProperties().get("password")));
        if (getConnection() == null) {
            showAlert("info", "Database connection not established.");
        } else
            showAlert("info", "Database connection established.");
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

    private void processApp(String spreadsheetUrl) {
        try {
            if (tables == null)
                return;

            String spreadsheetId = sheetsService.getSpreadsheetIdFromUrl(spreadsheetUrl);
            progressBar.setProgress(0);
            tableLabel.setVisible(true);
            for (int i = 0; i < tables.length; i++) {
                final String table = tables[i];
                tableLabel.setText("Table '" + table + "' is processing...");

                int rows = 0;

                List<List<Object>> allValues = new ArrayList<>(new ArrayList<>());
                while (rows <= 100000) {
                    List<List<Object>> values = processDatabase(connection, table, rows, pageLimit);
                    rows += values.size() + 1;
                    if (values.size() == 0)
                        break;

                    allValues.addAll(values);
                }
                processSpreadsheets(spreadsheetId, table, allValues);
                progressBar.setProgress((i + 1) / (double) tables.length);
            }
            tableLabel.setVisible(false);
            progressBar.setProgress(1);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            showAlert("error", "SQL Error occured. Try again");
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception e) {
            }
        }
    }

    public void processSpreadsheets(String spreadsheetId, String sheetName, List<List<Object>> values) throws GeneralSecurityException, IOException {
        sheetsService.createSheetIfNotExist(spreadsheetId, sheetName);

        ValueRange body = new ValueRange()
                .setValues(values);

        AppendValuesResponse result = getSpreadsheets().values().append(spreadsheetId, sheetName + "!A1", body)
                .setValueInputOption("RAW")
                .execute();
        System.out.printf("%d cells appended.", result.getUpdates().getUpdatedCells());
    }

    public List<List<Object>> processDatabase(Connection connection, String tableName, Integer rowStart, Integer offset) throws SQLException {
        ResultSet rs = null;
        List<List<Object>> values = new ArrayList<>(new ArrayList<>());
        String idField = !tableName.equals("salesorderlinedetail") ? "[TxnId]" : "[TxnLineID]";

        //TODO TxnId does not exist in some table, change It to valid query
        String query = " WITH CTE AS( "
                + " SELECT ROW_NUMBER() OVER ( ORDER BY " + idField + " ) AS RowNum , * FROM " + tableName + ") "
                + " SELECT * FROM CTE WHERE "
                + " RowNum BETWEEN " + rowStart + " AND " + (rowStart + offset - 1) + " "
                + " Order By RowNum ";

        rs = DbHelper.executeQuery(query, connection);

        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();

        while (rs.next()) {
            List<Object> rowValue = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++)
                rowValue.add(rs.getString(i));
            values.add(rowValue);
        }

        if (rs != null) try {
            rs.close();
        } catch (Exception e) {
        }

        return values;
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

        //startBtn.setDisable(true);
        //stopBtn.setDisable(true);
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
