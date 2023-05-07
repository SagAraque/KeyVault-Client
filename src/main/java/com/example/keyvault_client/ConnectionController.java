package com.example.keyvault_client;

import com.example.keyvault_client.viewControllers.AuthController;
import com.example.keyvault_client.viewControllers.MainController;
import com.keyvault.KeyVault;
import com.keyvault.database.models.Devices;
import com.keyvault.database.models.Items;
import com.keyvault.database.models.Users;
import javafx.application.Platform;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ConnectionController extends Thread{
    boolean local = true;
    private KeyVault api = new KeyVault(local);
    private Timer sessionTimer;
    String email, plainPassword;

    ConnectionController() {

    }

    public void register(String username, String password, AuthController controller){
        Users user = api.createUser(username, password);
        int response = api.register(user);

        Platform.runLater(() ->{
            if(response == 200)
            {
                ViewManager.switchToLogin();
            }
            else
            {
                controller.displayMessage(api.getResponseMessage(response));
            }
        });
    }

    public void login(String username, String password, AuthController controller){
        email = username;
        plainPassword = password;

        Users user = api.createUser(username, password);
        int response = api.login(user);

        Platform.runLater(() ->{
            switch (response){
                case 200 -> {
                    startTimer();
                    ViewManager.switchToMainView();
                }
                case 102 ->ViewManager.switchToVerify();
                default -> controller.displayMessage(api.getResponseMessage(response));
            }
        });

    }

    public void verify(String code, boolean saveDevice, AuthController controller){
        Users user = api.createUser(email, plainPassword);
        int response = api.verifyLogin(code, saveDevice, user);

        Platform.runLater(() -> {
            if (response == 200)
            {
                startTimer();
                ViewManager.switchToMainView();
            }
            else
            {
                controller.displayMessage(api.getResponseMessage(response));
            }
        });
    }

    public List<Items> getItems(){
        restartTimer();
        int response = api.getItems();

        if(response == 200)
        {
            return (List<Items>) api.getResponseContent();
        }
        else if (response == 201)
        {
            closeSession(false);
            return  null;
        }
        else
        {
            return  null;
        }
    }

    public List<Devices> getDevices(){
        restartTimer();
        int response = api.getDevices();

        if(response == 200)
        {
            return (List<Devices>) api.getResponseContent();
        }
        else if (response == 201)
        {
            closeSession(false);
            return  null;
        }
        else
        {
            return  null;
        }
    }

    public void deleteItem(Items item, MainController controller){
        restartTimer();
        int response =  api.deleteItem(item);
        boolean isNote = item.getNotesByIdI() != null;

        Platform.runLater(() ->{
            switch (response) {
                case 200 -> {
                    controller.removeItemFromArray(item);
                    controller.showMessage(isNote ? "Nota modificada con éxito" : "Contraseña modificada con éxito", false);
                    controller.reloadView();
                }
                case 201 -> closeSession(false);
                default -> controller.showMessage(api.getResponseMessage(response), true);
            }
        });
    }

    public void modItem(Items item, MainController controller){
        restartTimer();
        int response = api.modItem(item);
        boolean isNote = item.getNotesByIdI() != null;

        Platform.runLater(() ->{
            switch (response) {
                case 200 -> {
                    controller.updateItemFromArray(item);
                    controller.showMessage(isNote ? "Nota modificada con éxito" : "Contraseña modificada con éxito" , false);
                    controller.reloadView();
                }
                case 201 -> closeSession(false);
                default -> controller.showMessage(api.getResponseMessage(response), true);
            }
        });
    }

    public void changeFav(Items item, MainController controller){
        restartTimer();
        int response = api.modItem(item);

        Platform.runLater(() ->{
            if (response == 201)
            {
                closeSession(false);
            }
            else if(response != 200)
            {
                controller.showMessage(api.getResponseMessage(response), false);
            }
        });
    }

    public void changeTOTPstate()
    {
        restartTimer();
        int response = api.totp();

        Platform.runLater(() -> {
            if(response == 200)
            {
                String qr = (String) api.getResponseContent();

                if(qr != null)
                {
                    BufferedImage bufferedImage = api.generateQR(qr);
                    WritableImage image = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
                    PixelWriter pixelWriter = image.getPixelWriter();

                    for (int x = 0; x < bufferedImage.getWidth(); x++) {
                        for (int y = 0; y < bufferedImage.getHeight(); y++) {
                            pixelWriter.setArgb(x, y, bufferedImage.getRGB(x, y));
                        }
                    }


                }

            }
            else
            {

            }
        });
    }

    public void insertItem(Items newItem, MainController controller){
        restartTimer();
        int response = api.insertItem(newItem);
        boolean isNote = newItem.getNotesByIdI() != null;

        Platform.runLater(() ->{
            switch (response) {
                case 200 -> {
                    controller.addItemToArray(newItem);
                    controller.showMessage(isNote ? "Nota creada con éxito" : "Contraseña creada con éxito", false);
                    controller.reloadView();
                }
                case 201 -> closeSession(false);
                default -> controller.showMessage(api.getResponseMessage(response), true);
            }
        });
    }

    public Items createNote(String name, String content){
        return  api.createNote(name, "", content);
    }

    public Items createPassword(String name, String observations, String url, String email, String pass){
        return api.createPassword(name, observations, url, email, pass);
    }

    public void closeSession(boolean fullDelete){
        plainPassword = null;
        api = new KeyVault(local);

        if(fullDelete) email = null;

        ViewManager.switchToLogin();
    }

    public void closeAllSessions()
    {
        api.clearDevices();
        plainPassword = null;
        api = new KeyVault(local);
        Platform.runLater(ViewManager::switchToLogin);
    }

    public KeyVault getApi() {
        return api;
    }

    public String getEmail() {
        return email;
    }

    private void restartTimer(){
        sessionTimer.restartTimer();
    }

    private void startTimer() {
        sessionTimer = new Timer();
        sessionTimer.start();
    }

    public void killTimer(){
        if(sessionTimer != null)
            sessionTimer.stopThread();
    }
}
