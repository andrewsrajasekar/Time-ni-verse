package com.timeniverse;

import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
 
public class taskTracker {
    @FXML private TableView<taskInput> tableView;
    @FXML private TextField assignment;
    @FXML private TextField duration;
    @FXML private TextField deadline;
    @FXML private TextField priority;
    
    public taskTracker() {

    }
    
    @FXML
    private void switchToTaskInput() throws IOException {
        App.setRoot("inputForm");
    }
    
    @FXML
    protected void addTask(ActionEvent event) {
        ObservableList<taskInput> data = tableView.getItems();
        data.add(new taskInput(assignment.getText(),
            duration.getText(),
            deadline.getText(),
            priority.getText()
        ));

        assignment.setText("");
        duration.setText("");
        deadline.setText("");
        priority.setText("");   
    }

}