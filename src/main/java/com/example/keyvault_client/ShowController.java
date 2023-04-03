package com.example.keyvault_client;

import com.keyvault.entities.Items;
import com.keyvault.entities.Notes;
import com.keyvault.entities.Passwords;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ShowController {
    @FXML
    TextField usernameField, nameField, urlField;
    @FXML
    PasswordField passwordField;
    @FXML
    Label noteTitle;
    @FXML
    TextArea noteField;
    @FXML
    Button copyUser, copyPass, copyUrl, showPass, goToLink;
    @FXML
    HBox userBox, passwordBox;
    @FXML
    VBox urlBox;
    @FXML
    ImageView eyeIcon;
    Image openEyeIcon = new Image(getClass().getResourceAsStream("icons/openEye.png"));
    Image closeEyeIcon = new Image(getClass().getResourceAsStream("icons/closeEye.png"));

    boolean passIsVisible = false;

    public void initialize(Items item){
        nameField.setText(item.getName());

        if(item.getPasswordsByIdI() != null)
        {
            Passwords password = item.getPasswordsByIdI();
            usernameField.setText(password.getEmailP());
            passwordField.setText(password.getPassP());
            noteField.setText(item.getObservations());
        }
        else
        {
            Notes note = item.getNotesByIdI();
            noteField.setText(note.getContent());
            noteTitle.setText("Contenido de la nota");

            hideNode(userBox, passwordBox, urlBox);
        }

        passwordField.setSkin(new TextFieldSkin(passwordField){
            @Override
            protected String maskText(String text){
                if(passIsVisible)
                    return text;

                return super.maskText(text);
            }
        });
    }

    @FXML
    public void goTo() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(urlField.getText()));
    }

    @FXML
    public void changePasswordVisibility() {
        String pass = passwordField.getText();

        passIsVisible = !passIsVisible;
        passwordField.setText(null);
        passwordField.setText(pass);

        eyeIcon.setImage(passIsVisible ? openEyeIcon : closeEyeIcon);
    }

    @FXML
    public void copyField(ActionEvent event){
        Button target = (Button) event.getSource();
        String copyText = "";

        switch (target.getId())
        {
            case "copyUrl" -> copyText = urlField.getText();
            case "copyPass" -> copyText = passwordField.getText();
            case "copyUser" -> copyText = usernameField.getText();
            default -> copyText = "";
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(copyText), null);
    }

    private void hideNode(Node... nodes){
        MainController.changeNodeVisibility(false, nodes);
    }

}
