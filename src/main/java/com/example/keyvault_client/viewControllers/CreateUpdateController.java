package com.example.keyvault_client.viewControllers;

import com.example.keyvault_client.ViewManager;
import com.keyvault.entities.Items;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.TextFieldSkin;
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
    PasswordField passwordField;
    @FXML
    TextArea noteField;
    @FXML
    VBox urlBox, userBox;
    @FXML
    HBox passwordBox;
    @FXML
    SplitPane topMenu;
    @FXML
    ImageView eyeIcon;
    Image openEyeIcon = new Image(ViewManager.class.getResourceAsStream("icons/openEye.png"));
    Image closeEyeIcon = new Image(ViewManager.class.getResourceAsStream("icons/closeEye.png"));
    MainController mainController;
    Items newItem = null;
    boolean isNote = false;
    boolean passIsVisible = false;

    public void initialize(MainController mainController){
        passwordField.setSkin(new TextFieldSkin(passwordField){
            @Override
            protected String maskText(String text){
                if(passIsVisible)
                    return text;

                return super.maskText(text);
            }
        });

        this.mainController = mainController;

        setMaxFields(32, nameField);
        setMaxFields(256, usernameField);
        setMaxFields(64, passwordField);
        setMaxTextArea(256, noteField);
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

            noteTitle.setText(target == passwordButton ? "Anotación" : "Contenido de la nota");
            isNote = !isNote;
        }
    }

    @FXML
    public void displayGenerator() throws IOException {
        mainController.displayGenerator(this, "generator-view.fxml");
    }

    @FXML
    public void changePasswordVisibility() {
        String pass = passwordField.getText();

        passIsVisible = !passIsVisible;
        passwordField.setText(null);
        passwordField.setText(pass);

        eyeIcon.setImage(passIsVisible ? openEyeIcon : closeEyeIcon);
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
