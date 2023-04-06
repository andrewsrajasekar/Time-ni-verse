package com.timeniverse;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.stage.Window;

public class sceneController {
    // sceneController handles any action done by user on page

    private Stage stage;
    private Scene scene;
    private Parent root;
    
    @FXML
    private TextField assignment;

    @FXML
    private TextField duration;

    @FXML
    private TextField deadline;

    @FXML
    private Button submitButton;
    
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

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        // Submitting the task button
        Window owner = submitButton.getScene().getWindow();
        if(assignment.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter the task");
            return;
        }
        if(duration.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter the time to allocate for the task");
            return;
        }
        if(deadline.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter a deadline date using the format: MM-DD-YYYY");
            return;
        }

        AlertHelper.showAlert(Alert.AlertType.CONFIRMATION, owner, "Task Created!", 
                "Task Created");
    }

    @FXML
    protected void handleToggle(ActionEvent event) {
        // Handles toggle for Minor Major and Critical
        ToggleButton btnSelected = (ToggleButton) event.getSource();

        String statusName=btnSelected.getId().toString();
        if (btnSelected.isSelected()){
            if(statusName.equalsIgnoreCase("Minor")){
                //Figure out how to update it in the database
            } else if(statusName.equalsIgnoreCase("Major")){
                //do something
            } else if(statusName.equalsIgnoreCase("Critical")){
                //do something
            }
        }
    }
}