package com.timeniverse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import com.timeniverse.db_utils.DbConnection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private ObservableList<TaskData> originalList;

    @FXML private TableView<TaskData> tableView;
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
    @FXML private FolderData currentFolder;
    @FXML private Button completeButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private TextField searchBox;

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
        Window owner = completeButton.getScene().getWindow();

        Optional<ButtonType> result = AlertHelper.showAlert(AlertType.INFORMATION, owner, "Task Finished!",
                "Task Marked as Finished!");

        if (result.get() == ButtonType.OK) {
            tableView.getItems().setAll(getData());
            doSearchManual();
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
            doSearchManual();
            vbox_detail.setVisible(false);
        }
    }

    private void doSearchManual(){
        originalList = tableView.getItems();
        searchTable(null);
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

        doSearchManual();


        EventHandler<MouseEvent> onClick = this::onRowClick;
        
        tableView.setRowFactory(param -> {
            TableRow<TaskData> row = new TableRow<TaskData>();
            row.setOnMouseClicked(onClick);
            return row;
          });



        setListViewFolderInfo();
        listViewListener();
    }

    public void searchTable(ActionEvent event){
        String searchTerm = searchBox.getText();

        if (searchTerm == null || searchTerm.isEmpty()) {
            // If the search box is empty, restore the original list
            tableView.setItems(originalList);
        } else {
            // Get the list of tasks from the TableView
            ObservableList<TaskData> tasks = originalList;
    
            // Filter the tasks based on the search term
            FilteredList<TaskData> filteredTasks = tasks.filtered(task -> {
                // Replace this with your own logic to determine if the task should be included in the filtered list
                return task.getName().toLowerCase().contains(searchTerm.toLowerCase());
            });
    
            // Set the filtered tasks to be displayed in the TableView
            tableView.setItems(filteredTasks);
        }
    }

    public void onRowClick(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_CLICKED") ){
            @SuppressWarnings("unchecked")
            TableRow<Object> row = (TableRow<Object>) event.getSource();
            Boolean isVBoxVisible = vbox_detail.isVisible();
            currentRowSelected = row.getIndex();
            if(row.isEmpty()){
                vbox_detail.setVisible(false);
                tableView.getSelectionModel().clearSelection();
            }else{
                currentTaskData = (TaskData)row.getItem();
                if(tableView.getSelectionModel().getSelectedCells().size() > 0){
                    vbox_detail.setVisible(true);
                    detail_task_name.setText(currentTaskData.getName());
                    detail_task_description.setText(currentTaskData.getDescription());
                    if(currentFolder.getFolder_name().equalsIgnoreCase("Completed Task")){
                        editButton.setDisable(true);
                        completeButton.setDisable(true);
                    }else{
                        editButton.setDisable(false);
                        completeButton.setDisable(false);
                    }
                }  
            }
            event.consume();
        }
    }

    public void setListViewFolderInfo(){
        listView.getItems().clear();
        listView.getItems().setAll(getFolderData(true));
        listView.getSelectionModel().select(0);
        listView.getFocusModel().focus(0);
    }

    public void setListViewFolderBasedOnFolderId(Integer folderId){

    }

    public void listViewListener(){
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FolderData>() {
            @Override
            public void changed(ObservableValue<? extends FolderData> observable, FolderData oldValue, FolderData newValue) {
                tableView.setItems(null);
                if(newValue.getFolder_name() == "All"){
                    tableView.setItems(getData());
                }else if(newValue.getFolder_name() == "Completed Task"){
                    tableView.setItems(getCompletedTaskData());
                }else{
                    tableView.setItems(getDataBasedOnFolderId(newValue.getId()));
                }
                doSearchManual();
                currentFolder = newValue;
                resetData();
            }
        });
    }

    public void resetData(){
        vbox_detail.setVisible(false);
        currentRowSelected = 0;
        currentTaskData = null;
    }

    public List<FolderData> getFolderData(Boolean setDefaultAll){
        JSONArray data = DbConnection.getFolderInfoInSortedOrder(DbConnection.getFolderInfo());
        Iterator<Object> dataIter = data.iterator();
        List<FolderData> result = new ArrayList<>();
        Integer index = 0;
       
        FolderData systemObj = new FolderData();
        systemObj.setId(0);
        systemObj.setFolder_name("All");
        systemObj.setIs_default(false);
        systemObj.setOrder_number(0);
        result.add(systemObj);

        if(setDefaultAll){
            currentFolder = systemObj;
        }

        Integer largeInteger = 1;
        
        while(dataIter.hasNext()){
            JSONObject values = (JSONObject)dataIter.next();
            FolderData valueObj = new FolderData();
            valueObj.setId(Integer.valueOf("" + values.get("id")));
            valueObj.setFolder_name(""+values.get("name"));
            valueObj.setIs_default(Boolean.valueOf(""+values.get("is_default")));
            valueObj.setOrder_number(Integer.valueOf(""+values.get("order_number")));
            index++;
            if(largeInteger < valueObj.getId()){
                largeInteger = valueObj.getId();
            }
            result.add(valueObj);
        }

        systemObj = new FolderData();
        systemObj.setId(largeInteger + 1);
        systemObj.setFolder_name("Completed Task");
        systemObj.setIs_default(false);
        systemObj.setOrder_number(0);
        result.add(systemObj);

        return result;
    }
    

    private ObservableList<TaskData> getData(){
        JSONArray data = DbConnection.getTaskInfo();
        Iterator<Object> dataIter = data.iterator();
        ObservableList<TaskData> result = FXCollections.observableArrayList();
        while(dataIter.hasNext()){
            JSONObject values = (JSONObject)dataIter.next();
            TaskData valueObj = new TaskData();
            valueObj.setId(Integer.valueOf("" + values.get("id")));
            valueObj.setFolder_id(Integer.valueOf("" + values.get("folder_id")));
            valueObj.setName(""+values.get("name"));
            valueObj.setDescription(""+values.get("task_info"));
            valueObj.setDuration(Integer.valueOf(""+values.get("time_to_complete")));
            valueObj.setDeadline(DbConnection.getLocalDateForGivenInteger(Long.valueOf(""+values.get("deadline_timestamp"))));
            valueObj.setPriority(Boolean.valueOf(""+values.get("is_priority")));
            result.add(valueObj);
        }

        return result;
    }

    private ObservableList<TaskData> getCompletedTaskData(){
        JSONArray data = DbConnection.getCompletedTaskInfo();
        Iterator<Object> dataIter = data.iterator();
        ObservableList<TaskData> result = FXCollections.observableArrayList();
        while(dataIter.hasNext()){
            JSONObject values = (JSONObject)dataIter.next();
            TaskData valueObj = new TaskData();
            valueObj.setId(Integer.valueOf("" + values.get("id")));
            valueObj.setFolder_id(Integer.valueOf("" + values.get("folder_id")));
            valueObj.setName(""+values.get("name"));
            valueObj.setDescription(""+values.get("task_info"));
            valueObj.setDuration(Integer.valueOf(""+values.get("time_to_complete")));
            valueObj.setDeadline(DbConnection.getLocalDateForGivenInteger(Long.valueOf(""+values.get("deadline_timestamp"))));
            valueObj.setPriority(Boolean.valueOf(""+values.get("is_priority")));
            result.add(valueObj);
        }

        return result;
    }

    private ObservableList<TaskData> getDataBasedOnFolderId(Integer folderId){
        JSONArray data = DbConnection.getTaskInfoBasedOnFolderId(folderId);
        Iterator<Object> dataIter = data.iterator();
        ObservableList<TaskData> result = FXCollections.observableArrayList();
        while(dataIter.hasNext()){
            JSONObject values = (JSONObject)dataIter.next();
            TaskData valueObj = new TaskData();
            valueObj.setId(Integer.valueOf("" + values.get("id")));
            valueObj.setFolder_id(Integer.valueOf("" + values.get("folder_id")));
            valueObj.setName(""+values.get("name"));
            valueObj.setDescription(""+values.get("task_info"));
            valueObj.setDuration(Integer.valueOf(""+values.get("time_to_complete")));
            valueObj.setDeadline(DbConnection.getLocalDateForGivenInteger(Long.valueOf(""+values.get("deadline_timestamp"))));
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
        LocalDate deadline;
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
        public LocalDate getDeadline() {
            return deadline;
        }
        public void setDeadline(LocalDate deadline) {
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
