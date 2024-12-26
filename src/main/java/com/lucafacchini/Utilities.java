package com.lucafacchini;

import java.awt.*;
import java.awt.image.BufferedImage;


public class Utilities {

    // TODO: useless class. can be replaced with LOGGER.

    public Utilities() {
    }

    public void printErrorMessage(String className, String message) {
        System.out.println("\033[31m[" + className + "] " + message + "\033[0m");
    }

    public void printLoadingImagesError(String className, String message, String folderPath, String exception) {
        System.out.println("\033[31m[" + className + "] " + message + " " + folderPath + "[" + exception + "]" + "\033[0m");
    }

    public void printSuccess(String className, String message) {
        System.out.println("\033[32m[" + className + "] " + message + "\033[0m");
    }

    public void printLoadingImagesSuccess(String className, String message, String folderPath) {
        System.out.println("\033[32m[" + className + "] " + message + " " + folderPath + "\033[0m");
    }

    public void drawEntitySuccess(String className, String message) {
        System.out.println("\033[32m[" + className + "] " + message + "\033[0m");
    }


    // Performance utilities
    // Rescale image to new width and height
    public BufferedImage rescaleImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImage;
    }

    public void printLoadingStatus(String className, String message) {
        System.out.println("[" + className + "] " + message);
    }

    public void printLoadingStatus(boolean success, String className, String message, String folderPath) {
        // if success, green with java class
        if (success) {
            System.out.println(Color.GREEN + "["+  className + "]" + message + " " + folderPath + "\033[0m");
        } else {
            System.out.println(Color.RED + "[" + className + "]" + message + " " + folderPath + "\033[0m");
        }

    }
}
