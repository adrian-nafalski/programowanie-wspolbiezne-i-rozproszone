package com.example.client.utils;

import javafx.scene.image.Image;

public class ImageUtil {
    public static Image loadImage(String path) {
        try {
            return new Image(ImageUtil.class.getResourceAsStream(path));
        } catch (Exception e) {
            System.out.println("Nie znaleziono zdjęcia: " + path);
            return null;
        }
    }
}
