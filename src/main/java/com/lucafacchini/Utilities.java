package com.lucafacchini;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Utilities {
    // Rescale image to new width and height
    public BufferedImage rescaleImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImage;
    }
}