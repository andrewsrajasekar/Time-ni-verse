package com.timeniverse;

import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void switchToMain() throws IOException {
        App.setRoot("main");
    }
}