package com.example.keyvault_client;

import javafx.application.Platform;

public class Timer extends Thread{
    private int timeSecs = 600;
    private volatile boolean running = true;

    @Override
    public void run(){
        try {
            while (timeSecs != 0 && running)
            {
                timeSecs--;
                Thread.sleep(1000);
            }

            if(running)
                Platform.runLater(ViewManager::switchToLogin);


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void restartTimer(){
        timeSecs = 600;
    }

    public void stopThread(){
        running = false;
    }
}
