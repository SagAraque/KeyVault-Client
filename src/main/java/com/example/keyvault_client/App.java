package com.example.keyvault_client;

import com.example.keyvault_client.Controllers.Config;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        Config.load();
        ViewManager.main(args);
    }
}
