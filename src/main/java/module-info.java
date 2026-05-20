module com.delivery {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.delivery to javafx.fxml;
    opens com.delivery.model to javafx.base;
    opens com.delivery.ui to javafx.fxml;

    exports com.delivery;
    exports com.delivery.model;
    exports com.delivery.ui;
    exports com.delivery.dao;
    exports com.delivery.util;
    exports com.delivery.export;
}
