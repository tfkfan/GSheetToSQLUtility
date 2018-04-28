package com.tfkfan.app;

import com.tfkfan.app.db.utils.DbUtils;

import java.sql.*;

public class connectDS {

    public static void main(String[] args) {

        // Declare the JDBC objects.
        Connection con = DbUtils.getConnection(null, null, "Quickbooks", "dbuser", "qwert" );
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Create and execute an SQL statement that returns some data.
            String SQL = "SELECT TOP 10 * FROM invoice";
            stmt = con.createStatement();
            rs = stmt.executeQuery(SQL);

            // Iterate through the data in the result set and display it.
            while (rs.next()) {

                System.out.println("DONE");
            }
        }

        // Handle any errors that may have occurred.
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (rs != null) try { rs.close(); } catch(Exception e) {}
            if (stmt != null) try { stmt.close(); } catch(Exception e) {}
            if (con != null) try { con.close(); } catch(Exception e) {}
        }
    }
}