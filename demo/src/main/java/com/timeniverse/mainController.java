package com.timeniverse;

import java.io.IOException;
import javafx.fxml.FXML;

public class mainController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
