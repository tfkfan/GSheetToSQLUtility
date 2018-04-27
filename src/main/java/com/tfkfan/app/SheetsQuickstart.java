package com.tfkfan.app;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import static com.tfkfan.app.sheets.SheetsHelper.*;

public class SheetsQuickstart {
    public static void main(String... args) throws IOException, GeneralSecurityException {
        Spreadsheet sp = new Spreadsheet();
        Sheet sheet = new Sheet();

        sheet.setProperties(new SheetProperties().setTitle("MySHEET!"));
        List<Sheet> sheets = new ArrayList<>();
        sheets.add(sheet);
        sp.setSheets(sheets);
        getSpreadsheets().create(sp).execute();
       /* final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
        final String range = "Class Data!A2:E";
        List<List<Object>> values = readSpreadSheet(spreadsheetId, range);
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Name, Major");
            for (List row : values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s, %s\n", row.get(0), row.get(4));
            }
        }*/
    }
}