package com.tfkfan.app.services;

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface SheetsService {
    String getSpreadsheetIdFromUrl(String url);

    List<List<Object>> readSpreadSheet(String spreadsheetId, String range) throws IOException, GeneralSecurityException;

    void createSheet(String spreadsheetId, String sheetName) throws GeneralSecurityException, IOException;

    Sheet getSheetByName(String spreadsheetId, String sheetName) throws GeneralSecurityException, IOException;

    void createSheetIfNotExist(String spreadsheetId, String sheetName) throws GeneralSecurityException, IOException;

    BatchUpdateSpreadsheetResponse executeBatchSpreadsheetRequest(List<Request> requests, String spreadsheetId) throws GeneralSecurityException, IOException;

    BatchUpdateValuesResponse executeBatchRequest(List<List<Object>> values, String spreadsheetId, String range) throws GeneralSecurityException, IOException;

    void clearSheet(String spreadsheetId, String sheetName) throws GeneralSecurityException, IOException;
}
