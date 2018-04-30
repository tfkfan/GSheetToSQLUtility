package com.tfkfan.app.helpers;

import javafx.scene.control.Alert;

public class AppHelper {
    public static void showAlert(String title, String header, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
