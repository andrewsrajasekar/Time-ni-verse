package com.timeniverse;

import javafx.scene.control.Alert;
import javafx.stage.Window;

public class AlertHelper {
    // Class that alerts the user that task input field is not filled in correctly
    public static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
}