package com.example.keyvault_client.viewControllers;

import com.example.keyvault_client.NodeGenerator;
import com.keyvault.entities.Devices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class ConfigController {

    @FXML
    Button closeButton;
    @FXML
    ChoiceBox<String> languageSelect, themeSelect;
    @FXML
    VBox devicesContainer;
    MainController mainController;

    public void initialize(MainController mainController, List<Devices> userDevices)
    {
        this.mainController = mainController;

        languageSelect.getItems().addAll("Castellano", "Inglés");
        languageSelect.getSelectionModel().selectFirst();
        themeSelect.getItems().addAll("Claro", "Oscuro");
        themeSelect.getSelectionModel().selectFirst();

        for (Devices device : userDevices)
            devicesContainer.getChildren().add(NodeGenerator.generateDeviceCard(device));
    }

    @FXML
    public void closeModal()
    {
        mainController.removeModal();
    }
}
