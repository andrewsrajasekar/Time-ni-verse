package com.timeniverse;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class sceneController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    
    public void switchToMain(ActionEvent event) throws IOException {
    root = FXMLLoader.load(getClass().getResource("main.fxml"));
    stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    scene = new Scene(root, 1000, 500);
    stage.setScene(scene);
    stage.show();
    }
    
    public void switchToInputForm(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("inputForm.fxml"));
    stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    scene = new Scene(root, 1000, 500);
    stage.setScene(scene);
    stage.show();
    }
}