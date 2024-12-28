package com.lucafacchini.objects;

import com.lucafacchini.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

/**
 * SuperObject class represents the superclass of all objects in the game.
 * It contains the properties and methods that are common to all objects.
 */
public class SuperObject {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(SuperObject.class.getName());

    // SuperObject properties
    public String name;

    public int worldX, worldY; // The actual position of the object in the world.
    public int screenX, screenY; // The position of the object on the screen. It is calculated based on the player's position.

    public boolean isSolid = false; // If the object is solid, the player cannot walk through it.

    // TODO: Replace size with gp.TILE_SIZE. Must use a constructor to pass gp.TILE_SIZE.
    public Rectangle boundingBox = new Rectangle(0, 0, 64, 64);
    public int boundingBoxDefaultX = 0;
    public int boundingBoxDefaultY = 0;

    public BufferedImage image; // The image of the object.


    /**
     * @brief This method is used to draw the object on the screen.
     * It calculates the screenX and screenY based on the player's position.
     * It also checks if the object is within the screen boundaries before drawing it. (Optimization)
     *
     * @param g2d The Graphics2D object.
     * @param gp The GamePanel instance.
     */
    public void draw(Graphics2D g2d, GamePanel gp) {
        screenX = worldX - gp.player.worldX + gp.player.screenX;
        screenY = worldY - gp.player.worldY + gp.player.screenY;

        // If the object is within the screen boundaries, draw it.
        if (worldX + gp.TILE_SIZE > gp.player.worldX - gp.player.screenX &&
                worldX - gp.TILE_SIZE < gp.player.worldX + gp.player.screenX &&
                worldY + gp.TILE_SIZE > gp.player.worldY - gp.player.screenY &&
                worldY - gp.TILE_SIZE < gp.player.worldY + gp.player.screenY) {
            g2d.drawImage(image, screenX, screenY, null);
        }

        //Debug ##IMPORTANT
        g2d.setColor(Color.BLACK);
        g2d.drawRect(screenX + boundingBox.x, screenY + boundingBox.y, boundingBox.width, boundingBox.height);
    }
}