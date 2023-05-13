package com.example.keyvault_client.nodes;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.util.List;

public class SingleNumInput extends TextField {
    public SingleNumInput()
    {
        super();

        this.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 1 ? change : null
        ));

        this.textProperty().addListener((obs, oldValue, newValue) ->{
            if(!newValue.matches("\\d*"))
                this.setText(newValue.replaceAll("[^\\d]", ""));
        });

        this.setOnKeyPressed(this::moveFocusVerify);

    }

    private void moveFocusVerify(KeyEvent e){
        HBox verifyFields = (HBox) this.getParent();

        int pos = verifyFields.getChildren().indexOf(this);
        List<Node> childs = verifyFields.getChildren();

        if(pos < childs.size() - 1  && !this.getText().isBlank()){
            childs.get(pos + 1).requestFocus();
        }else if(pos != 0 && this.getText().isBlank()){
            if(pos != childs.size() - 1)
                ((TextField) childs.get(pos - 1)).setText("");

            childs.get(pos - 1).requestFocus();
        }
    }
}
