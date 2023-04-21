package com.timeniverse;

import com.timeniverse.db_utils.DbConnection;
import com.timeniverse.mainController.FolderData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class folderPopController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private FolderData defaultFolderData;
    private Integer defaultIndex;
    private Integer existingTaskId;

    @FXML
    private TextField folderName;

    @FXML
    private Label errorMsg;

    @FXML
    private Button addButton;

    @FXML
    private HBox errorHBox;

    @FXML private void initialize(){
    }

    public void handleAddFolder(ActionEvent event){
        errorMsg.setVisible(false);
        errorHBox.setVisible(false);
        String name = folderName.getText();
        if(name.trim().equals("")){
            errorHBox.setVisible(true);
            errorMsg.setText("Folder Name is Empty");
            errorMsg.setVisible(true);
        }else if(!name.trim().matches("^[a-zA-Z].*$")){
            errorHBox.setVisible(true);
            errorMsg.setText("Folder Name should start with letter");
            errorMsg.setVisible(true);
        }else if(name.trim().equalsIgnoreCase("all task") || name.trim().equalsIgnoreCase("completed task") || name.trim().equalsIgnoreCase("completed tasks") || name.trim().equalsIgnoreCase("all tasks")){
            errorHBox.setVisible(true);
            errorMsg.setText("Folder Name should not be system words like All Task, All Tasks, Completed Task, Completed Tasks");
            errorMsg.setVisible(true);
        }else if(DbConnection.isFolderExists(name)){
            errorHBox.setVisible(true);
            errorMsg.setText("Folder Name already exists");
            errorMsg.setVisible(true);
        }else{
            DbConnection.insertFolderInfo(name);
            stage.hide();
        }
    }

    public void setStage(Stage popupStage) {
        this.stage = popupStage;
    }

    public String getResult() {
        return this.folderName.getText();
    }

}
