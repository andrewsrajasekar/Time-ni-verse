package com.timeniverse;

import java.io.IOException;
import java.util.Optional;

import com.timeniverse.db_utils.DbConnection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.stage.Window;

public class inputFormController {
    // sceneController handles any action done by user on page

    private Stage stage;
    private Scene scene;
    private Parent root;
    
    @FXML
    private TextField assignment;

    @FXML
    private TextField description;

    @FXML
    private TextField duration = new TextField();

    @FXML
    private TextField deadline= new TextField();

    @FXML
    private ComboBox courses;

    @FXML
    private ToggleButton priority_button;

    @FXML
    private Button submitButton;

    public void switchToMain(ActionEvent event) throws IOException {
    root = FXMLLoader.load(getClass().getResource("main.fxml"));
    stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    scene = new Scene(root, 1000, 500);
    stage.setScene(scene);
    stage.show();
    }
    
    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) throws Exception {
        // Submitting the task button
        Window owner = submitButton.getScene().getWindow();
        if(assignment.getText().trim().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter the task");
            return;
        }
        if(description.getText().trim().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter the task Description");
            return;
        }
        if(duration.getText().trim().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter the time to allocate for the task");
            return;
        }
        if(deadline.getText().trim().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter a deadline date using the format: MM-DD-YYYY");
            return;
        }

        //Dummy Folder Create we need to handle this in UI
        if(DbConnection.getFolderInfo().isEmpty()){
            DbConnection.insertFolderInfo("Dummy");
        }

         //Dummy Folder Created we need to handle this in UI
        Integer folder_id = Integer.parseInt( DbConnection.getFolderInfo().getJSONObject(0).get("id").toString() );

        DbConnection.insertTaskInfo(assignment.getText().trim(), description.getText().trim(), folder_id, Long.valueOf(duration.getText().trim()), Long.valueOf(deadline.getText().trim()), priority_button.isSelected());


        Optional<ButtonType> result = AlertHelper.showAlert(Alert.AlertType.INFORMATION, owner, "Task Created!", 
                "Task Created");

        if(result.get() == ButtonType.OK){
            assignment.clear();
            description.clear();
            duration.clear();
            deadline.clear();
            priority_button.setSelected(false);
        }
    }

    private void setNumberOnlyField(){
        duration.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    duration.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        deadline.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    deadline.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    @FXML private void initialize(){
        setNumberOnlyField();
    }
    
}