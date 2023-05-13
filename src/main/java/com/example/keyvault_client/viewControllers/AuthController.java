package com.example.keyvault_client.viewControllers;

import com.example.keyvault_client.ConnectionController;
import com.example.keyvault_client.ViewManager;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

public class AuthController {
    @FXML
    private TextField usernameField;
    private static String username, password;
    @FXML
    private PasswordField passwordField, repeatPasswordField;
    @FXML
    private ImageView eyeIconPassword, eyeIconRepeatPassword;
    @FXML
    private HBox errorMessage, verifyFields;
    @FXML
    private Label errorLabel;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private CheckBox saveDevice;
    private Image openEyeIcon = new Image(ViewManager.class.getResourceAsStream("icons/openEye.png"));
    private Image closeEyeIcon = new Image(ViewManager.class.getResourceAsStream("icons/closeEye.png"));
    private ExecutorService executorService;
    boolean isPassVisible, isPassRepeatVisible;

    public void initialize(){
        executorService = ViewManager.executorService;

        if(verifyFields != null){
    private ConnectionController connectionController;

            List<Node> fields = verifyFields.getChildren();

            for (Node node : fields) {
                TextField field = (TextField) node;
                field.setTextFormatter(new TextFormatter<>(change ->
                    change.getControlNewText().length() <= 1 ? change : null
                ));

                field.textProperty().addListener((obs, oldValue, newValue) ->{
                    if(!newValue.matches("\\d*"))
                        field.setText(newValue.replaceAll("[^\\d]", ""));
                });
            }
        }

        if(repeatPasswordField == null && ViewManager.conn.getEmail() != null && usernameField != null)
            usernameField.setText(ViewManager.conn.getEmail());

        if(repeatPasswordField != null)
        {
            repeatPasswordField.setSkin(new TextFieldSkin(repeatPasswordField){
                @Override
                protected String maskText(String text){
                    if(isPassRepeatVisible)
                        return text;

                    return super.maskText(text);
                }
            });
        }
    public void initialize()
    {
        this.executorService = ViewManager.executorService;
        this.connectionController = ViewManager.conn;

        passwordField.setSkin(new TextFieldSkin(passwordField){
            @Override
            protected String maskText(String text){
                if(isPassVisible)
                    return text;
        if(repeatPasswordField == null && connectionController.getEmail() != null && usernameField != null)
            usernameField.setText(connectionController.getEmail());

                return super.maskText(text);
            }
        });
    }

    @FXML
    public void changePasswordVisibility()
    {
        String pass = passwordField.getText();
        isPassVisible = !isPassVisible;
        passwordField.setText(null);
        passwordField.setText(pass);

        eyeIconPassword.setImage(isPassVisible ? openEyeIcon : closeEyeIcon);
    }

    @FXML
    public void changePasswordRepeatVisibility()
    {
        String pass = repeatPasswordField.getText();
        isPassRepeatVisible = !isPassRepeatVisible;
        repeatPasswordField.setText(null);
        repeatPasswordField.setText(pass);

        eyeIconRepeatPassword.setImage(isPassRepeatVisible ? openEyeIcon : closeEyeIcon);
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

    @FXML
    public void moveFocusVerify(KeyEvent e){
        TextField field = (TextField) e.getSource();
        int pos = verifyFields.getChildren().indexOf(field);
        List<Node> childs = verifyFields.getChildren();


        if(pos < childs.size() - 1  && !e.getCharacter().trim().isBlank()){
            childs.get(pos + 1).requestFocus();
        }else if(pos != 0 && e.getCharacter().trim().isBlank()){
            if(pos != childs.size() - 1)
                ((TextField) childs.get(pos - 1)).setText("");

            childs.get(pos - 1).requestFocus();
        }
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
