package com.lucafacchini;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeyHandler implements KeyListener {

    public boolean isLeftPressed = false;
    public boolean isRightPressed = false;
    public boolean isUpPressed = false;
    public boolean isDownPressed = false;
    private final GamePanel gp;

    // Minimum time in milliseconds before toggling the pause state
//    private static final long TIME_BEFORE_NEXT_PAUSE_SCREEN = 500; // 500ms
//    private long lastPauseTime = 0;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Handle movement keys only if the game is not paused or in dialogue state
        if (gp.gameStatus != GamePanel.GameStatus.PAUSED && GamePanel.GameStatus.RUNNING != GamePanel.GameStatus.DIALOGUE) {
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                isLeftPressed = true;
            }
            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                isRightPressed = true;
            }
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                isUpPressed = true;
            }
            if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                isDownPressed = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        // Debugging logs
//        System.out.println("Key released: " + KeyEvent.getKeyText(key));
//        System.out.println("Current game status: " + gp.gameStatus);

        if (gp.gameStatus == GamePanel.GameStatus.RUNNING) {
            // Handle movement keys
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) isLeftPressed = false;
            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) isRightPressed = false;
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) isUpPressed = false;
            if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) isDownPressed = false;

            // Handle pause toggle
            handlePauseToggle(key);

        } else if (gp.gameStatus == GamePanel.GameStatus.DIALOGUE) {
            if (key == KeyEvent.VK_ENTER) {
                System.out.println("Dialogue state: Enter key pressed, exiting dialogue");
                gp.gameStatus = GamePanel.GameStatus.RUNNING; // Attempting state transition
                System.out.println("Game status changed to: " + gp.gameStatus);
            }
        } else if (gp.gameStatus == GamePanel.GameStatus.PAUSED) {
            // Handle paused state actions
            System.out.println("Paused state: Key released");
        }
    }

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
}
