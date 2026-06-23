module com.ocir {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.sql;

    opens com.ocir to javafx.fxml;
    opens com.ocir.controller to javafx.fxml;
    opens com.ocir.model to javafx.base;

    exports com.ocir;
}
