package com.example.keyvault_client;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NodeGenerator {
    public static Button generateActionButton(String icon, String text, String... classText){
        ImageView iconView = new ImageView(new Image(NodeGenerator.class.getResourceAsStream("icons/" + icon)));
        iconView.setFitHeight(22);
        iconView.setFitWidth(22);

        Button button = new Button();
        button.setGraphic(iconView);
        button.getStyleClass().addAll(classText);

        if(text != null) button.setText(text);

        return button;
    }
}
