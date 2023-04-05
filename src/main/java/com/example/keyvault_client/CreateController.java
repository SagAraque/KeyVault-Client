package com.example.keyvault_client;

import com.keyvault.entities.Items;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CreateController {
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
    StackPane mainBody;
    Items newItem = null;
    boolean isNote = false;

    public void initialize(StackPane mainBody){
        this.mainBody = mainBody;
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
    public void displayGenerator(){
        ViewManager.displayModalWindow("generator-view.fxml", mainBody);
    }

    private void hideNode(Node... nodes){
        MainController.changeNodeVisibility(false,nodes);
    }

    private void showNode(Node... nodes){
        MainController.changeNodeVisibility(true, nodes);
    }

    public int insertItem(){
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

        return ViewManager.conn.insertItem(newItem);
    }

    public Items getItem(){
        return  newItem;
    }
}
