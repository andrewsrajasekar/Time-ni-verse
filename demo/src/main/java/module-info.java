module com.timeniverse {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.timeniverse to javafx.fxml;
    exports com.timeniverse;
}
