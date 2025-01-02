package com.lucafacchini;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * Handles key events for the game.
 *
 * This class implements the KeyListener interface to manage keyboard input.
 * It updates the state of key presses and handles game status changes.
 */
public class KeyHandler implements KeyListener {

    public boolean isLeftPressed = false;
    public boolean isRightPressed = false;
    public boolean isUpPressed = false;
    public boolean isDownPressed = false;

    public boolean isEnterPressed = false;
    public boolean enterPressedHandled = false;

    private final GamePanel gp;

    /**
     * @brief Constructor of the KeyHandler class.
     * Initializes the key handler with the given GamePanel.
     *
     * @param gp the GamePanel object to interact with.
     */
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    /**
     * @brief Invoked when a key has been pressed.
     * Updates the state of key presses for movement keys.
     *
     * @param e the event to be processed.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Handle movement keys only if the game is not paused or in dialogue state
        if (gp.gameStatus == GamePanel.GameStatus.RUNNING || gp.gameStatus == GamePanel.GameStatus.DIALOGUE) {
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) { isLeftPressed = true; }
            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) { isRightPressed = true; }
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) { isUpPressed = true; }
            if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) { isDownPressed = true; }
        }

        else if(gp.gameStatus == GamePanel.GameStatus.TITLE_SCREEN) {


            if(gp.ui.currentTitleScreenWindow == 1) {
                if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
                    if(gp.ui.titleScreenOption == 0) {
                        gp.ui.titleScreenOption = 2;
                    } else {
                        gp.ui.titleScreenOption--;
                    }
                } else if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
                    if(gp.ui.titleScreenOption == 2) {
                        gp.ui.titleScreenOption = 0;
                    } else {
                        gp.ui.titleScreenOption++;
                    }
                }
            }



            if(key == KeyEvent.VK_ENTER) {
                if (gp.ui.titleScreenOption == 0) {
                    switch(gp.ui.currentTitleScreenWindow) {
                        case 1 -> gp.ui.currentTitleScreenWindow = 2;
                        case 2 -> gp.gameStatus = GamePanel.GameStatus.RUNNING;
                    }
                }

                else if (gp.ui.titleScreenOption == 1) {
                    // stuff
                }

                else if (gp.ui.titleScreenOption == 2) {
                    System.exit(0);
                }
            }
        }
    }

    /**
     * @brief Invoked when a key has been released.
     * Updates the state of key releases and handles game status changes.
     *
     * @param e the event to be processed.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (gp.gameStatus != GamePanel.GameStatus.PAUSED) {
            // Handle movement keys
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) isLeftPressed = false;
            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) isRightPressed = false;
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) isUpPressed = false;
            if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) isDownPressed = false;

            if(key == KeyEvent.VK_ENTER) {
                isEnterPressed = true;
                enterPressedHandled = false;
            }

            // Handle pause toggle
            handlePauseToggle(key);
        }
    }

    /**
     * @brief Toggles the pause state of the game.
     * This method is called when the pause key is released.
     *
     * @param key the key code of the released key.
     */
    private void handlePauseToggle(int key) {
        if (key == KeyEvent.VK_T) {
            if (gp.gameStatus == GamePanel.GameStatus.RUNNING) {
                System.out.println("PAUSE");
                gp.gameStatus = GamePanel.GameStatus.PAUSED;
            } else if (gp.gameStatus == GamePanel.GameStatus.PAUSED) {
                System.out.println("UNPAUSE");
                gp.gameStatus = GamePanel.GameStatus.RUNNING;
            }
        }
    }

    public void updateKeyStates() {
        if(isEnterPressed && !enterPressedHandled) {
            enterPressedHandled = true;
        } else {
            isEnterPressed = false;
        }
    }
}