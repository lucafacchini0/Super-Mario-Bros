package com.lucafacchini.entity;

import com.lucafacchini.GamePanel;
import com.lucafacchini.Utilities;

import java.util.Random;

public class NPC_OldMan extends Entity{
    public final int NPC_HEIGHT = 19;
    public final int NPC_WIDTH = 11;
    public final int RESCALED_NPC_HEIGTH;
    public final int RESCALED_NPC_WIDTH;
    public final int DEFAULT_NPC_SPEED = 1;

    /**
     * @brief Number of sprites used for  animations.
     */
    public final int NUM_MOVING_SPRITES = 2;
    public final int NUM_IDLING_SPRITES = 2;

    private int updateFrameCounter = 0;

    Utilities utilities = new Utilities();

    public NPC_OldMan(GamePanel gp) {
        super(gp);
        RESCALED_NPC_HEIGTH = NPC_HEIGHT * gp.SCALE;
        RESCALED_NPC_WIDTH = NPC_WIDTH * gp.SCALE;
        setDefaultValues();
        loadSprites("player", NUM_MOVING_SPRITES, NUM_IDLING_SPRITES);
        rescaleSprites(RESCALED_NPC_WIDTH, RESCALED_NPC_HEIGTH);
        setDialogue();

    }

    void setDefaultValues() {
        worldX = gp.TILE_SIZE * 24 - gp.TILE_SIZE; // Spawn at the center of the map
        worldY = gp.TILE_SIZE * 26 - gp.TILE_SIZE; // Spawn at the center of the map

        speed = 1;
    }



    @Override
    public void setAction() {
        updateFrameCounter++;
        if (updateFrameCounter >= 120) {
            Random random = new Random();
            int index = random.nextInt(4); // Random number between 0 and 3

            switch (index) {
                case 0 -> currentDirection = Direction.UP;
                case 1 -> currentDirection = Direction.DOWN;
                case 2 -> currentDirection = Direction.LEFT;
                case 3 -> currentDirection = Direction.RIGHT;
            }
            updateFrameCounter = 0;
        }
    }

    public void setDialogue() {
        dialogues[0] = "Hello, welcome to FacchiniRPG!";
        dialogues[1] = "I'm so happy you've found me!";
        dialogues[2] = "Have a nice adventure!";
    }

    @Override
    public void speak() {
        gp.ui.currentDialogue = dialogues[dialogueIndex];
        if(dialogues[dialogueIndex + 1] != null) {
            dialogueIndex++;
        }
    }
}