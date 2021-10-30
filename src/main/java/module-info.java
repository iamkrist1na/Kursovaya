module com.company.soldatowa {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires commons.math3;
    requires java.sql;
    requires spring.jdbc;
    requires spring.core;
    requires hsqldb;

    opens com.company.soldatowa.view to javafx.fxml, javafx.base, javafx.controls, javafx.graphics;
    opens com.company.soldatowa.controllers to javafx.fxml, javafx.base, javafx.controls, javafx.graphics;

}