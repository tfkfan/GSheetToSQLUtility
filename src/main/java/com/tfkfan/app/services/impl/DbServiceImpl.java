package com.tfkfan.app.services.impl;

import com.tfkfan.app.helpers.DbHelper;
import com.tfkfan.app.services.DbService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbServiceImpl implements DbService {
    @Override
    public List<List<Object>> getValues(Connection connection, String tableName, Integer rowStart, Integer offset) throws SQLException {
        List<List<Object>> rows = new ArrayList<>(new ArrayList<>(10000));
        String idField = !tableName.equals("salesorderlinedetail") ? "[TxnId]" : "[TxnLineID]";

        //TODO TxnId does not exist in some table, change It to valid query
        String query = " WITH CTE AS( "
                + " SELECT ROW_NUMBER() OVER ( ORDER BY " + idField + " ) AS RowNum , * FROM " + tableName + ") "
                + " SELECT * FROM CTE WHERE "
                + " RowNum BETWEEN " + rowStart + " AND " + (rowStart + offset - 1) + " "
                + " Order By RowNum ";

        ResultSet rs = DbHelper.executeQuery(query, connection);

        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();

        while (rs.next()) {
            List<Object> row = new ArrayList<>(50);
            for (int i = 1; i <= columnCount; i++)
                row.add(rs.getString(i));
            rows.add(row);
        }

        if (rs != null) try {
            rs.close();
        } catch (Exception e) {
        }

        return rows;
    }
}
