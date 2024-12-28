package com.lucafacchini;

import javax.swing.*;


/**
 * Main class to initialize and start the game.
 *
 * This class sets up the main game window and starts the game loop.
 */
public class Main {

    /**
     * @brief Main method to start the game.
     * Initializes the game window and starts the game loop.
     *
     * @param args command line arguments (not used).
     */
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack(); // Resize the window to fit the GamePanel

        window.setLocationRelativeTo(null); // Center the window on the screen
        window.setVisible(true); // Make the window visible

        gamePanel.initializeGame();
        gamePanel.startGameThread();
    }
}