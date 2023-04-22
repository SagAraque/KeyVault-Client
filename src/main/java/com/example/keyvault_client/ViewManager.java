package com.example.keyvault_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewManager extends Application {
    private static Stage window;
    public static ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static ConnectionController conn = new ConnectionController();
    public static ResourceBundle bundle;

    @Override
    public void start(Stage stage) {
        window = stage;
        window.setTitle("KeyVault");
        window.getIcons().add(new Image(getClass().getResourceAsStream("icons/icon.png")));
        switchToLogin();
    }

    public static void main(String[] args) {
        Font.loadFont(ViewManager.class.getResourceAsStream("fonts/NotoSans-Medium.ttf"), 12);
        Font.loadFont(ViewManager.class.getResourceAsStream("fonts/NotoSans-Regular.ttf"), 12);
        Font.loadFont(ViewManager.class.getResourceAsStream("fonts/NotoSans-Bold.ttf"), 12);
        System.setProperty("prism.lcdtext", "false");

        Locale defaultLocale = Locale.getDefault();

        if(defaultLocale.getLanguage().equals("es"))
            defaultLocale = new Locale("es", "Es");
        else
            defaultLocale = new Locale("en", "Us");


        bundle = ResourceBundle.getBundle("com.example.keyvault_client.lang.lang", defaultLocale);

        launch();
    }

    public static void switchToLogin() {loadFXML("Login.fxml", 800, 600, false);}

    public static void switchToRegister() {
        loadFXML("register.fxml", 800, 600, false);
    }

    public static void switchToVerify(){loadFXML("verify.fxml", 800, 600, false);}

    public static void switchToMainView(){loadFXML("main-view.fxml", 900, 1280, true);}

    private static void loadFXML(String fxml, int height, int width, boolean isResizable){

        try {
            restartExecutor();

            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("views/" + fxml), bundle);
            Scene scene = new Scene(loader.load(), width, height,true, SceneAntialiasing.BALANCED);
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();

            window.setScene(scene);
            window.setMinWidth(width);
            window.setMinHeight(height);
            window.setWidth(isResizable ? screenBounds.getWidth() : width);
            window.setHeight(isResizable ? screenBounds.getHeight() : height);
            window.setMaximized(isResizable);
            window.setResizable(isResizable);

            window.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void restartExecutor(){
        executorService.shutdownNow();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void stop(){
        if(executorService != null)
            executorService.shutdownNow();

        conn.killTimer();
    }

    public static Stage getStage(){
        return window;
    }
}