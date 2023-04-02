module com.example.keyvault_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires KeyVault.API;
    requires java.sql;
    requires org.controlsfx.controls;
    requires java.desktop;

    opens com.example.keyvault_client to javafx.fxml;
    exports com.example.keyvault_client;
}