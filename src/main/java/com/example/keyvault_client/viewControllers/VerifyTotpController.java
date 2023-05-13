package com.example.keyvault_client.viewControllers;

import com.example.keyvault_client.ConnectionController;
import com.example.keyvault_client.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class VerifyTotpController
{
    @FXML
    public HBox verifyFields, errorMessage, qrBox;
    @FXML
    public Label errorLabel;
    @FXML
    public ProgressIndicator progressIndicator;
    public MainController mainController;
    private ExecutorService executorService = ViewManager.executorService;
    private ConnectionController connectionController;

    public void initialize(MainController mainController)
    {
        this.mainController = mainController;
        this.connectionController = ViewManager.conn;

        executorService.submit(() -> {
            BufferedImage qr = connectionController.changeTOTPstate();

            WritableImage image = new WritableImage(qr.getWidth(), qr.getHeight());
            PixelWriter pixelWriter = image.getPixelWriter();

            for (int x = 0; x < qr.getWidth(); x++) {
                for (int y = 0; y < qr.getHeight(); y++) {
                    pixelWriter.setArgb(x, y, qr.getRGB(x, y));
                }
            }

            Platform.runLater(() -> {
                ImageView qrCode = new ImageView(image);
                qrBox.getChildren().clear();
                qrBox.getChildren().add(qrCode);
            });

        });
    }

    @FXML
    public void back(){
        try {
            mainController.removeModal();
            mainController.displayConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void verify()
    {
        StringBuilder code = new StringBuilder();

        for (Node node : verifyFields.getChildren())
            code.append(((TextField) node).getText().trim());

        if(code.toString().length() == 6){
            progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

            executorService.execute(() -> {
               System.out.println(code);
               int response = ViewManager.conn.verifyTOTP(code.toString());

               if(response == 200)
               {
                   Platform.runLater(this::back);
               }
               else
               {
                   ViewManager.displayMessage("message" + response, errorMessage, errorLabel, progressIndicator);
               }

            });
        }else{
            ViewManager.displayMessage("codeFormat", errorMessage, errorLabel, progressIndicator);
        }
    }
}
