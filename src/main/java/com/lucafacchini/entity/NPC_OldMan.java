package com.lucafacchini.entity;

import com.lucafacchini.GamePanel;
import com.lucafacchini.Utilities;

import java.util.Random;

public class NPC_OldMan extends Entity{
    public final int NPC_HEIGHT = 16;
    public final int NPC_WIDTH = 16;
    public final int RESCALED_NPC_HEIGTH;
    public final int RESCALED_NPC_WIDTH;
    public final int DEFAULT_NPC_SPEED = 1;

    private int updateFrameCounter = 0;

    Utilities utilities = new Utilities();

    public NPC_OldMan(GamePanel gp) {
        super(gp);
        RESCALED_NPC_HEIGTH = NPC_HEIGHT * gp.SCALE;
        RESCALED_NPC_WIDTH = NPC_WIDTH * gp.SCALE;
        setDefaultValues();
       // getImages("npc/old_man");
        rescaleImages();
        setDialogue();

    }

    void setDefaultValues() {
//        worldX = gp.TILE_SIZE * 24 - gp.TILE_SIZE; // Spawn at the center of the map
//        worldY = gp.TILE_SIZE * 26 - gp.TILE_SIZE; // Spawn at the center of the map
//
//        speed = 1;
//        currentDirection = "down";

    }

    private void rescaleImages() {
//        for(int i = 0; i < MAX_SPRITES_PER_WALKING_DIRECTION; i++) {
//            if (upImages[i] != null) {
//                upImages[i] = utilities.rescaleImage(upImages[i], RESCALED_NPC_WIDTH, RESCALED_NPC_HEIGTH);
//            }
//            if(downImages[i] != null) {
//                downImages[i] = utilities.rescaleImage(downImages[i], RESCALED_NPC_WIDTH, RESCALED_NPC_HEIGTH);
//            }
//            if(leftImages[i] != null) {
//                leftImages[i] = utilities.rescaleImage(leftImages[i], RESCALED_NPC_WIDTH, RESCALED_NPC_HEIGTH);
//            }
//            if(rightImages[i] != null) {
//                rightImages[i] = utilities.rescaleImage(rightImages[i], RESCALED_NPC_WIDTH, RESCALED_NPC_HEIGTH);
//            }
//        }
//
//        for(int i = 0; i < MAX_SPRITES_PER_IDLING_DIRECTION; i++) {
//            if(idlingUpImages[i] != null) {
//                idlingUpImages[i] = utilities.rescaleImage(idlingUpImages[i], RESCALED_NPC_WIDTH, RESCALED_NPC_HEIGTH);
//            }
//            if(idlingDownImages[i] != null) {
//                idlingDownImages[i] = utilities.rescaleImage(idlingDownImages[i], RESCALED_NPC_WIDTH, RESCALED_NPC_HEIGTH);
//            }
//            if(idlingLeftImages[i] != null) {
//                idlingLeftImages[i] = utilities.rescaleImage(idlingLeftImages[i], RESCALED_NPC_WIDTH, RESCALED_NPC_HEIGTH);
//            }
//            if(idlingRightImages[i] != null) {
//                idlingRightImages[i] = utilities.rescaleImage(idlingRightImages[i], RESCALED_NPC_WIDTH, RESCALED_NPC_HEIGTH);
//            }
//        }
    }

    @Override
    public void setAction() {
//        updateFrameCounter++;
//        if (updateFrameCounter >= 120) {
//            Random random = new Random();
//            int index = random.nextInt(4); // Random number between 0 and 3
//
//            switch (index) {
//                case 0 -> currentDirection = "up";
//                case 1 -> currentDirection = "down";
//                case 2 -> currentDirection = "left";
//                case 3 -> currentDirection = "right";
//            }
//            updateFrameCounter = 0;
//        }
    }

    public void setDialogue() {
//        dialogues[0] = "Hello, welcome to FacchiniRPG!";
//        dialogues[1] = "I'm so happy you've found me!";
//        dialogues[2] = "Have a nice adventure!";
    }

    @Override
    public void speak() {
//        gp.ui.currentDialogue = dialogues[dialogueIndex];
//        if(dialogues[dialogueIndex + 1] != null) {
//            dialogueIndex++;
//        }
    }
}