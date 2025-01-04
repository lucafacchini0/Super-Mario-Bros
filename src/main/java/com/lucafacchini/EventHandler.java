package com.lucafacchini;

import com.lucafacchini.entity.Entity;

import java.awt.*;

/**
 * This class handles the events that occur in the game.
 * It can handle events such as collisions, triggers, teleports,
 * healing... basically anything that can happen in the game.
 */
public class EventHandler {

    // GamePanel reference
    GamePanel gp;

    // Default values for the event trigger bounding box
    Rectangle triggerBox;
    int triggerBoxDefaultX, triggerBoxDefaultY;

    public EventHandler(GamePanel gp) {
        this.gp = gp;

        setBoundingBox();
    }

    private void setBoundingBox() {
        triggerBox = new Rectangle();

        triggerBox.x = gp.TILE_SIZE / 2 - 1;
        triggerBox.y = gp.TILE_SIZE / 2 - 1;
        triggerBox.width = 3;
        triggerBox.height = 3;
        triggerBoxDefaultX = triggerBox.x;
        triggerBoxDefaultY = triggerBox.y;
    }

    public void checkEvent() {
        // @DEBUG
        System.out.println("x:" + gp.player.worldX / gp.TILE_SIZE + " y:" + gp.player.worldY / gp.TILE_SIZE);

        if(hit(16, 31)) {
            gp.player.hp.setMaxHP(50);
            gp.player.hp.setHP(50);
        }

        if(hit(18, 31)) {
            gp.player.hp.removeHP(1);
        }
    }

    private boolean hit(int columnX, int rowY) {

        boolean hit = false;

        // TODO: This is unsafe and, imo, inefficient. It's better to use a separate variable to store the player's bounding box. This way we avoid re-assinging the player's bounding box every time we check for a collision.
        gp.player.boundingBox.x = gp.player.worldX + gp.player.boundingBox.x;
        gp.player.boundingBox.y = gp.player.worldY + gp.player.boundingBox.y;
        triggerBox.x = columnX * gp.TILE_SIZE + triggerBox.x;
        triggerBox.y = rowY * gp.TILE_SIZE + triggerBox.y;

        if (gp.player.boundingBox.intersects(triggerBox)) {
            hit = true;
        }

        gp.player.boundingBox.x = gp.player.boundingBoxDefaultX;
        gp.player.boundingBox.y = gp.player.boundingBoxDefaultY;
        triggerBox.x = triggerBoxDefaultX;
        triggerBox.y = triggerBoxDefaultY;
        return hit;
    }
}
