package com.example.keyvault_client.Controllers;

import com.example.keyvault_client.ViewManager;

import java.io.*;
import java.util.Properties;

public class Config {
    private static Properties config;
    static String configDir = System.getProperty("user.home") + File.separator + "config";
    static String configFile = configDir + File.separator + "config.conf";


    public static void load() throws IOException {
        File directory = new File(configDir);
        config = new Properties();

        if(!directory.exists())
            createFile(directory);

        config.load(new FileInputStream(configFile));

    }

    public static String getKey(String key)
    {
        return config.getProperty(key);
    }

    public static void setKey(String key, String value){
        try (OutputStream outputStream = new FileOutputStream(configFile)){
            config.setProperty(key, value);
            config.store(outputStream, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createFile(File directory) {
            directory.mkdirs();

            setKey("darkMode", "false");
            setKey("lang", "es");
    }
}
