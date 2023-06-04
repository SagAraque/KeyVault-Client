package com.example.keyvault_client.viewControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GeneratorController {
    @FXML
    Button accept, cancel, generate;
    @FXML
    CheckBox capital, numbers, simbols;
    @FXML
    Label sliderNum, password;
    @FXML
    Slider slider;
    CreateUpdateController controller;
    private static final char[] LOWER_CHARACTERS = { 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    private static final char[] UPPER_CHARACTERS = { 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    private static final char[] NUMERIC_CHARACTERS = { '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9' };
    private static final char[] SPECIAL_CHARACTERS = { '~', '!', '@', '#',
            '$', '%', '^', '&', '*', '-', '_', '=', '+','?' };

    public void initialize(CreateUpdateController controller){
        slider.valueProperty().addListener((observableValue, number, newVal) -> {
            StackPane trackPane = (StackPane) slider.lookup(".track");
            double percent = (newVal.doubleValue() - slider.getMin()) / (slider.getMax() - slider.getMin());

            String style = "-fx-background-color: linear-gradient(to right, #475464 "+ percent +" , #e5e4e4 "+ percent +");";
            trackPane.setStyle(style);

            sliderNum.setText(String.valueOf(newVal.intValue()));
        });

        this.controller = controller;
        generatePassword();
    }

    @FXML
    public void generatePassword(){
        int length = Integer.parseInt(sliderNum.getText());
        int random = length == 8 ? 2 : ThreadLocalRandom.current().nextInt(2 + (length / 8) - 2, (length / 8) + 4);
        StringBuilder builder = new StringBuilder();

        if(capital.isSelected())
            appendCharacters(builder, UPPER_CHARACTERS, random);

        if(simbols.isSelected())
            appendCharacters(builder, SPECIAL_CHARACTERS, random);

        if(numbers.isSelected())
            appendCharacters(builder, NUMERIC_CHARACTERS, random);

        appendCharacters(builder, LOWER_CHARACTERS, length - builder.length());

        char[] passwordArray = builder.toString().toCharArray();

        for (int i = 0; i < length; i++) {
            int pos = new Random().nextInt(length);
            char temp = passwordArray[i];

            passwordArray[i] = passwordArray[pos];
            passwordArray[pos] = temp;
        }

        password.setText(new String(passwordArray));

    }

    @FXML
    public void setPassword(){
        controller.setPasswordOnField(password.getText());
    }

    @FXML
    public void removeModal(){
        controller.removeModal();
    }

    private void appendCharacters(StringBuilder builder, char[] array, int random){
        int arrayPos;

        for (int i = 0; i < random; i++){
            arrayPos = ThreadLocalRandom.current().nextInt(0, array.length);

            builder.append(array[arrayPos]);
        }
    }
}
