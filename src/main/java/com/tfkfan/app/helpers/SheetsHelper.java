package com.tfkfan.app.helpers;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.tfkfan.app.SheetsQuickstart;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public final class SheetsHelper {
    private static final String CLIENT_SECRET_DIR = "/client_secret.json";
    public static final String APPLICATION_NAME = "Google Sheets API Java";
    public static final List<String> SCOPES = new ArrayList<>(SheetsScopes.all());
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static final String CREDENTIALS_FOLDER = "credentials"; // Directory to store user credentials.

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If there is no client_secret.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetsQuickstart.class.getResourceAsStream(CLIENT_SECRET_DIR);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static List<List<Object>> readSpreadSheet(String spreadsheetId, String range) throws IOException, GeneralSecurityException {
        ValueRange response = getSpreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        return values;
    }

    public static Sheets.Spreadsheets getSpreadsheets() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service.spreadsheets();
    }

    public static BatchUpdateSpreadsheetResponse executeBatchRequest(List<Request> requests, String spreadsheetId) throws GeneralSecurityException, IOException {
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        requestBody.setRequests(requests);

        Sheets.Spreadsheets.BatchUpdate request =
                getSpreadsheets().batchUpdate(spreadsheetId, requestBody);

        return request.execute();
    }

    public static void createSheetIfNotExist(String spreadsheetId, String sheetName) throws GeneralSecurityException, IOException {
        Sheet foundSheet = getSpreadsheets().get(spreadsheetId).execute().getSheets().stream().filter(sheet -> sheet.getProperties().getTitle().equals(sheetName)).findFirst().get();

        if(foundSheet != null)
            return;

        final List<Request> requests = new ArrayList<>();
        final AddSheetRequest addSheetRequest = new AddSheetRequest();
        addSheetRequest.setProperties(new SheetProperties().setTitle(sheetName));

        final Request request = new Request();
        request.setAddSheet(addSheetRequest);

        requests.add(request);

        BatchUpdateSpreadsheetResponse response = executeBatchRequest(requests, spreadsheetId);

        // TODO: Change code below to process the `response` object:
        System.out.println(response);
    }

    public static String getSpreadsheetId(String url) {
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
}
