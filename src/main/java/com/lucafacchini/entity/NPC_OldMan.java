package com.lucafacchini.entity;

import com.lucafacchini.GamePanel;

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
    public final int SPRITE_HEIGHT_PX = 19;
    public final int SPRITE_WIDTH_PX = 11;
    public final int RESCALED_SPRITE_HEIGHT_PX;
    public final int RESCALED_SPRITE_WIDTH_PX;
    public final int MOVING_SPRITE_UPDATE_TIME = 5;

    // NPC Settings
    public final int DEFAULT_SPEED = 4;

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
        loadSprites("player", NUM_MOVING_SPRITES, NUM_IDLING_SPRITES);
        rescaleSprites(RESCALED_SPRITE_WIDTH_PX, RESCALED_SPRITE_HEIGHT_PX);

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
        worldY = gp.TILE_SIZE * 26 - gp.TILE_SIZE; // Spawn at the center of the map

        speed = DEFAULT_SPEED;
    }


    /**
     * @brief Sets the dialogue for the NPC.
     */
    public void setDialogue() {
        dialogues[0] = "Hello, welcome to FacchiniRPG!";
        dialogues[1] = "I'm so happy you've found me!";
        dialogues[2] = "Have a nice adventure!";
    }


    /**
     * @brief Makes the NPC speak.
     */
    @Override
    public void speak() {
        finishedTalking = false;

        if(dialogues[dialogueIndex] == null) {
            dialogueIndex = 0;
            finishedTalking = true;
        }

        gp.ui.currentDialogue = dialogues[dialogueIndex];
    }
}