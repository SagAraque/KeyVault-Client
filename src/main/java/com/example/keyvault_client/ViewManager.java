package com.example.keyvault_client;

import com.example.keyvault_client.Controllers.Config;
import com.example.keyvault_client.Controllers.ConnectionController;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    public static boolean isDark = false;

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
        System.setProperty("javafx.acrylic.j2d.gpu", "true");

        String lang = Config.getKey("lang");
        isDark = Config.getKey("darkMode").equals("true");

        Locale defaultLocale;

        if(lang.equals("es"))
            defaultLocale = new Locale("es", "ES");
        else
            defaultLocale = new Locale("en", "US");

        bundle = ResourceBundle.getBundle("com.example.keyvault_client.lang.lang", defaultLocale);

        launch();
    }

    public static void switchToLogin() {loadFXML("views/Login.fxml", 800, 600, false);}
    public static void switchToRegister() {
        loadFXML("views/register.fxml", 800, 600, false);
    }
    public static void switchToVerify(){loadFXML("views/verify.fxml", 800, 600, false);}
    public static void switchToMainView(){loadFXML("views/main-view.fxml", 900, 1280, true);}

    private static void loadFXML(String fxml, int height, int width, boolean isResizable){
        try
        {
            executorService.shutdownNow();
            executorService = Executors.newSingleThreadExecutor();

            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource(fxml), bundle);
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();

            if(window.getScene() == null)
            {
                Scene scene = new Scene(loader.load(), width, height,true, SceneAntialiasing.BALANCED);
                scene.getStylesheets().add(ViewManager.class.getResource("style-min.css").toString());
                window.setScene(scene);
            }
            else
            {
                window.getScene().setRoot(loader.load());
            }

            changeStyleSheet();

            window.setMinWidth(width);
            window.setMinHeight(height);
            window.setWidth(isResizable ? screenBounds.getWidth() : width);
            window.setHeight(isResizable ? screenBounds.getHeight() : height);
            window.setMaximized(isResizable);
            window.setResizable(isResizable);

            window.show();
        }
         catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void displayMessage(String message, HBox errorMessage, Label errorLabel, ProgressIndicator progressIndicator){
        Platform.runLater(() -> {
            FadeTransition fade = new FadeTransition(Duration.millis(200), errorMessage);

            if(progressIndicator != null)
                progressIndicator.setProgress(0);

            errorMessage.setVisible(true);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
            errorLabel.setText(ViewManager.bundle.getString(message));

            new Thread(() ->{
                try {
                    Thread.sleep(3000);
                    fade.setFromValue(1.0);
                    fade.setToValue(0.0);
                    fade.setOnFinished(e -> errorMessage.setVisible(false));
                    fade.play();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });
    }

    public static void changeTheme(boolean changeToDark) {
        isDark = changeToDark;
        Config.setKey("darkMode", String.valueOf(changeToDark));
        switchToMainView();
    }

    private static void changeStyleSheet()
    {
        try
        {
          if(isDark)
              window.getScene().getStylesheets().add(ViewManager.class.getResource("darkMode-min.css").toString());
          else
              window.getScene().getStylesheets().remove(1);
        }
        catch (IndexOutOfBoundsException ignored)
        {

        }
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