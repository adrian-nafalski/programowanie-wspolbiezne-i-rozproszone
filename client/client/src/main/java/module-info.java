module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires server;
    requires java.rmi;


    opens com.example.client to javafx.fxml;
    exports com.example.client;
}