package com.timeniverse;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import com.timeniverse.db_utils.DbConnection;
import com.timeniverse.mainController.FolderData;
import com.timeniverse.mainController.TaskData;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

public class inputFormController {
    // sceneController handles any action done by user on page

    private Stage stage;
    private Scene scene;
    private Parent root;
    private FolderData defaultFolderData;
    private Integer defaultIndex;
    private Integer existingTaskId;

    public void setCurrentTaskData(TaskData currentTaskData) {
        this.assignment.setText(currentTaskData.getName());
        this.description.setText(currentTaskData.getDescription());
        this.duration.setText(currentTaskData.getDuration().toString());
        this.deadline.setValue(currentTaskData.getDeadline());
        ObservableList<FolderData> data = group.getItems();
        Integer index = 0;
        for(FolderData value : data){
            if(value.getId() == currentTaskData.getFolder_id()){
                group.getSelectionModel().select(index);
            }
            index++;
        }
        priority_button.setSelected(currentTaskData.getPriority());
        submitButton.setVisible(false);
        updateButton.setVisible(true);
        existingTaskId = currentTaskData.getId();
    }

    @FXML
    private TextField assignment;

    @FXML
    private TextField description;

    @FXML
    private TextField duration = new TextField();

    @FXML
    private DatePicker deadline= new DatePicker();

    @FXML
    private ComboBox<FolderData> group;

    @FXML
    private ToggleButton priority_button;

    @FXML
    private Button submitButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button addFolders;

    @FXML private VBox vbox_detail;

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
        Boolean isUpdate = ((Button) event.getSource()).getId().equals("updateButton");
        Window owner = submitButton.getScene().getWindow();
        Integer folder_id = -1;
        if (assignment.getText().trim().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                    "Please enter the task");
            return;
        }
        if (description.getText().trim().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                    "Please enter the task Description");
            return;
        }
        if (duration.getText().trim().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                    "Please enter the time to allocate for the task");
            return;
        }
        if (! ( Long.valueOf(duration.getText().trim().toString()) >= 1 && Long.valueOf(duration.getText().trim().toString()) <= 24 ) ) {

            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                    "Duration must be greater than 1 and less than 24");
            return;
        }
        if (deadline.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                    "Please select a deadline");
            return;
        }
        if (group.getValue() != null) {
            FolderData data = group.getValue();
            folder_id = data.getId();
        }

        if (folder_id < 0) {
            Optional<ButtonType> result = AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Task Creation Failed!",
                    "Task Creation Failed Due to folder issue");

            if (result.get() == ButtonType.OK && !isUpdate) {
                assignment.clear();
                description.clear();
                duration.clear();
                deadline.setValue(null);
                priority_button.setSelected(false);
            }
        } else {
            if (isUpdate) {
                DbConnection.updateTask(existingTaskId, assignment.getText().trim(), description.getText().trim(),
                        folder_id, Long.valueOf(duration.getText().trim()), DbConnection.getTimeStampFromLocalDate(deadline.getValue()),
                        priority_button.isSelected());
                Optional<ButtonType> result = AlertHelper.showAlert(Alert.AlertType.INFORMATION, owner, "Task Updated!",
                        "Task Updated");
                if (result.get() == ButtonType.OK) {
                    switchToMain(event);
                }
            } else {
                DbConnection.insertTaskInfo(assignment.getText().trim(), description.getText().trim(), folder_id,
                        Long.valueOf(duration.getText().trim()), DbConnection.getTimeStampFromLocalDate(deadline.getValue()),
                        priority_button.isSelected());

                Optional<ButtonType> result = AlertHelper.showAlert(Alert.AlertType.INFORMATION, owner, "Task Created!",
                        "Task Created");

                if (result.get() == ButtonType.OK) {
                    assignment.clear();
                    description.clear();
                    duration.clear();
                    deadline.setValue(null);
                    priority_button.setSelected(false);
                }
            }
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
    }

    private void setCurrentDate(){
        deadline.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
    
                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
    }

    @FXML private void initialize(){
        submitButton.setVisible(true);
        updateButton.setVisible(false);
        setNumberOnlyField();
        setDropDownData();
        setCurrentDate();

        group.setConverter(new StringConverter<FolderData>() {

            @Override
            public String toString(FolderData folderData) {
                if (folderData == null) return null;
                return folderData.getFolder_name().toString();
            }

            @Override
            public FolderData fromString(String arg0) {
                int a = 55;
                FolderData folderData = (FolderData) group.getValue();
                return folderData;
            }
        });

    }

    private void setDropDownData(){
        ObservableList<FolderData> obList = FXCollections.observableList(getFolderData());
        group.getItems().clear();
        group.setItems(obList);
        if(defaultFolderData != null){
            group.getSelectionModel().select(defaultIndex);
        }
    }

    public List<FolderData> getFolderData(){
        JSONArray data = DbConnection.getFolderInfoInSortedOrder(DbConnection.getFolderInfo());
        Iterator<Object> dataIter = data.iterator();
        List<FolderData> result = new ArrayList<>();
        Integer index = 0;
        while(dataIter.hasNext()){
            JSONObject values = (JSONObject)dataIter.next();
            FolderData valueObj = new mainController().new FolderData();
            valueObj.setId(Integer.valueOf("" + values.get("id")));
            valueObj.setFolder_name(""+values.get("name"));
            valueObj.setIs_default(Boolean.valueOf(""+values.get("is_default")));
            valueObj.setOrder_number(Integer.valueOf(""+values.get("order_number")));
            if(valueObj.getIs_default()){
                defaultFolderData = valueObj;
                defaultIndex = index;
            }
            index++;
            result.add(valueObj);
        }

        return result;
    }

    public void handleAddFolder(ActionEvent event) throws IOException{
        HashMap<String, Object> resultMap = new HashMap<String, Object>();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("folderPopUp.fxml"));
            // initializing the controller
            folderPopController popupController = new folderPopController();
            loader.setController(popupController);
            Parent layout;
            try {
                layout = loader.load();
                Scene scene = new Scene(layout);
                // this is the popup stage
                Stage popupStage = new Stage();
                // Giving the popup controller access to the popup stage (to allow the controller to close the stage) 
                popupController.setStage(popupStage);
                popupStage.initOwner((Stage)((Node)event.getSource()).getScene().getWindow());
                popupStage.initModality(Modality.WINDOW_MODAL);
                popupStage.setScene(scene);
                popupStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setDropDownData();

    }
    
}