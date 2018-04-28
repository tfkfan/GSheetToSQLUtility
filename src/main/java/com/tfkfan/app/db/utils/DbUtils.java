package com.tfkfan.app.db.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbUtils {
    public static Connection getConnection(String host, Integer port, String dbName, String dbUser, String dbPassword) {
        // Create a variable for the connection string.
        if(host == null)
            host = "localhost";
        if(port == null)
            port = 1433;

        String connectionUrl = "jdbc:sqlserver://" + host + ":" + port + ";" +
                "databaseName=" + dbName + ";user=" + dbUser + ";password=" + dbPassword;

        // Declare the JDBC objects.
        Connection connection = null;

        try {
            // Establish the connection.
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(connectionUrl);
        }

        // Handle any errors that may have occurred.
        catch (Exception e) {

        }
        return connection;
    }
}
