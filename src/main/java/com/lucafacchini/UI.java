package com.lucafacchini;

import com.lucafacchini.entity.Entity;

import java.awt.*;
import java.util.logging.Logger;

/**
 * @brief Manages the user interface of the game.
 *
 * This class handles drawing the UI elements on the screen.
 * @note: This is still experimental code, it has to be tested.
 */
public class UI {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(UI.class.getName());
    public boolean gameFinished = false;

    // Fonts
    Font arial_30 = new Font("Arial", Font.PLAIN, 30);

    // Dialogues
    public String currentDialogue = null;

    // GamePanel instance
    GamePanel gp;

    // Graphics2D object
    Graphics2D g2d;

//    // KeyHandler @NOTE This is not used yet. I am not sure if it will be used.
//    KeyHandler kh;

    /**
     * Constructor of the UI class.
     *
     * @param gp the GamePanel instance.
     * @param kh the KeyHandler instance.
     */
    public UI(GamePanel gp, KeyHandler kh) {
        this.gp = gp;
//        this.kh = kh;
    }






    // ********** DRAW METHODS ********** //


    /**
     * @brief Shows a message on the screen.
     *
     * @note: This method is still experimental.
     *
     * @param text the text to show on the screen.
     */
    public void showMessage(String text) {
        g2d.setFont(arial_30);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, 100, 100);
    }


    /**
     * @brief Draws the UI elements on the screen.
     *
     * @param g2d the Graphics2D object used to draw the elements.
     */
    public void draw(Graphics2D g2d) {
        this.g2d = g2d;

        g2d.setFont(arial_30);
        g2d.setColor(Color.WHITE);

        if(gp.gameStatus == GamePanel.GameStatus.RUNNING) {
            drawEntityRelatedStuff();
        }

        if(gp.gameStatus == GamePanel.GameStatus.DIALOGUE) {
            drawDialogues();
        }

        if(gp.gameStatus == GamePanel.GameStatus.PAUSED) {
            // Do stuff
        }
    }


    /**
     * @brief Draws the dialogue screen.
     *
     * It starts by calculating the x, y, width, and height of the dialogue screen.
     * Then it draws the sub window and the dialogue text.
     */
    private void drawDialogueScreen() {
        int x, y, width, height;

        x = gp.TILE_SIZE * 2;
        y = gp.TILE_SIZE;
        width = gp.WINDOW_WIDTH - gp.TILE_SIZE * 4;
        height = gp.TILE_SIZE * 4;

        drawDialogueWindow(x, y, width, height);
        drawDialogueString(x, y, currentDialogue);
    }


    /**
     * @brief Draws a sub window on the screen. (the background)
     *
     * @param x the x coordinate of the window.
     * @param y the y coordinate of the window.
     * @param width the width of the window.
     * @param height the height of the window.
     *
     */
    private void drawDialogueWindow(int x, int y, int width, int height) {
        Color color = new Color(0, 0,0, 150);
        g2d.setColor(color);
        g2d.fillRoundRect(x, y, width, height, 50, 50);

        color = new Color(255, 255, 255);
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(color);
        g2d.drawRoundRect(x+5, y+5, width-10, height-10, 50, 50);
    }


    /**
     * @brief Draws a dialogue string on the screen.
     *
     * It starts by setting the font size and style.
     * Then it draws the dialogue string on the screen.
     *
     * @param x the x coordinate of the string.
     * @param y the y coordinate of the string.
     * @param dialogue the string to draw.
     */
    private void drawDialogueString(int x, int y, String dialogue) {
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 32F));
        x += gp.TILE_SIZE;
        y += gp.TILE_SIZE;

        g2d.drawString(dialogue, x, y);
    }


    /**
     * @brief Draws a bulb above the entity.
     *
     * @param entity the entity to draw the bulb above.
     */
    public void drawBulb(Entity entity) {
        int x = worldToScreenX(entity.worldX);
        int y = worldToScreenY(entity.worldY) - gp.TILE_SIZE / 2;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, gp.TILE_SIZE / 4, gp.TILE_SIZE / 4);
    }







    // ********** HELPER METHODS ********** //


    private void drawEntityRelatedStuff() {
        for(Entity entity : gp.npcArray) {
            if(entity != null && entity.isNextToPlayer) {
                drawBulb(entity);
            }
        }
    }

    private void drawDialogues() {
        drawDialogueScreen();
    }

    private int worldToScreenX(int worldX) {
        return worldX - gp.player.worldX + gp.player.screenX;
    }

    private int worldToScreenY(int worldY) {
        return worldY - gp.player.worldY + gp.player.screenY;
    }
}