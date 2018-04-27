package com.tfkfan.app;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import static com.tfkfan.app.sheets.SheetsHelper.*;

public class SheetsQuickstart {
    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
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
        }
    }
}