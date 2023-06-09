package com.example.keyvault_client;

import com.example.keyvault_client.Controllers.Cache;
import com.keyvault.database.models.Devices;
import com.keyvault.database.models.Items;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class NodeGenerator {

    public static Button generateActionButton(String icon, String text, String... classText){
        String dark = ViewManager.isDark ? "dark" : "";
        ImageView iconView = new ImageView(Cache.loadImage(icon));
        iconView.setFitHeight(22);
        iconView.setFitWidth(22);

        Button button = new Button();
        button.setGraphic(iconView);
        button.getStyleClass().addAll(classText);
        button.getStyleClass().add(dark);

        if(text != null) button.setText(text);

        return button;
    }

    public static HBox generateItemCard(Items item)
    {
        String dark = ViewManager.isDark ? "dark" : "";
        Label title = new Label(item.getName());
        Label account = new Label(item.getPasswordsByIdI() != null ? item.getPasswordsByIdI().getEmailP() : "Nota segura");

        title.getStyleClass().addAll("text16", "medium", dark);
        account.getStyleClass().addAll("text14", dark);

        VBox textContainer = new VBox(title, account);
        SVGPath icon = new SVGPath();
        HBox border = new HBox();

        textContainer.getStyleClass().addAll("itemTextContainer", dark);
        if(item.getPasswordsByIdI() != null)
        {
            icon.setContent("M80.573 123L80.1 98.915C79.513 69.071 91.715 45.5 120 45.5l13.944.014c30.548.588 41.343 23.594 41.93 53.447l.473 24.039h7.646c4.416 0 8.007 3.586 8.007 8.01v84.98c0 4.43-3.585 8.01-8.007 8.01H72.007c-4.416 0-8.007-3.586-8.007-8.01v-84.98c0-4.43 3.585-8.01 8.007-8.01h8.566zm16.39 0h62.657c.033-5.473.104-12.555-.05-20.33l-.078-4.024c-.414-21.015-2.663-34.678-25.194-35.11l-12.405-.358c-28.124-.56-25.578 26.864-25.397 36.052l.08 4.024.387 19.746zM176 143.316c0-2.384-1.547-4.316-3.46-4.316H83.46c-1.911 0-3.46 1.936-3.46 4.316v60.368c0 2.384 1.547 4.316 3.46 4.316h89.08c1.911 0 3.46-1.936 3.46-4.316v-60.368z");
            icon.setScaleY(0.2);
            icon.setScaleX(0.2);
        }
        else
        {
            icon.setContent("M13.52 5.72h-7.4c-0.36 0-0.56 0.2-0.6 0.24l-5.28 5.28c-0.040 0.040-0.24 0.24-0.24 0.56v12.2c0 1.24 1 2.24 2.24 2.24h11.24c1.24 0 2.24-1 2.24-2.24v-16.040c0.040-1.24-0.96-2.24-2.2-2.24zM5.28 8.56v1.8c0 0.32-0.24 0.56-0.56 0.56h-1.84l2.4-2.36zM14.080 24.040c0 0.32-0.28 0.56-0.56 0.56h-11.28c-0.32 0-0.56-0.28-0.56-0.56v-11.36h3.040c1.24 0 2.24-1 2.24-2.24v-3.040h6.52c0.32 0 0.56 0.24 0.56 0.56l0.040 16.080z");
            icon.setScaleY(1.5);
            icon.setScaleX(1.5);
        }

        icon.setFill(Color.hsb(0, 0, 0.95));
        border.getStyleClass().addAll("itemContainerBorder", dark);

        HBox iconBox = new HBox(icon);
        iconBox.getStyleClass().add("itemIconBox");

        iconBox.setStyle("-fx-background-color:" + item.getColor());

        HBox container = new HBox(iconBox, textContainer);
        container.getStyleClass().addAll("itemContainer", dark);

        return container;
    }

    public static HBox generateDeviceCard(Devices device)
    {
        String dark = ViewManager.isDark ? "Light" : "";
        String agent = device.getAgent().toLowerCase();
        String os = "device";

        if (agent.contains("windows"))
            os = "windows";
        else if (agent.contains("linux"))
            os = "linux";
        else if (agent.contains("mac"))
            os = "mac";

        ImageView iconView = new ImageView(Cache.loadImage("icons/"+ os + dark +".png"));

        iconView.setFitHeight(32);
        iconView.setFitWidth(32);

        Label deviceType = new Label(device.getAgent() + " - " + device.getLocation());
        deviceType.getStyleClass().addAll("text16", "medium");

        Label deviceDate = new Label(device.getLastLogin().toString());
        deviceDate.getStyleClass().add("text12");

        VBox textContainer = new VBox(deviceType, deviceDate);

        HBox container = new HBox(iconView, textContainer);
        container.getStyleClass().addAll("whiteFieldContainer", "deviceCard");

        return container;
    }
}
