package com.tfkfan.app;

import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.tfkfan.app.helpers.SheetsHelper.*;

public class SheetsQuickstart {
    public static void main(String... args) throws IOException, GeneralSecurityException {
       /* Spreadsheet sp = new Spreadsheet();
        Sheet sheet = new Sheet();

        sheet.setProperties(new SheetProperties().setTitle("MySHEET!"));
        List<Sheet> sheets = new ArrayList<>();
        sheets.add(sheet);
        sp.setSheets(sheets);
        getSpreadsheets().create(sp).execute();*/

        String url = "https://docs.google.com/spreadsheets/d/1Gh3Xgj99SJ84q8CDosVHxC1VfjHW5efdpJ6_WNXEY-0/edit?usp=sharing";
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        "haha","gdfg"
                )
        );
        ValueRange body = new ValueRange()
                .setValues(values);
        AppendValuesResponse result = getSpreadsheets().values().append(getSpreadsheetId(url), "A1", body)
                       .setValueInputOption("RAW")
                        .execute();
        System.out.printf("%d cells appended.", result.getUpdates().getUpdatedCells());
    }
}