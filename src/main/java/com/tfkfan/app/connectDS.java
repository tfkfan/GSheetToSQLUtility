package com.tfkfan.app;

import com.tfkfan.app.helpers.DbHelper;

import java.sql.*;

public class connectDS {

    public static void main(String[] args) {
        Connection connection = DbHelper.getConnection(null, null, "Quickbooks", "dbuser", "qwert");

        ResultSet rs = null;
        try {
            String query = "SELECT TOP 10 * FROM invoice";

            rs = DbHelper.executeQuery(query, connection);

            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();

            while (rs.next()) {
                String row = "";
                for (int i = 1; i <= columnCount; i++) {
                    row += "[" + metadata.getColumnName(i) + " / " + rs.getString(i) + " ], ";
                }
                System.out.println(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
            }
            if (connection != null) try {
                connection.close();
            } catch (Exception e) {
            }
        }
    }
}