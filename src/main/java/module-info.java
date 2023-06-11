module com.example.keyvault_client {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires KeyVault.API;
    requires java.sql;
    requires org.controlsfx.controls;
    requires java.desktop;

    opens com.example.keyvault_client to javafx.fxml;
    exports com.example.keyvault_client;
    exports com.example.keyvault_client.viewControllers;
    exports com.example.keyvault_client.nodes;
    opens com.example.keyvault_client.nodes to javafx.fxml;
    opens com.example.keyvault_client.viewControllers to javafx.fxml;
    exports com.example.keyvault_client.Controllers;
    opens com.example.keyvault_client.Controllers to javafx.fxml;
}