package com.example.keyvault_client.Controllers;
import com.example.keyvault_client.ViewManager;
import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class Cache {
    private static Map<String, Image> imageCache = new HashMap<>();

    public Cache()
    {

    }

    public static Image loadImage(String name)
    {
        Image image = imageCache.get(name);

        if(image != null)
            return image;

        image = new Image(ViewManager.class.getResourceAsStream(name));
        imageCache.put(name, image);

        return image;
    }
}

