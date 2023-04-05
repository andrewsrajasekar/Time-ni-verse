package com.timeniverse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.timeniverse.db_utils.DbConnection;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("main"), 1000, 500);
        stage.setTitle("Time-ni-verse");
        stage.setScene(scene);
        stage.show();
        DbConnection.insertUserInfo("andrews", "1234", "andrews@gmail.com");
        JSONArray userData = DbConnection.getUserInfo();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
        DbConnection.closeConnection();
    }

}