module com.ocir {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires de.jensd.fx.glyphs.fontawesome;
    requires de.jensd.fx.glyphs.commons;

    opens com.ocir to javafx.fxml;
    opens com.ocir.controller to javafx.fxml;
    opens com.ocir.model to javafx.base;

    exports com.ocir;
}
