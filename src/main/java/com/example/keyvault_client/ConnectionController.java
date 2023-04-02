package com.example.keyvault_client;

import com.keyvault.KeyVault;
import com.keyvault.entities.Items;
import com.keyvault.entities.Users;
import javafx.application.Platform;

import java.util.List;

public class ConnectionController extends Thread{
    private final KeyVault api = new KeyVault();
    String email, plainPassword;

    ConnectionController() {

    }

    public void register(String username, String password, AuthController controller){
        Users user = api.createUser(username, password);
        int response = api.register(user);

        Platform.runLater(() ->{
            if(response == 200){
                ViewManager.switchToRegister();
            }else{
                controller.displayMessage(api.getResponseMessage(response));
            }
        });
    }

    public void login(String username, String password, AuthController controller){
        email = username;
        plainPassword = password;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Users user = api.createUser(username, password);
        int response = api.login(user);

        Platform.runLater(() ->{
            switch (response){
                case 200 -> ViewManager.switchToMainView();
                case 102 ->ViewManager.switchToVerify();
                case 204 -> api.forceDisconnect();
                default -> controller.displayMessage(api.getResponseMessage(response));
            }
        });

    }

    public void verify(String code, boolean saveDevice, AuthController controller){
        Users user = api.createUser(email, plainPassword);

        int response = api.verifyLogin(code, saveDevice, user);

        Platform.runLater(() -> {
            if (response == 200) {
                ViewManager.switchToMainView();
            } else {
                controller.displayMessage(api.getResponseMessage(response));
            }
        });
    }

    public List<Items> getItems(){
        int response = api.getItems();

        return response == 200 ? (List<Items>) api.getResponseContent()[0] : null;

    }

    public KeyVault getApi() {
        return api;
    }

    public String getEmail() {
        return email;
    }
}
