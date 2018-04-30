package com.tfkfan.app.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DbService {
    List<List<Object>> getValues(Connection connection, String tableName, Integer rowStart, Integer offset) throws SQLException;
}
