module com.example.event_registration_system_pl {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.event_registration_system_pl to javafx.fxml;
    exports com.example.event_registration_system_pl;
}