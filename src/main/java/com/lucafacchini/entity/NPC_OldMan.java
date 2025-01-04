package com.lucafacchini.entity;

import com.lucafacchini.GamePanel;
import com.lucafacchini.stats.Speed;

import java.util.logging.Logger;

/**
 * Represents an NPC entity in the game.
 */
public class NPC_OldMan extends Entity {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(NPC_OldMan.class.getName());

    // Sprite settings
    public final int NUM_MOVING_SPRITES = 2;
    public final int NUM_IDLING_SPRITES = 2;
    public final int SPRITE_HEIGHT_PX = 16;
    public final int SPRITE_WIDTH_PX = 16;
    public final int RESCALED_SPRITE_HEIGHT_PX;
    public final int RESCALED_SPRITE_WIDTH_PX;
    public final int MOVING_SPRITE_UPDATE_TIME = 15;

    // Stats Settings
    public final int DEFAULT_SPEED = 1;

    /**
     * @brief Constructor for the NPC_OldMan class.
     * @param gp The GamePanel instance.
     */
    public NPC_OldMan(GamePanel gp) {
        super(gp);


        // Initialize bounding box dimensions and default values
        boundingBox.x = 0;
        boundingBox.y = 0;
        boundingBox.width = gp.TILE_SIZE;
        boundingBox.height = gp.TILE_SIZE;
        boundingBoxDefaultX = boundingBox.x;
        boundingBoxDefaultY = boundingBox.y;
        boundingBoxDefaultHeight = boundingBox.height;
        boundingBoxDefaultWidth = boundingBox.width;

        // Calculate rescaled sprite dimensions based on game scale
        RESCALED_SPRITE_HEIGHT_PX = SPRITE_HEIGHT_PX * gp.SCALE;
        RESCALED_SPRITE_WIDTH_PX = SPRITE_WIDTH_PX * gp.SCALE;

        // Load and rescale player sprites
        loadSprites("npc/old_man", NUM_MOVING_SPRITES, NUM_IDLING_SPRITES);
        rescaleSprites(RESCALED_SPRITE_WIDTH_PX, RESCALED_SPRITE_HEIGHT_PX);


        // Stats
        speed = new Speed(DEFAULT_SPEED);


        // Set default values
        setDialogue();
        setDefaultValues();
        setSpriteTimers(MOVING_SPRITE_UPDATE_TIME, NUM_MOVING_SPRITES);

    }


    /**
     * @brief Sets the default values for the NPC.
     */
    void setDefaultValues() {
        worldX = gp.TILE_SIZE * 24 - gp.TILE_SIZE; // Spawn at the center of the map
        worldY = gp.TILE_SIZE * 22 - gp.TILE_SIZE; // Spawn at the center of the map
    }


    /**
     * @brief Sets the dialogue for the NPC.
     */
    public void setDialogue() {
        dialogues[0] = "Hello how are you";
        dialogues[1] = "I'm an old man";
        dialogues[2] = "Tararararatgretytryr";
    }

}