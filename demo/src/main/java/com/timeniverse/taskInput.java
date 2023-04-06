package com.timeniverse;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class taskInput {
    private final SimpleStringProperty assignment = new SimpleStringProperty("");
    private final SimpleStringProperty duration = new SimpleStringProperty("");
    private final SimpleStringProperty deadline = new SimpleStringProperty("");
    private final SimpleStringProperty priority = new SimpleStringProperty("");
   
    public taskInput(String assignment, String duration, String deadline, String priority) {
        setAssignment(assignment);
        setDuration(duration);
        setDeadline(deadline);
        setPriority(priority);
    }
    @FXML
    private void switchToTaskTracker() throws IOException {
        App.setRoot("main");
    }
    

    
    public String getAssignment() {
        return assignment.get();
    }

    public void setAssignment(String assign) {
        assignment.set(assign);
    }
        
    public String getDuration() {
        return duration.get();
    }
    
    public void setDuration(String hours) {
        duration.set(hours);
    }
    
    public String getDeadline() {
        return deadline.get();
    }
    
    public void setDeadline(String date) {
        deadline.set(date);
    }

    public String getPriority() {
        return priority.get();
    }
    
    public void setPriority(String isPriority ) {
        deadline.set(isPriority);
    }
}
