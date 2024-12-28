package com.lucafacchini;

import com.lucafacchini.objects.Key_Object;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * Manages the user interface of the game.
 *
 * This class handles drawing the UI elements on the screen.
 * @note: This is still experimental code, it has to be tested.
 */
public class UI {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(UI.class.getName());

    // Fonts
    Font arial_30 = new Font("Arial", Font.PLAIN, 30);
    Font arial_50 = new Font("Arial", Font.PLAIN, 50);

    // Dialogues
    public boolean isDrawing = false;
    public String currentDialogue = "";
    public boolean messageOn = false;
    public String message = "";

    int messageCounter = 0;

    // GamePanel instance
    GamePanel gp;

    // Graphics2D object
    Graphics2D g2d;

    public boolean gameFinished = false;

    double playTime = 0;
    DecimalFormat df = new DecimalFormat("#0.00");

    BufferedImage keyImage;

    /**
     * Constructor of the UI class.
     *
     * @param gp the GamePanel instance.
     */
    public UI(GamePanel gp) {
        this.gp = gp;
        Key_Object key_object = new Key_Object(gp, new Utilities());
        keyImage = key_object.image;
    }

    /**
     * Displays a message on the screen.
     *
     * @param text the message to be displayed.
     */
    public void showMessage(String text) {
    }

    /**
     * Draws the UI elements on the screen.
     *
     * @param g2d the Graphics2D object used to draw the elements.
     */
    public void draw(Graphics2D g2d) {
        this.g2d = g2d;

        g2d.setFont(arial_30);
        g2d.setColor(Color.WHITE);

        if(gp.gameStatus == GamePanel.GameStatus.RUNNING) {
            // Do stuff
        }
        if(gp.gameStatus == GamePanel.GameStatus.DIALOGUE) {
            // Do stuff
        }
        if(gp.gameStatus == GamePanel.GameStatus.PAUSED) {
            isDrawing = true;
            drawDialogueScreen();
        }
    }

    /**
     * Draws the dialogue screen.
     */
    public void drawDialogueScreen() {
        int x, y, width, height;

        x = gp.TILE_SIZE * 2;
        y = gp.TILE_SIZE;
        width = gp.WINDOW_WIDTH - gp.TILE_SIZE * 4;
        height = gp.TILE_SIZE * 4;

        drawSubWindow(x, y, width, height);

        x += gp.TILE_SIZE;
        y += gp.TILE_SIZE;
        g2d.drawString(currentDialogue, x, y);
    }


    /**
     * Draws a sub window on the screen.
     *
     * @param x the x coordinate of the window.
     * @param y the y coordinate of the window.
     * @param width the width of the window.
     * @param height the height of the window.
     */
    public void drawSubWindow(int x, int y, int width, int height) {
        Color color = new Color(0, 0,0, 200);
        g2d.setColor(color);
        g2d.fillRoundRect(x, y, width, height, 50, 50);

        color = new Color(255, 255, 255);
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(color);
        g2d.drawRoundRect(x+5, y+5, width-10, height-10, 50, 50);

    }
}