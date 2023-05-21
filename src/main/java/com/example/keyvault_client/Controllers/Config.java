package com.example.keyvault_client.Controllers;

import com.example.keyvault_client.ViewManager;

import java.io.*;
import java.util.Properties;

public class Config {
    private static Properties config;

    public static void load() throws IOException {
        config = new Properties();
        config.load(ViewManager.class.getResourceAsStream("config.conf"));
    }

    public static String getKey(String key)
    {
        return config.getProperty(key);
    }

    public static void setKey(String key, String value){
        try (OutputStream outputStream = new FileOutputStream(ViewManager.class.getResource("config.conf").getFile())){
            config.setProperty(key, value);
            config.store(outputStream, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
