module com.timeniverse {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    opens com.timeniverse to javafx.fxml;
    exports com.timeniverse;
}
