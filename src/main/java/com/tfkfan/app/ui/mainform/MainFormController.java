package com.tfkfan.app.ui.mainform;

import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.tfkfan.app.helpers.DbHelper;
import com.tfkfan.app.services.SheetsService;
import com.tfkfan.app.services.impl.SheetsServiceImpl;
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

    private SheetsService sheetsService = new SheetsServiceImpl();


    private DBConnectionFormController dbWindowController;
    private Parent dbWindow;
    private Stage dbWindowModal;
    private Stage mainStage;

    private Map<String, String> properties;
    private Connection connection;

    private static String[] tables = {"invoice", "salesorder", "salesorderlinedetail"};
    private static Integer pageLimit = 1000;


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

        processApp(getProperties().get("name"), tables, getConnection(), spreadsheetUrlField.getText());
    }

    @FXML
    public void stopBtnClick(ActionEvent actionEvent) {
        progressBar.setProgress(0.33d);
    }

    private void processApp(String dbName, String[] tables, Connection connection, String spreadsheetUrl) {
        try {
            if (tables == null)
                return;

            String spreadsheetId = sheetsService.getSpreadsheetIdFromUrl(spreadsheetUrl);
            progressBar.setProgress(0);
            for (int i = 0; i < tables.length; i++) {
                final String table = tables[i];
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
