package com.timeniverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import com.timeniverse.db_utils.DbConnection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class mainController {
    private Stage stage;
    private Scene scene;
    private Integer currentRowSelected = 0;
    private TaskData currentTaskData;
    private TaskData toBeEditedTask;

    @FXML private TableView tableView;
    @FXML private TableColumn<TaskData, Integer> task_id;
    @FXML private TableColumn<TaskData, String> task_name;
    @FXML private TableColumn<TaskData, String> task_description;
    @FXML private TableColumn<TaskData, String> task_duration;
    @FXML private TableColumn<TaskData, String> task_deadline;
    @FXML private TableColumn<TaskData, String> task_priority;
    @FXML private VBox vbox_detail;
    @FXML private Label detail_task_name;
    @FXML private Label detail_task_description;
    @FXML private ListView<FolderData> listView;
    @FXML private Button finishButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    

    public void switchToInputForm(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("inputForm.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1000, 500);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToFolderForm(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("folderForm.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1000, 500);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handleTaskFinish(ActionEvent event) {
        DbConnection.updateTaskCompletion(currentTaskData.getId(), true);
        Window owner = finishButton.getScene().getWindow();

        Optional<ButtonType> result = AlertHelper.showAlert(AlertType.INFORMATION, owner, "Task Finished!",
                "Task Marked as Finished!");

        if (result.get() == ButtonType.OK) {
            tableView.getItems().setAll(getData());
            vbox_detail.setVisible(false);
        }
    }

    @FXML
    public void handleTaskDelete(ActionEvent event){
        DbConnection.deleteTask(currentTaskData.getId());
        Window owner = deleteButton.getScene().getWindow();

        Optional<ButtonType> result = AlertHelper.showAlert(AlertType.INFORMATION, owner, "Task Deleted!",
                "Task Deleted Successfully!");

        if (result.get() == ButtonType.OK) {
            tableView.getItems().setAll(getData());
            vbox_detail.setVisible(false);
        }
    }

    @FXML
    public void handleEdit(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("inputForm.fxml"));
        Parent root = fxmlLoader.load();
        inputFormController controller = fxmlLoader.getController();
        controller.setCurrentTaskData(currentTaskData);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1000, 500);
        stage.setScene(scene);
        stage.show();
    }

    @FXML private void initialize(){
        task_name.setCellValueFactory(new PropertyValueFactory<TaskData, String>("name"));
        task_description.setCellValueFactory(new PropertyValueFactory<TaskData, String>("description"));
        task_duration.setCellValueFactory(new PropertyValueFactory<TaskData, String>("duration"));
        task_deadline.setCellValueFactory(new PropertyValueFactory<TaskData, String>("deadline"));
        task_priority.setCellValueFactory(new PropertyValueFactory<TaskData, String>("priority"));

        tableView.getItems().setAll(getData());

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getEventType().getName().equals("MOUSE_CLICKED") ){
                    Boolean isVBoxVisible = vbox_detail.isVisible();
                    currentTaskData = (TaskData)tableView.getSelectionModel().getSelectedItem();
                    if(tableView.getSelectionModel().getSelectedCells().size() > 0){
                        TablePosition pos = (TablePosition) tableView.getSelectionModel().getSelectedCells().get(0);
                        int row = pos.getRow();
                        if(row == currentRowSelected){
                            if(isVBoxVisible){
                                vbox_detail.setVisible(false);
                                return;
                            }
                        }
                        row = currentRowSelected;
                        vbox_detail.setVisible(true);
                        detail_task_name.setText(currentTaskData.getName());
                        detail_task_description.setText(currentTaskData.getDescription());
    
    
                    }   
                }
            }
        });

        setListViewFolderInfo();
        listViewListener();
        
    }

    public void setListViewFolderInfo(){
        listView.getItems().clear();
        listView.getItems().setAll(getFolderData());
        listView.getSelectionModel().select(0);
        listView.getFocusModel().focus(0);
    }

    public void setListViewFolderBasedOnFolderId(Integer folderId){

    }

    public void listViewListener(){
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FolderData>() {
            @Override
            public void changed(ObservableValue<? extends FolderData> observable, FolderData oldValue, FolderData newValue) {
                if(newValue.getFolder_name() == "All"){
                    tableView.getItems().setAll(getData());
                }else{
                    tableView.getItems().setAll(getDataBasedOnFolderId(newValue.getId()));
                }
                resetData();
            }
        });
    }

    public void resetData(){
        vbox_detail.setVisible(false);
        currentRowSelected = 0;
        currentTaskData = null;
    }

    public List<FolderData> getFolderData(){
        JSONArray data = DbConnection.getFolderInfoInSortedOrder(DbConnection.getFolderInfo());
        Iterator<Object> dataIter = data.iterator();
        List<FolderData> result = new ArrayList<>();
        Integer index = 0;
       
        FolderData allObj = new FolderData();
        allObj.setId(0);
        allObj.setFolder_name("All");
        allObj.setIs_default(false);
        allObj.setOrder_number(0);
        result.add(allObj);
        
        while(dataIter.hasNext()){
            JSONObject values = (JSONObject)dataIter.next();
            FolderData valueObj = new FolderData();
            valueObj.setId(Integer.valueOf("" + values.get("id")));
            valueObj.setFolder_name(""+values.get("name"));
            valueObj.setIs_default(Boolean.valueOf(""+values.get("is_default")));
            valueObj.setOrder_number(Integer.valueOf(""+values.get("order_number")));
            index++;
            result.add(valueObj);
        }

        return result;
    }
    

    private List<TaskData> getData(){
        JSONArray data = DbConnection.getTaskInfo();
        Iterator<Object> dataIter = data.iterator();
        List<TaskData> result = new ArrayList<>();
        while(dataIter.hasNext()){
            JSONObject values = (JSONObject)dataIter.next();
            TaskData valueObj = new TaskData();
            valueObj.setId(Integer.valueOf("" + values.get("id")));
            valueObj.setFolder_id(Integer.valueOf("" + values.get("folder_id")));
            valueObj.setName(""+values.get("name"));
            valueObj.setDescription(""+values.get("task_info"));
            valueObj.setDuration(Integer.valueOf(""+values.get("time_to_complete")));
            valueObj.setDeadline(Integer.valueOf(""+values.get("deadline_timestamp")));
            valueObj.setPriority(Boolean.valueOf(""+values.get("is_priority")));
            result.add(valueObj);
        }

        return result;
    }

    private List<TaskData> getDataBasedOnFolderId(Integer folderId){
        JSONArray data = DbConnection.getTaskInfoBasedOnFolderId(folderId);
        Iterator<Object> dataIter = data.iterator();
        List<TaskData> result = new ArrayList<>();
        while(dataIter.hasNext()){
            JSONObject values = (JSONObject)dataIter.next();
            TaskData valueObj = new TaskData();
            valueObj.setId(Integer.valueOf("" + values.get("id")));
            valueObj.setFolder_id(Integer.valueOf("" + values.get("folder_id")));
            valueObj.setName(""+values.get("name"));
            valueObj.setDescription(""+values.get("task_info"));
            valueObj.setDuration(Integer.valueOf(""+values.get("time_to_complete")));
            valueObj.setDeadline(Integer.valueOf(""+values.get("deadline_timestamp")));
            valueObj.setPriority(Boolean.valueOf(""+values.get("is_priority")));
            result.add(valueObj);
        }

        return result;
    }

    public class TaskData{
        Integer id;
        Integer folder_id;
        String name;
        String description;
        Integer duration;
        Integer deadline;
        Boolean priority;

        public Integer getId() {
            return id;
        }
        public void setId(Integer id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public Integer getDuration() {
            return duration;
        }
        public void setDuration(Integer duration) {
            this.duration = duration;
        }
        public Integer getDeadline() {
            return deadline;
        }
        public void setDeadline(Integer deadline) {
            this.deadline = deadline;
        }
        public Boolean getPriority() {
            return priority;
        }
        public void setPriority(Boolean priority) {
            this.priority = priority;
        }

        public Integer getFolder_id() {
            return folder_id;
        }
        public void setFolder_id(Integer folder_id) {
            this.folder_id = folder_id;
        }
    }

    public class FolderData{
        Integer id;
        String folder_name;
        Boolean is_default;
        Integer order_number;
        public Integer getId() {
            return id;
        }
        public void setId(Integer id) {
            this.id = id;
        }
        public String getFolder_name() {
            return folder_name;
        }
        public void setFolder_name(String folder_name) {
            this.folder_name = folder_name;
        }
        public Boolean getIs_default() {
            return is_default;
        }
        public void setIs_default(Boolean is_default) {
            this.is_default = is_default;
        }
        public Integer getOrder_number() {
            return order_number;
        }
        public void setOrder_number(Integer order_number) {
            this.order_number = order_number;
        }

        @Override
        public String toString() {
            return folder_name;
        }
        
    }
}
