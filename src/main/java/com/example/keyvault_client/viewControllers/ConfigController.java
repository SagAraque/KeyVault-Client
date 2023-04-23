package com.example.keyvault_client.viewControllers;

import com.example.keyvault_client.NodeGenerator;
import com.example.keyvault_client.ViewManager;
import com.keyvault.entities.Devices;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

public class ConfigController {

    @FXML
    Button closeButton, deleteButton, closeSessionButton;
    @FXML
    ChoiceBox<String> languageSelect, themeSelect;
    @FXML
    Label modalTitle, themeLabel, languageLabel, totpLabel, sessionsLabel, deleteLabel;
    @FXML
    VBox devicesContainer, switchBody;
    MainController mainController;
    private ExecutorService executorService;
    boolean switchIsActive = false;

    public void initialize(MainController mainController, List<Devices> userDevices)
    {
        this.mainController = mainController;
        this.executorService = ViewManager.executorService;

        languageSelect.getItems().addAll("Castellano", "Inglés");
        languageSelect.getSelectionModel().selectFirst();
        themeSelect.getItems().addAll("Claro", "Oscuro");
        themeSelect.getSelectionModel().selectFirst();

        for (Devices device : userDevices)
            devicesContainer.getChildren().add(NodeGenerator.generateDeviceCard(device));

        languageSelect.setOnAction(e ->{
            changeLanguage(languageSelect.getSelectionModel().getSelectedIndex());
        });
    }

    @FXML
    public void closeModal()
    {
        mainController.removeModal();
    }

    @FXML
    public void closeAllSessions()
    {
        devicesContainer.getChildren().clear();
        devicesContainer.getChildren().add(new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS));
        executorService.execute(() -> ViewManager.conn.closeAllSessions());
    }

    @FXML
    public void changeSwitch()
    {
        if(switchIsActive)
            switchBody.getStyleClass().remove("switchBodyActive");
        else
            switchBody.getStyleClass().add("switchBodyActive");

        switchIsActive = !switchIsActive;
    }


    public void changeLanguage(int index)
    {
        System.out.println(index);
        Locale lang =  index == 0 ? new Locale("es", "Es") : new Locale("en", "Us");

        ViewManager.bundle = ResourceBundle.getBundle("com.example.keyvault_client.lang.lang", lang);
        modalTitle.setText(ViewManager.bundle.getString("configTitle"));
        languageLabel.setText(ViewManager.bundle.getString("languageLabel"));
        themeLabel.setText(ViewManager.bundle.getString("themeLabel"));
        sessionsLabel.setText(ViewManager.bundle.getString("sessionsLabel"));
        totpLabel.setText(ViewManager.bundle.getString("totpLabel"));
        deleteLabel.setText(ViewManager.bundle.getString("deleteLabel"));
        closeButton.setText(ViewManager.bundle.getString("close"));
        closeSessionButton.setText(ViewManager.bundle.getString("closeSessions"));
        deleteButton.setText(ViewManager.bundle.getString("deleteAccount"));
    }
}
