package com.example.keyvault_client;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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
    private HBox errorMessage, verifyFields;
    @FXML
    private Label errorLabel;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private CheckBox saveDevice;

    private ExecutorService executorService;

    public void initialize(){
        executorService = ViewManager.executorService;

        if(verifyFields != null){

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
    }

    @FXML
    public void cursorToField(MouseEvent e){
        VBox parent = (VBox) e.getSource();
        parent.getChildren().get(1).requestFocus();
    }

    @FXML
    public void login(){
        username = usernameField.getText().trim();
        password = passwordField.getText().trim();

        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        executorService.execute(() -> ViewManager.conn.login(username, password, this));
    }

    @FXML
    public void register(){
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String repeatPassword = repeatPasswordField.getText().trim();

        switch (verifyFields(username, password, repeatPassword)) {
            case "EMPTY-FIELDS" -> displayMessage("Los campos no pueden estar vacios");
            case "PASSWORDS-WRONG-FORMAT" -> displayMessage("La contraseña debe ser mayor a 8 carateres e incluir una mayúscula");
            case "PASSWORDS-NOT-EQUALS" -> displayMessage("Las contraseñas no coinciden");
            case "EMAIL-WRONG-FORMAT" -> displayMessage("Correo electrónico invalido");
            case "OK" -> executorService.execute(() ->{
                progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

                ViewManager.conn.register(username, password, this);
            });
        }
    }

    @FXML
    public void verify(){
        StringBuilder code = new StringBuilder();

        for (Node node : verifyFields.getChildren())
            code.append(((TextField) node).getText().trim());

        if(code.toString().length() == 6){
            progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

            executorService.execute(() -> ViewManager.conn.verify(code.toString(), saveDevice.isSelected(), this));
        }else{
            displayMessage("El código debe contener 6 dígitos");
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

    public void displayMessage(String message){

        FadeTransition fade = new FadeTransition(Duration.millis(200), errorMessage);
        progressIndicator.setProgress(0);
        errorMessage.setVisible(true);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
        errorLabel.setText(message);

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
    }

    private String verifyFields(String username, String password, String repeatPassword){
        Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$&*])(?=\\S+$).{8,}$");
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

        if(username.length() == 0 || password.length() == 0 || repeatPassword.length() == 0)
            return "EMPTY-FIELDS";

        if(!pattern.matcher(password).matches())
            return "PASSWORDS-WRONG-FORMAT";

        if(!password.equals(repeatPassword))
            return "PASSWORDS-NOT-EQUALS";

        if(!emailPattern.matcher(username).matches())
            return "EMAIL-WRONG-FORMAT";

        return "OK";
    }
}
