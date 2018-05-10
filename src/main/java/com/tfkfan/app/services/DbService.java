package com.tfkfan.app.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DbService {
    List<List<Object>> getValues(Connection connection, List<Object> columns, String tableName, Integer rowStart, Integer offset) throws SQLException;
    List<Object> getTableColumns(Connection connection, String tableName) throws SQLException;
    List<String> getTables(Connection connection) throws SQLException;
}
