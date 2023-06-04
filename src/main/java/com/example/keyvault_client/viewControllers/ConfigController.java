package com.example.keyvault_client.viewControllers;

import com.example.keyvault_client.Controllers.Config;
import com.example.keyvault_client.Controllers.ConnectionController;
import com.example.keyvault_client.NodeGenerator;
import com.example.keyvault_client.ViewManager;
import com.keyvault.database.models.Devices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import java.io.IOException;
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
    private ConnectionController connectionController;
    private ResourceBundle bundle;
    boolean switchIsActive = false;

    public void initialize(MainController mainController, List<Devices> userDevices)
    {
        this.mainController = mainController;
        this.executorService = ViewManager.executorService;
        this.connectionController = ViewManager.conn;
        this.bundle = ViewManager.bundle;

        setSelects();

        if(connectionController.getApi().getAuthUser().isTotpverified())
        {
            switchBody.getStyleClass().add("switchBodyActive");
            switchIsActive = true;
        }

        for (Devices device : userDevices)
            devicesContainer.getChildren().add(NodeGenerator.generateDeviceCard(device));
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
        executorService.execute(() -> connectionController.closeAllSessions());
    }

    @FXML
    public void changeSwitch() throws IOException
    {
        if(switchIsActive)
            switchBody.getStyleClass().remove("switchBodyActive");
        else
            switchBody.getStyleClass().add("switchBodyActive");

        switchIsActive = !switchIsActive;

        if(switchIsActive)
        {
            mainController.removeModal();
            mainController.displayVerifyTotp();
        }
        else
        {
            executorService.submit(connectionController::changeTOTPstate);
        }
    }

    private void changeLanguage(int index) {
        Locale lang =  index == 0 ? new Locale("es", "ES") : new Locale("en", "US");
        Config.setKey("lang", lang.getLanguage());

        bundle = ResourceBundle.getBundle("com.example.keyvault_client.lang.lang", lang);
        ViewManager.bundle = bundle;

        modalTitle.setText(bundle.getString("configTitle"));
        languageLabel.setText(bundle.getString("languageLabel"));
        themeLabel.setText(bundle.getString("themeLabel"));
        sessionsLabel.setText(bundle.getString("sessionsLabel"));
        totpLabel.setText(bundle.getString("totpLabel"));
        deleteLabel.setText(bundle.getString("deleteLabel"));
        closeButton.setText(bundle.getString("close"));
        closeSessionButton.setText(bundle.getString("closeSessions"));
        deleteButton.setText(bundle.getString("deleteAccount"));

        setSelects();

        mainController.changeLanguage(bundle);
    }

    private void setSelects()
    {
        int indexLang = bundle.getLocale().toString().equals("es_ES") ? 0 : 1;
        int indexTheme = ViewManager.isDark ? 1 : 0;

        languageSelect.setOnAction(null);
        themeSelect.setOnAction(null);

        languageSelect.getItems().clear();
        languageSelect.getItems().add(bundle.getString("spanish"));
        languageSelect.getItems().add(bundle.getString("english"));
        languageSelect.getSelectionModel().select(indexLang);

        themeSelect.getItems().clear();
        themeSelect.getItems().add(bundle.getString("light"));
        themeSelect.getItems().add(bundle.getString("dark"));
        themeSelect.getSelectionModel().select(indexTheme);

        languageSelect.setOnAction(e ->{
            changeLanguage(languageSelect.getSelectionModel().getSelectedIndex());
        });

        themeSelect.setOnAction(e ->{
            ViewManager.changeTheme(themeSelect.getSelectionModel().getSelectedIndex() == 1);
        });
    }

    @FXML
    public void delete()
    {
        connectionController.deleteAccount();
    }
}
