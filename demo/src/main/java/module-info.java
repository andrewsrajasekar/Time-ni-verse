module com.timeniverse {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires mongo.java.driver;
    requires org.json;
    requires java.logging;
    
    opens com.timeniverse to javafx.fxml;
    exports com.timeniverse;
}
