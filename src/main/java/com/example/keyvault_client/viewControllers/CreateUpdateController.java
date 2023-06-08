package com.example.keyvault_client.viewControllers;

import com.example.keyvault_client.Controllers.Cache;
import com.example.keyvault_client.ViewManager;
import com.example.keyvault_client.nodes.PasswordFieldSkin;
import com.keyvault.database.models.Items;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CreateUpdateController {
    @FXML
    Label passwordButton, noteButton, noteTitle;
    @FXML
    TextField nameField, usernameField, urlField;
    @FXML
    PasswordFieldSkin passwordField;
    @FXML
    TextArea noteField;
    @FXML
    VBox urlBox, userBox;
    @FXML
    HBox passwordBox;
    @FXML
    SplitPane topMenu;
    @FXML
    ImageView eyeIcon, generatorImage;
    Image openEyeIcon;
    Image closeEyeIcon;
    MainController mainController;
    Items newItem = null;
    boolean isNote = false;

    public void initialize(MainController mainController){
        this.mainController = mainController;

        setMaxFields(32, nameField);
        setMaxFields(256, usernameField);
        setMaxFields(64, passwordField);
        setMaxTextArea(256, noteField);

        String darkConcat = ViewManager.isDark ? "Dark" : "";

        openEyeIcon = Cache.loadImage("icons/openEye"+ darkConcat +".png");
        closeEyeIcon = Cache.loadImage("icons/closeEye"+ darkConcat +".png");

        generatorImage.setImage(Cache.loadImage("icons/reload"+ darkConcat +".png"));
        eyeIcon.setImage(openEyeIcon);
    }

    @FXML
    public void actionMenu(MouseEvent event){
        Label target = (Label) event.getSource();

        if(!target.getStyleClass().contains("createSelectorSelected"))
        {
            target.getStyleClass().add("createSelectorSelected");

            if (target == noteButton)
            {
                passwordButton.getStyleClass().remove("createSelectorSelected");
                hideNode(urlBox, userBox, passwordBox);
            }
            else
            {
                noteButton.getStyleClass().remove("createSelectorSelected");
                showNode(urlBox, passwordBox, userBox);
            }

            isNote = !isNote;
        }
    }

    @FXML
    public void displayGenerator() throws IOException {
        mainController.displayGenerator(this);
    }

    @FXML
    public void changePasswordVisibility() {
        passwordField.changeVisibility();
        eyeIcon.setImage(passwordField.isPassVisible() ? closeEyeIcon : openEyeIcon);
    }

    public void setPasswordOnField(String password){
        passwordField.setText(password);
        mainController.removeModal();
    }

    public void removeModal(){
        mainController.removeModal();
    }

    public void enableEditMode(Items item){
        nameField.setText(item.getName());

        if(item.getPasswordsByIdI() == null)
        {
            hideNode(topMenu, userBox, passwordBox, urlBox);
            noteTitle.setText("Contenido de la nota");
            noteField.setText(item.getNotesByIdI().getContent());

            isNote = true;
        }
        else
        {
            hideNode(topMenu);

            usernameField.setText(item.getPasswordsByIdI().getEmailP());
            passwordField.setText(item.getPasswordsByIdI().getPassP());
            urlField.setText(item.getPasswordsByIdI().getUrl());
            noteField.setText(item.getObservations());
        }

        newItem = item;
    }

    private void hideNode(Node... nodes){
        MainController.changeNodeVisibility(false,nodes);
    }

    private void showNode(Node... nodes){
        MainController.changeNodeVisibility(true, nodes);
    }

    public Items generateItem(){
        if(isNote)
        {
            newItem = ViewManager.conn.createNote(nameField.getText(), noteField.getText());
        }
        else
        {
            newItem = ViewManager.conn.createPassword(nameField.getText(),
                    noteField.getText(),
                    urlField.getText(),
                    usernameField.getText(),
                    passwordField.getText());
        }

        return newItem;
    }

    public Items updateItem(){
        newItem.setName(nameField.getText());

        if(isNote)
        {
            newItem.getNotesByIdI().setContent(noteField.getText());
        }
        else
        {
            newItem.getPasswordsByIdI().setEmailP(usernameField.getText());
            newItem.getPasswordsByIdI().setPassP(passwordField.getText());
            newItem.getPasswordsByIdI().setUrl(urlField.getText());
            newItem.setObservations(noteField.getText());
        }

        return newItem;
    }

    private void setMaxFields(int max, TextField field){
        field.textProperty().addListener((observableValue, s, t1) -> {
            if(field.getText().length() > max)
                field.setText(field.getText().substring(0, max));
        });
    }

    private void setMaxTextArea(int max, TextArea field){
        field.textProperty().addListener((observableValue, s, t1) -> {
            if(field.getText().length() > max)
                field.setText(field.getText().substring(0, max));
        });
    }

    public Items getItem(){
        return  newItem;
    }

}
