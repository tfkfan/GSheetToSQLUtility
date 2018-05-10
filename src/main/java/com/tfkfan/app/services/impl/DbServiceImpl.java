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
    public List<List<Object>> getValues(Connection connection, List<Object> columns, String tableName, Integer rowStart, Integer offset) throws SQLException {
        List<List<Object>> rows = new ArrayList<>(30000);
        String columnsStr = "";
        for(int i = 0; i < columns.size(); i++) {
            final String columnName = "[" + columns.get(i) + "]";
            columnsStr += i != columns.size() - 1 ? columnName + ", " : columnName;
        }

        String query = " WITH CTE AS( "
                + " SELECT ROW_NUMBER() OVER ( ORDER BY (SELECT NULL) ) AS RowNum , * FROM " + tableName + " ) "
                + " SELECT " + columnsStr + " FROM CTE WHERE "
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

    @Override
    public List<Object> getTableColumns(Connection connection, String tableName) throws SQLException {
        final List<Object> row = new ArrayList<>();

        String query = " SELECT TOP(1) * FROM " + tableName;

        ResultSet rs = DbHelper.executeQuery(query, connection);

        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();


        for (int i = 1; i <= columnCount; i++)
            row.add(metadata.getColumnName(i));

        if (rs != null) try {
            rs.close();
        } catch (Exception e) {
        }

        return row;
    }

    @Override
    public List<String> getTables(Connection connection) throws SQLException {
        List<String> res = new ArrayList<>();
        String query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES ORDER BY TABLE_NAME ASC";
        ResultSet rs = DbHelper.executeQuery(query,connection);
        while(rs.next()){
            res.add(rs.getString(1));
        }

        return res;
    }
}
