package com.example.keyvault_client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;

import java.text.DecimalFormat;

public class GeneratorController {
    @FXML
    Button accept, cancel, generate;
    @FXML
    CheckBox capital, numbers, simbols;
    @FXML
    Label sliderNum, password;
    @FXML
    Slider slider;

    public void initialize(){
        slider.valueProperty().addListener((observableValue, number, newVal) -> {
            StackPane trackPane = (StackPane) slider.lookup(".track");
            DecimalFormat df = new DecimalFormat("#.###");
            String value = df.format(newVal.doubleValue() / 128).replace(",", ".");

            System.out.println(value);
            String style = "-fx-background-color: linear-gradient(to right, #475464 "+ value +" , #e5e4e4 "+ value +");";
            trackPane.setStyle(style);

            sliderNum.setText(String.valueOf(newVal.intValue()));
        });
    }
}
