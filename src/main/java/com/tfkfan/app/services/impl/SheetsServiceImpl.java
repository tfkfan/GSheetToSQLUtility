package com.tfkfan.app.services.impl;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.tfkfan.app.services.SheetsService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static com.tfkfan.app.helpers.SheetsHelper.getSpreadsheets;

public class SheetsServiceImpl implements SheetsService {
    @Override
    public String getSpreadsheetIdFromUrl(String url) {
        String[] parts = url.split("spreadsheets/d/");
        String result;
        if (parts[1].contains("/")) {
            String[] parts2 = parts[1].split("/");
            result = parts2[0];
        } else {
            result = parts[1];
        }
        return result;
    }

    @Override
    public List<List<Object>> readSpreadSheet(String spreadsheetId, String range) throws IOException, GeneralSecurityException {
        ValueRange response = getSpreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        return response.getValues();
    }

    @Override
    public void createSheet(String spreadsheetId, String sheetName) throws GeneralSecurityException, IOException {
        final List<Request> requests = new ArrayList<>();
        final AddSheetRequest addSheetRequest = new AddSheetRequest();
        addSheetRequest.setProperties(new SheetProperties().setTitle(sheetName));

        final Request request = new Request();
        request.setAddSheet(addSheetRequest);

        requests.add(request);

        executeBatchSpreadsheetRequest(requests, spreadsheetId);
    }

    @Override
    public Sheet getSheetByName(String spreadsheetId, String sheetName) throws GeneralSecurityException, IOException {
        List<Sheet> sheets =getSpreadsheets().get(spreadsheetId).execute().getSheets();
        Sheet foundSheet = null;

        for(Sheet sheet : sheets){
            if(sheet.getProperties().getTitle().equals(sheetName)) {
                foundSheet = sheet;
                break;
            }
        }

        return foundSheet;
    }

    @Override
    public void createSheetIfNotExist(String spreadsheetId, String sheetName) throws GeneralSecurityException, IOException {
        Sheet sheet = getSheetByName(spreadsheetId, sheetName);
        if(sheet == null)
            createSheet(spreadsheetId, sheetName);
    }

    @Override
    public BatchUpdateSpreadsheetResponse executeBatchSpreadsheetRequest(List<Request> requests, String spreadsheetId) throws GeneralSecurityException, IOException {
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        requestBody.setRequests(requests);

        Sheets.Spreadsheets.BatchUpdate request =
                getSpreadsheets().batchUpdate(spreadsheetId, requestBody);

        return request.execute();
    }

    @Override
    public BatchUpdateValuesResponse executeBatchRequest(List<List<Object>> values, String spreadsheetId, String range) throws GeneralSecurityException, IOException {
        List<ValueRange> data = new ArrayList<>();
        data.add(new ValueRange()
                .setRange(range)
                .setValues(values));

        BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
                .setValueInputOption("RAW")
                .setData(data);
        return getSpreadsheets().values().batchUpdate(spreadsheetId, body).execute();
    }

    @Override
    public void clearSheet(String spreadsheetId, String sheetName) throws GeneralSecurityException, IOException {
        getSpreadsheets().values().clear(spreadsheetId,sheetName + "!A1:CI100000", new ClearValuesRequest()).execute();
    }
}
