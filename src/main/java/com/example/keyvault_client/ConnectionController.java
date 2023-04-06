package com.example.keyvault_client;

import com.example.keyvault_client.viewControllers.AuthController;
import com.example.keyvault_client.viewControllers.MainController;
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
            if(response == 200)
            {
                ViewManager.switchToRegister();
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
            if (response == 200)
            {
                ViewManager.switchToMainView();
            }
            else
            {
                controller.displayMessage(api.getResponseMessage(response));
            }
        });
    }

    public List<Items> getItems(){
        int response = api.getItems();

        return response == 200 ? (List<Items>) api.getResponseContent()[0] : null;
    }

    public void deleteItem(Items item, MainController controller){
        int response =  api.deleteItem(item);
        boolean isNote = item.getNotesByIdI() != null;

        Platform.runLater(() ->{
            if(response == 200)
            {
                controller.removeItemFromArray(item);
                controller.showMessage(isNote ? "Nota modificada con éxito" : "Contraseña modificada con éxito", false);
                controller.reloadView();
            }
            else
            {
                controller.showMessage(api.getResponseMessage(response), true);
            }
        });
    }

    public void modItem(Items item, MainController controller){
        int response = api.modItem(item);
        boolean isNote = item.getNotesByIdI() != null;

        Platform.runLater(() ->{
            if(response == 200)
            {
                controller.updateItemFromArray(item);
                controller.showMessage(isNote ? "Nota modificada con éxito" : "Contraseña modificada con éxito" , false);
                controller.reloadView();
            }
            else
            {
                controller.showMessage(api.getResponseMessage(response), true);
            }
        });
    }

    public void changeFav(Items item, MainController controller){
        int response = api.modItem(item);

        Platform.runLater(() ->{
            if(response != 200)
            {
                controller.showMessage(api.getResponseMessage(response), false);
            }
        });
    }

    public void insertItem(Items newItem, MainController controller){
        int response = api.insertItem(newItem);
        boolean isNote = newItem.getNotesByIdI() != null;


        Platform.runLater(() ->{
            if(response == 200)
            {
                controller.addItemToArray(newItem);
                controller.showMessage(isNote ? "Nota modificada con éxito" : "Contraseña modificada con éxito", false);
                controller.reloadView();
            }
            else
            {
                controller.showMessage(api.getResponseMessage(response), true);
            }
        });
    }

    public Items createNote(String name, String content){
        return  api.createNote(name, "", content);
    }

    public Items createPassword(String name, String observations, String url, String email, String pass){
        return api.createPassword(name, observations, url, email, pass);
    }

    public KeyVault getApi() {
        return api;
    }

    public String getEmail() {
        return email;
    }
}
