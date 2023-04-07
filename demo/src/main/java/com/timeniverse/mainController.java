package com.timeniverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.timeniverse.db_utils.DbConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class mainController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML private TableView tableView;
    @FXML private TableColumn<TaskData, String> task_name;
    @FXML private TableColumn<TaskData, String> task_description;
    @FXML private TableColumn<TaskData, String> task_duration;
    @FXML private TableColumn<TaskData, String> task_deadline;
    @FXML private TableColumn<TaskData, String> task_priority;

    public void switchToInputForm(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("inputForm.fxml"));
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
    }
    

    private List<TaskData> getData(){
        JSONArray data = DbConnection.getTaskInfo();
        Iterator<Object> dataIter = data.iterator();
        List<TaskData> result = new ArrayList<>();
        while(dataIter.hasNext()){
            JSONObject values = (JSONObject)dataIter.next();
            TaskData valueObj = new TaskData();
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
        String name;
        String description;
        Integer duration;
        Integer deadline;
        Boolean priority;

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
    }
}
