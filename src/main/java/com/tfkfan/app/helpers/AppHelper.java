package com.tfkfan.app.helpers;

import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.tfkfan.app.helpers.SheetsHelper.*;

public class AppHelper {
    public static void processSpreadsheets(String spreadsheetId, String sheetName, List<List<Object>> values) throws GeneralSecurityException, IOException {
        createSheetIfNotExist(spreadsheetId, sheetName);
        ValueRange body = new ValueRange()
                .setValues(values);

        AppendValuesResponse result = getSpreadsheets().values().append(spreadsheetId, sheetName + "!A1", body)
                .setValueInputOption("RAW")
                .execute();
        System.out.printf("%d cells appended.", result.getUpdates().getUpdatedCells());
    }

    public static List<List<Object>> processDatabase(Connection connection, String dbName, String tableName, Integer rowStart, Integer offset) {
        ResultSet rs = null;
        List<List<Object>> values = new ArrayList<>(new ArrayList<>());
        try {
            //TODO TxnId is not in some table, change It to valid query
            String query = " WITH CTE AS( "
                    + " SELECT ROW_NUMBER() OVER ( ORDER BY [TxnId] ) AS RowNum , * FROM " + tableName + ") "
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
            }
        }
        return values;
    }

    public static void processApp(String dbName, String[] tables, Connection connection, String spreadsheetUrl) {
        try {
            if (tables == null)
                return;

            String spreadsheetId = getSpreadsheetId(spreadsheetUrl);

            for (final String table : tables) {
                int rows = 0;
                int maxRows = 1000;
                List<List<Object>> allValues = new ArrayList<>(new ArrayList<>());
                while (rows <= 100000) {
                    List<List<Object>> values = processDatabase(connection, dbName, table, rows, maxRows);
                    rows += values.size();
                    if (values.size() == 0)
                        break;

                    allValues.addAll(values);

                }
                processSpreadsheets(spreadsheetId, table, allValues);
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception e) {
            }
        }
    }
}
