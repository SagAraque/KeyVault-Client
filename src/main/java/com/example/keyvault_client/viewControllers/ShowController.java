package com.example.keyvault_client.viewControllers;

import com.example.keyvault_client.ViewManager;
import com.keyvault.database.models.Items;
import com.keyvault.database.models.Notes;
import com.keyvault.database.models.Passwords;
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
    ImageView eyeIcon, copyUserIcon, copyPassIcon, copyUrlIcon, shareUrlIcon;
    HBox topMenuContainer;
    Items selectedItem;
    Image openEyeIcon;
    Image closeEyeIcon;
    boolean passIsVisible = false;

    public void initialize(Items item, HBox topMenu){
        nameField.setText(item.getName());

        if(item.getPasswordsByIdI() != null)
        {
            Passwords password = item.getPasswordsByIdI();
            usernameField.setText(password.getEmailP());
            passwordField.setText(password.getPassP());
            noteField.setText(item.getObservations());
            urlField.setText(password.getUrl());

            String darkConcat = ViewManager.isDark ? "Dark" : "";
            Image copyImg = new Image(ViewManager.class.getResourceAsStream("icons/copy"+ darkConcat +".png"));
            Image shareImg = new Image(ViewManager.class.getResourceAsStream("icons/share"+ darkConcat +".png"));

            openEyeIcon = new Image(ViewManager.class.getResourceAsStream("icons/openEye"+ darkConcat +".png"));
            closeEyeIcon = new Image(ViewManager.class.getResourceAsStream("icons/closeEye"+ darkConcat +".png"));

            copyPassIcon.setImage(copyImg);
            copyUrlIcon.setImage(copyImg);
            copyUserIcon.setImage(copyImg);
            shareUrlIcon.setImage(shareImg);
            eyeIcon.setImage(openEyeIcon);

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

        topMenuContainer = topMenu;
        selectedItem = item;
    }

    @FXML
    public void goTo() throws URISyntaxException, IOException {
        String url = urlField.getText();

        if(!url.contains("https://"))
            url = "https://" + url;

        Desktop.getDesktop().browse(new URI(url));
    }

    @FXML
    public void changePasswordVisibility() {
        String pass = passwordField.getText();

        passIsVisible = !passIsVisible;
        passwordField.setText(null);
        passwordField.setText(pass);

        eyeIcon.setImage(passIsVisible ? closeEyeIcon : openEyeIcon);
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
