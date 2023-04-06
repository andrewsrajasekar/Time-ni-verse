package com.timeniverse;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.timeniverse.db_utils.DbConnection;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @FXML
    private TableView tableView;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        scene = new Scene(root, 1000, 500);
        stage.setTitle("Time-ni-verse");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
        DbConnection.closeConnection();
    }
}