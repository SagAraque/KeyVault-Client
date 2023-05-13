package com.example.keyvault_client.nodes;

import javafx.scene.control.PasswordField;
import javafx.scene.control.skin.TextFieldSkin;

public class PasswordFieldSkin extends PasswordField {

    private boolean isPassVisible = false;
    public PasswordFieldSkin()
    {
        super();

        this.setSkin(new TextFieldSkin(this){
            @Override
            protected String maskText(String text){
                if(isPassVisible)
                    return text;

                return super.maskText(text);
            }
        });
    }

    public void changeVisibility()
    {
        String password = this.getText();
        this.isPassVisible = !this.isPassVisible;
        this.setText(null);
        this.setText(password);
    }

    public boolean isPassVisible()
    {
        return this.isPassVisible;
    }
}
