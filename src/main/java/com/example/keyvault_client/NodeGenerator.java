package com.example.keyvault_client;

import com.keyvault.entities.Devices;
import com.keyvault.entities.Items;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

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

    public static HBox generateItemCard(Items item){
        Label title = new Label(item.getName());
        Label account = new Label(item.getPasswordsByIdI() != null ? item.getPasswordsByIdI().getEmailP() : "Nota segura");

        title.getStyleClass().addAll("text16", "medium");
        account.getStyleClass().add("text14");

        VBox textContainer = new VBox(title, account);
        SVGPath icon = new SVGPath();
        HBox border = new HBox();

        textContainer.getStyleClass().add("itemTextContainer");
        icon.setContent("M18.102 12.129c0-0 0-0 0-0.001 0-1.564 1.268-2.831 2.831-2.831s2.831 1.268 2.831 2.831c0 1.564-1.267 2.831-2.831 2.831-0 0-0 0-0.001 0h0c-0 0-0 0-0.001 0-1.563 0-2.83-1.267-2.83-2.83 0-0 0-0 0-0.001v0zM24.691 12.135c0-2.081-1.687-3.768-3.768-3.768s-3.768 1.687-3.768 3.768c0 2.081 1.687 3.768 3.768 3.768v0c2.080-0.003 3.765-1.688 3.768-3.767v-0zM10.427 23.76l-1.841-0.762c0.524 1.078 1.611 1.808 2.868 1.808 1.317 0 2.448-0.801 2.93-1.943l0.008-0.021c0.155-0.362 0.246-0.784 0.246-1.226 0-1.757-1.424-3.181-3.181-3.181-0.405 0-0.792 0.076-1.148 0.213l0.022-0.007 1.903 0.787c0.852 0.364 1.439 1.196 1.439 2.164 0 1.296-1.051 2.347-2.347 2.347-0.324 0-0.632-0.066-0.913-0.184l0.015 0.006zM15.974 1.004c-7.857 0.001-14.301 6.046-14.938 13.738l-0.004 0.054 8.038 3.322c0.668-0.462 1.495-0.737 2.387-0.737 0.001 0 0.002 0 0.002 0h-0c0.079 0 0.156 0.005 0.235 0.008l3.575-5.176v-0.074c0.003-3.12 2.533-5.648 5.653-5.648 3.122 0 5.653 2.531 5.653 5.653s-2.531 5.653-5.653 5.653h-0.131l-5.094 3.638c0 0.065 0.005 0.131 0.005 0.199 0 0.001 0 0.002 0 0.003 0 2.342-1.899 4.241-4.241 4.241-2.047 0-3.756-1.451-4.153-3.38l-0.005-0.027-5.755-2.383c1.841 6.345 7.601 10.905 14.425 10.905 8.281 0 14.994-6.713 14.994-14.994s-6.713-14.994-14.994-14.994c-0 0-0.001 0-0.001 0h0z");
        icon.setScaleY(1);
        icon.setScaleX(1);
        border.getStyleClass().add("itemContainerBorder");

        HBox container = new HBox(border, icon, textContainer);
        container.getStyleClass().add("itemContainer");

        return container;
    }

    public static HBox generateDeviceCard(Devices device)
    {
        ImageView iconView = new ImageView(new Image(NodeGenerator.class.getResourceAsStream("icons/windows.png")));
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
