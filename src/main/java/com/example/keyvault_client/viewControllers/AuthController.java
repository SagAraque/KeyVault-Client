package com.example.keyvault_client.viewControllers;

import com.example.keyvault_client.Controllers.ConnectionController;
import com.example.keyvault_client.ViewManager;
import com.example.keyvault_client.nodes.PasswordFieldSkin;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

public class AuthController {
    @FXML
    private TextField usernameField;
    private static String username, password;
    @FXML
    private PasswordFieldSkin passwordField, repeatPasswordField;
    @FXML
    private ImageView eyeIconPassword, eyeIconRepeatPassword, logo;
    @FXML
    private HBox errorMessage, verifyFields;
    @FXML
    private Label errorLabel;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private CheckBox saveDevice;

    private Image openEyeIcon;
    private Image closeEyeIcon;
    private ExecutorService executorService;
    private ConnectionController connectionController;

    public void initialize()
    {
        this.executorService = ViewManager.executorService;
        this.connectionController = ViewManager.conn;

        String darkConcat = ViewManager.isDark ? "Dark" : "";

        openEyeIcon = new Image(ViewManager.class.getResourceAsStream("icons/openEye"+ darkConcat +".png"));
        closeEyeIcon = new Image(ViewManager.class.getResourceAsStream("icons/closeEye"+ darkConcat +".png"));

        eyeIconPassword.setImage(openEyeIcon);

        if(eyeIconRepeatPassword != null)
            eyeIconRepeatPassword.setImage(openEyeIcon);

        if(ViewManager.isDark)
            logo.setImage(new Image(ViewManager.class.getResourceAsStream("icons/logoDark.png")));

        if(repeatPasswordField == null && connectionController.getEmail() != null && usernameField != null)
            usernameField.setText(connectionController.getEmail());

    }

    @FXML
    public void changePasswordVisibility()
    {
        passwordField.changeVisibility();
        eyeIconPassword.setImage(passwordField.isPassVisible() ? closeEyeIcon : openEyeIcon);
    }

    @FXML
    public void changePasswordRepeatVisibility()
    {
        repeatPasswordField.changeVisibility();
        eyeIconRepeatPassword.setImage(repeatPasswordField.isPassVisible() ? closeEyeIcon : openEyeIcon);
    }


    @FXML
    public void cursorToField(MouseEvent e)
    {
        VBox parent = (VBox) e.getSource();
        parent.getChildren().get(1).requestFocus();
    }

    @FXML
    public void login()
    {
        username = usernameField.getText().trim();
        password = passwordField.getText().trim();

        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        executorService.execute(() -> {
            int response = connectionController.login(username, password);
            Platform.runLater(() -> consumeResponse(response, ViewManager::switchToMainView));
        } );
    }

    @FXML
    public void register()
    {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String repeatPassword = repeatPasswordField.getText().trim();
        String fieldsCheck = verifyFields(username, password, repeatPassword);

        if(fieldsCheck.equals("OK"))
        {
            progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

            executorService.execute(() ->{
                int response = connectionController.register(username, password);
                Platform.runLater(() -> consumeResponse(response, ViewManager::switchToLogin));
            });
        }
        else
        {
            ViewManager.displayMessage(fieldsCheck,  errorMessage, errorLabel, progressIndicator);
        }
    }

    @FXML
    public void verify()
    {
        StringBuilder code = new StringBuilder();

        for (Node node : verifyFields.getChildren())
            code.append(((TextField) node).getText().trim());

        if(code.toString().length() == 6)
        {
            progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

            executorService.execute(() -> {
                int response = connectionController.verify(code.toString(), saveDevice.isSelected());
                Platform.runLater(() -> consumeResponse(response, ViewManager::switchToMainView));
            });
        }
        else
        {

            ViewManager.displayMessage("codeFormat", errorMessage, errorLabel, progressIndicator);
        }
    }

    private void consumeResponse(int response, Runnable successFunction)
    {
        switch (response)
        {
            case 200 -> successFunction.run();
            case 103 -> ViewManager.switchToVerify();
            default -> ViewManager.displayMessage("message" + response, errorMessage, errorLabel, progressIndicator);
        }
    }

    @FXML
    public void toLogin(){
        ViewManager.switchToLogin();
    }
    @FXML
    public void toRegister(){
        ViewManager.switchToRegister();
    }

    private String verifyFields(String username, String password, String repeatPassword)
    {
        Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$&*])(?=\\S+$).{8,}$");
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

        if(username.length() == 0 || password.length() == 0 || repeatPassword.length() == 0)
            return "emptyFields";

        if(!pattern.matcher(password).matches())
            return "wrongPasswordFormat";

        if(!password.equals(repeatPassword))
            return "wrongMatch";

        if(!emailPattern.matcher(username).matches())
            return "invalidEmail";

        return "OK";
    }
}
