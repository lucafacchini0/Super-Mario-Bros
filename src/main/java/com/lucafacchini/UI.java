package com.lucafacchini;

import com.lucafacchini.entity.Entity;

import java.awt.*;
import java.io.InputStream;
import java.util.Objects;
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
    Font defaultFont;

    // Title screen
    public int titleScreenOption = 0;
    public int currentTitleScreenWindow = 1;

    // Dialogues
    public String currentDialogue = null;
    public String dialogueToPrint = null;
    public int currentLetter = 1;
    public boolean hasFinishedPrintingDialogue = true;

    // GamePanel instance
    GamePanel gp;

    // Graphics2D object
    Graphics2D g2d;

    // Colors
    Color dialogueWindowBackground = new Color(0, 0, 0, 210);
    Color dialogueWindowStroke = new Color(255, 255, 255);


    /**
     * Constructor of the UI class.
     *
     * @param gp the GamePanel instance.
     */
    public UI(GamePanel gp) {
        this.gp = gp;

        loadFonts();
    }






// ********************************************** DRAW METHODS ********************************************** //


    /**
     * @brief Draws the UI elements on the screen.
     * This is called every frame.
     *
     * @param g2d the Graphics2D object used to draw the elements.
     */
    public void draw(Graphics2D g2d) {
        this.g2d = g2d;

        switch (gp.gameStatus) {

            case TITLE_SCREEN -> {
                drawTitleScreen();
            }

            case RUNNING -> {
                drawEntityRelatedStuff();
                drawStatsBar();
            }

            case DIALOGUE -> {
                drawDialogues();
            }

            case PAUSED -> {
                drawStatsBar();
            }
        }
    }






    // ********** TITLE_SCREEN STATE ********** //


    /**
     * @brief Method to draw the title screen.
     *
     * It starts by checking which window to draw.
     * Then it draws the title screen window.
     */
    private void drawTitleScreen() {
        switch (currentTitleScreenWindow) {
            case 1 -> drawTitleScreenWindow1();
            case 2 -> drawTitleScreenWindow2();
        }
    }


    /**
     * @brief Draws the first window of the title screen.
     *
     * It starts by setting the font size and style.
     * Then it draws the title and the options.
     */
    private void drawTitleScreenWindow1() {
        setFont(defaultFont, 94F, Font.BOLD);
        drawShadowText("FacchiniRPG", Color.YELLOW, Color.DARK_GRAY, getCenteredX("FacchiniRPG"), gp.TILE_SIZE * 2, 3, 3);
        setFont(defaultFont, 32F, Font.BOLD);
        drawShadowText("Game by Luca Facchini", Color.WHITE, Color.DARK_GRAY, getCenteredX("Game by Luca Facchini"), gp.TILE_SIZE * 3, 3, 3);

        // Options
        String[] options = {"PLAY GAME", "LOAD FILE", "EXIT"};

        for (int i = 0; i < options.length; i++) {
            int y = gp.TILE_SIZE * (8 + i);
            setFont(defaultFont, 48F, Font.BOLD);
            drawTitleScreenOption(options[i], y, i == titleScreenOption);
        }
    }


    /**
     * @brief Draws the second window of the title screen.
     *
     * It starts by setting the font size and style.
     * Then it the window (not implemented yet).
     */
    private void drawTitleScreenWindow2() {
        setFont(defaultFont, 24F, Font.PLAIN);
        drawText("Press enter to start the game", Color.WHITE, true, gp.WINDOW_HEIGHT / 2);
    }


    /**
     * @brief Draws the title screen options.
     *
     * It starts by drawing the text and the ">" symbol if the option is selected.
     *
     * @param text the text to draw.
     * @param y the y coordinate of the text.
     * @param isSelected if the option is selected.
     */
    private void drawTitleScreenOption(String text, int y, boolean isSelected) {
        drawText(text, Color.WHITE, true, y);

        if (isSelected) {
            drawText(">", Color.YELLOW, getCenteredX(text) - 50, y);
        }
    }






    // ********** RUNNING STATE ********** //


    /**
     * @brief Draws a bulb above the entity.
     *
     * @param entity the entity to draw the bulb above.
     */
    private void drawBulb(Entity entity) {
        int x, y, width, height;

        width = gp.TILE_SIZE / 4;
        height = gp.TILE_SIZE / 4;
        x = worldToScreenX(entity.worldX) + gp.TILE_SIZE / 2 - width / 2;
        y = worldToScreenY(entity.worldY) - gp.TILE_SIZE / 2;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, width, height);
    }





    // ********** DIALOGUE STATE ********** //



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
        g2d.setColor(dialogueWindowBackground);
        g2d.fillRoundRect(x, y, width, height, 50, 50);

        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(dialogueWindowStroke);
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
        setFont(defaultFont, 30F, Font.PLAIN);

        x += gp.TILE_SIZE;
        y += gp.TILE_SIZE;

        if(isPlayerReadyForNextDialogue()) {
            hasFinishedPrintingDialogue = false;
            currentLetter = 1;
        }

        if (currentLetter > dialogue.length()) {
            hasFinishedPrintingDialogue = true;
            currentLetter = dialogue.length();
        }

        dialogueToPrint = dialogue.substring(0, currentLetter);

        drawShadowText(dialogueToPrint, Color.WHITE, Color.BLACK, x, y, 2, 2);

        currentLetter++;
    }







    // ********** GENERAL METHODS (ANY GAME STATE) ********** //


    /**
     * @brief Shows a message on the screen.
     *
     * @note: This method is still experimental.
     *
     * @param text the text to show on the screen.
     */
    public void showMessage(String text) {
        setFont(defaultFont, 30F, Font.PLAIN);
        drawText(text, Color.WHITE, true, gp.TILE_SIZE);
    }


    public void drawStatsBar() {
        int x, y, width, height;

        x = gp.TILE_SIZE;
        y = gp.TILE_SIZE;
        width = gp.TILE_SIZE / 4 * gp.player.hp.maxHP;
        height = gp.TILE_SIZE / 4;

        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, width, height);

        width = gp.TILE_SIZE / 4 * gp.player.hp.currentHP;

        g2d.setColor(Color.GREEN);
        g2d.fillRect(x, y, width, height);

        String text = "HP: " + gp.player.hp.currentHP + "/" + gp.player.hp.maxHP;
        setFont(defaultFont, 16F, Font.PLAIN);
        drawText(text, Color.BLACK, x + 10, y + gp.TILE_SIZE / 4 - (height / 6));
    }




// ********************************************** HELPER METHODS ********************************************** //


    private void loadFonts() {
        try {
            InputStream is = Objects.requireNonNull(getClass().getResourceAsStream("/fonts/Pixel-Life.ttf"), "Font resource not found");
            defaultFont = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            LOGGER.severe("Error loading font: " + e.getMessage());
        }
    }

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

    private boolean isPlayerReadyForNextDialogue() {
        return gp.player.isReadyForNextDialogue;
    }

    private int getCenteredX(String text) {
        int length = (int)g2d.getFontMetrics().getStringBounds(text, g2d).getWidth();

        return (gp.WINDOW_WIDTH - length) / 2;
    }



    // Draw helper methods
    private void setFont(Font font, float size, int style) {
        g2d.setFont(font.deriveFont(style, size));
    }


    private void drawText(String text, Color color, int x, int y) {
        g2d.setColor(color);
        g2d.drawString(text, x, y);
    }

    private void drawShadowText(String text, Color textColor, Color shadowColor, int x, int y, int deltaX, int deltaY) {

        g2d.setColor(shadowColor);
        g2d.drawString(text, x + deltaX, y + deltaY);
        g2d.setColor(textColor);
        g2d.drawString(text, x, y);
    }

    private void drawText(String text, Color color, boolean center, int y) {
        if(!center) {
            Logger.getLogger(UI.class.getName()).severe("Center is false, use the other method");
            return;
        }

        g2d.setColor(color);
        g2d.drawString(text, getCenteredX(text), y);
    }

    private void drawShadowText(String text, Color textColor, Color shadowColor, boolean center, int y, int deltaX, int deltaY) {
        if(!center) {
            Logger.getLogger(UI.class.getName()).severe("Center is false, use the other method");
            return;
        }

        g2d.setColor(shadowColor);
        g2d.drawString(text, getCenteredX(text) + deltaX, y + deltaY);
        g2d.setColor(textColor);
        g2d.drawString(text, getCenteredX(text), y);
    }
}