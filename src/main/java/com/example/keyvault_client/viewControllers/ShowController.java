package com.example.keyvault_client.viewControllers;

import com.example.keyvault_client.Controllers.Cache;
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
    Image openEyeIcon, closeEyeIcon;
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
            Image copyImg = Cache.loadImage("icons/copy"+ darkConcat +".png");
            Image shareImg = Cache.loadImage("icons/share"+ darkConcat +".png");

            openEyeIcon = Cache.loadImage("icons/openEye"+ darkConcat +".png");
            closeEyeIcon = Cache.loadImage("icons/closeEye"+ darkConcat +".png");

            copyPassIcon.setImage(copyImg);
            copyUrlIcon.setImage(copyImg);
            copyUserIcon.setImage(copyImg);
            shareUrlIcon.setImage(shareImg);
            eyeIcon.setImage(openEyeIcon);

            changeVisibility(true, userBox, passwordBox, urlBox);
        }
        else
        {
            Notes note = item.getNotesByIdI();
            noteField.setText(note.getContent());

            changeVisibility(false, userBox, passwordBox, urlBox);
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

    private void changeVisibility(boolean visible, Node... nodes){
        MainController.changeNodeVisibility(visible, nodes);
    }
}
