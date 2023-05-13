package com.example.keyvault_client;

import com.example.keyvault_client.viewControllers.MainController;
import com.keyvault.KeyVault;
import com.keyvault.database.models.Devices;
import com.keyvault.database.models.Items;
import com.keyvault.database.models.Users;
import javafx.application.Platform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConnectionController extends Thread{
    boolean local = true;
    private KeyVault api = new KeyVault(local);
    private Timer sessionTimer;
    private String email, plainPassword;
    private Users authUser = null;

    ConnectionController() {

    }

    public int register(String username, String password)
    {
        Users user = api.createUser(username, password);

        return api.register(user);
    }

    public int login(String username, String password)
    {
        email = username;
        plainPassword = password;

        Users user = api.createUser(username, password);
        int response = api.login(user);

        if(response == 200)
        {
            startTimer();
            authUser = api.getAuthUser();
        }

        return response;

    }

    public int verify(String code, boolean saveDevice)
    {
        Users user = api.createUser(email, plainPassword);
        int response = api.verifyLogin(code, saveDevice, user);

        if(response == 200)
            startTimer();

        return response;
    }

    public List<Items> getItems()
    {
        return consumeListResponse(api::getItems);
    }

    public List<Devices> getDevices()
    {
        return consumeListResponse(api::getDevices);
    }

    private <T> List<T> consumeListResponse(Supplier<Integer> getFunction)
    {
        restartTimer();
        int response = getFunction.get();

        if(response == 200)
        {
            return (List<T>) api.getResponseContent();
        }
        else if (response == 201)
        {
            closeSession(false);
        }

        return null;
    }

    public int deleteItem(Items delItem)
    {
        return performOperation(api::deleteItem, delItem);
    }

    public int modItem(Items modItem)
    {
        return performOperation(api::modItem, modItem);
    }

    public int insertItem(Items newItem)
    {
        return performOperation(api::insertItem, newItem);
    }

    public int performOperation(Function<Items, Integer> operation, Items item)
    {
        restartTimer();
        return operation.apply(item);
    }

    public void changeFav(Items item, MainController controller)
    {
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

    public BufferedImage changeTOTPstate()
    {
        restartTimer();
        int response = api.totp();
        BufferedImage bufferedImage = null;

        if(response == 200)
        {
            String qr = (String) api.getResponseContent();

            if(authUser.isTotpverified())
                authUser.setTotpverified(false);

            System.out.println(qr);

            if(qr != null)
                bufferedImage = api.generateQR(qr);
        }

        return bufferedImage;
    }

    public int verifyTOTP(String code)
    {
        restartTimer();
        int response = api.verifyTOTP(code);

        if(response == 200)
            authUser.setTotpverified(true);

        return response;
    }



    public Items createNote(String name, String content)
    {
        return  api.createNote(name, "", content);
    }

    public Items createPassword(String name, String observations, String url, String email, String pass)
    {
        return api.createPassword(name, observations, url, email, pass);
    }

    public void closeSession(boolean fullDelete)
    {
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

    public KeyVault getApi()
    {
        return api;
    }

    public String getEmail()
    {
        return email;
    }

    private void restartTimer()
    {
        sessionTimer.restartTimer();
    }

    private void startTimer()
    {
        sessionTimer = new Timer();
        sessionTimer.start();
    }

    public void killTimer()
    {
        if(sessionTimer != null)
            sessionTimer.stopThread();
    }
}
