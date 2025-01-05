package com.lucafacchini.entity;

import com.lucafacchini.GamePanel;
import com.lucafacchini.KeyHandler;
import com.lucafacchini.objects.SuperObject;
import com.lucafacchini.stats.*;

import java.util.logging.Logger;

/**
 * Represents the player entity in the game.
 *
 * This class defines the player's attributes, controls, and interactions
 * within the game world, including movement, sprite animations, and collision.
 */
public class Player extends Entity {

    // Debugging
    public int hasKey = 0;
    private static final Logger LOGGER = Logger.getLogger(Entity.class.getName());
    public int NOT_USED_YET_1 = 0;

    // Sprite settings
    public final int NUM_MOVING_SPRITES = 6;
    public final int NUM_IDLING_SPRITES = 4;
    public final int SPRITE_HEIGHT_PX = 19;
    public final int SPRITE_WIDTH_PX = 11;
    public final int RESCALED_SPRITE_HEIGHT_PX;
    public final int RESCALED_SPRITE_WIDTH_PX;
    public final int MOVING_SPRITE_UPDATE_TIME = 5;
    public final int IDLING_SPRITE_MULTIPLIER_UPDATE_TIME = 120;
    public final int IDLING_SPRITE_MULTIPLIER_EYES_CLOSED = MOVING_SPRITE_UPDATE_TIME;

    // Indexes of objects and entities
    public int objectIndex;
    public int npcIndex;

    // Player settings
    public final int DEFAULT_SPEED = 8;
    /**
     * @brief screenX and screenY are the coordinates of the player on the screen.
     * They are used to center the player on the screen. Their purpose is to
     * make the player "static" on the screen while the world moves. This will make
     * the illusion that the player is moving.
     */
    public final int screenX;
    public final int screenY;

    // Stats (only for the player)
    public HP hp;

    // KeyHandler
    KeyHandler kh;

    /**
     * @brief Constructs a Player object with initial settings.
     * Initializes sprite dimensions, default values, and key handler setup.
     *
     * @param gp the GamePanel object providing game settings and state.
     * @param kh the KeyHandler object managing player input.
     */
    public Player(GamePanel gp, KeyHandler kh) {
        super(gp);
        this.kh = kh;

        // Center the player on the screen
        screenX = gp.WINDOW_WIDTH / 2 - gp.TILE_SIZE / 2;
        screenY = gp.WINDOW_HEIGHT / 2 - gp.TILE_SIZE / 2;

        boundingBox.x = 0;
        boundingBox.y = 0;
        boundingBox.width =  gp.TILE_SIZE;
        boundingBox.height = gp.TILE_SIZE;
        boundingBoxDefaultX = boundingBox.x;
        boundingBoxDefaultY = boundingBox.y;

        /*
         * @NOTE this might be useless.
         */
        boundingBoxDefaultHeight = boundingBox.height;
        boundingBoxDefaultWidth = boundingBox.width;

        // Calculate rescaled sprite dimensions based on game scale
        RESCALED_SPRITE_HEIGHT_PX = SPRITE_HEIGHT_PX * gp.SCALE;
        RESCALED_SPRITE_WIDTH_PX = SPRITE_WIDTH_PX * gp.SCALE;

        // Stats
        hp = new HP(10);
        speed = new Speed(DEFAULT_SPEED);

        // Load and rescale player sprites
        loadSprites("player", NUM_MOVING_SPRITES, NUM_IDLING_SPRITES);
        rescaleSprites(RESCALED_SPRITE_WIDTH_PX, RESCALED_SPRITE_HEIGHT_PX);

        // Set default values and initialize sprite timers
        setDefaultValues();
        setSpriteTimers(MOVING_SPRITE_UPDATE_TIME, NUM_MOVING_SPRITES);
    }


    /**
     * @brief Initializes the player's default attributes.
     * Sets the spawn location and movement speed of the player.
     */
    void setDefaultValues() {
        // Set player spawn location
        worldX = gp.TILE_SIZE * 27 - gp.TILE_SIZE;
        worldY = gp.TILE_SIZE * 25 - gp.TILE_SIZE;
    }


    /**
     * @brief Updates the player's state during the game loop.
     */
    @Override
    public void update() {
        updateDirection();
        updateSprite();
        updatePosition();

        checkForDialogues();
        checkForEvents();
    }


    /**
     * @brief Updates the player's movement direction based on keyboard input.
     * Determines whether the player is moving or idling and updates
     * the player's current direction accordingly.
     *
     * TODO: Optimize direction checking using a combined boolean flag. (Ex: isMoving = kh.isWASDPressed)
     */
    private void updateDirection() {

        if(gp.gameStatus == GamePanel.GameStatus.RUNNING) {
            boolean isMoving = kh.isUpPressed || kh.isDownPressed || kh.isLeftPressed || kh.isRightPressed;
            boolean isIdle = !isMoving;

            if (isIdle || (kh.isUpPressed && kh.isDownPressed) || (kh.isLeftPressed && kh.isRightPressed)) {
                currentStatus = Status.IDLING;
            } else {
                currentStatus = Status.MOVING;

                if (kh.isUpPressed) { currentDirection = Direction.UP; }
                else if (kh.isDownPressed) { currentDirection = Direction.DOWN; }
                else if (kh.isLeftPressed) { currentDirection = Direction.LEFT; }
                else { currentDirection = Direction.RIGHT; }
            }
        } else if(gp.gameStatus == GamePanel.GameStatus.DIALOGUE) {
            currentStatus = Status.IDLING;
        }
    }


    /**
     * @brief Updates the player's sprite animation.
     * Adjusts the current sprite frame based on the player's status
     * and ensures smooth animation transitions.
     */
    public void updateSprite() {
        spriteFramesCounter++;
        setMultiplier(spriteImageNum); // Adjust animation speed based on player status and sprite frame

        // if(NUM_MOVING_SPRITES > NUM_IDLING_SPRITES)
        if (currentStatus == Status.IDLING && spriteImageNum > NUM_IDLING_SPRITES) {
            spriteImageNum = 1;
        }

        if (spriteFramesCounter >= spriteCounterMultiplier) {
            spriteFramesCounter = 0;
            spriteImageNum++;
            if (spriteImageNum > NUM_MOVING_SPRITES) {
                spriteImageNum = 1;
            }
        }
    }


    /**
     * @brief Sets the multiplier for sprite animation delays.
     * Adjusts animation speed based on the player's status and sprite frame.
     *
     * @param spriteImageNum The current sprite frame number.
     */
    private void setMultiplier(int spriteImageNum) {
        if (currentStatus == Status.IDLING) {
            spriteCounterMultiplier = (spriteImageNum == 1) ? IDLING_SPRITE_MULTIPLIER_UPDATE_TIME : IDLING_SPRITE_MULTIPLIER_EYES_CLOSED;
        } else {
            spriteCounterMultiplier = MOVING_SPRITE_UPDATE_TIME;
        }
    }


    /**
     * @brief Updates the player's position in the game world
     * Handles collision detection and resolves movement based on the
     * player's current direction and interactions.
     */
    private void updatePosition() {
        if (currentStatus == Status.MOVING) {

            // Reset collision flags
            isCollidingWithTile = false;
            isCollidingWithObject = false;
            isCollidingWithEntity = false;

            // Perform collision checks
            gp.cm.checkTile(this, true);

            objectIndex = gp.cm.checkObject(this, true);
            pickUpObject(objectIndex);

            NOT_USED_YET_1 = gp.cm.checkEntity(this, gp.npcArray);
            // Maybe "push" the entity away from the player if they collide?

            if (!isCollidingWithTile && !isCollidingWithObject && !isCollidingWithEntity) {
                move();
            }
        }
    }


    // A flag to prevent multiple dialogues
    private boolean enterKeyProcessed = false;
    public boolean isReadyForNextDialogue = false;

    /**
     * @brief Checks for dialogues with NPCs in the game world.
     *
     * Iterates through the NPC to check if the player is next to an NPC.
     * The flag isNextToPlayer is managed by the Entity class.
     *
     * If the player is next to an NPC, the handleDialogue method is called.
     */
    private void checkForDialogues() {
        for (int i = 0; i < gp.npcArray.length; i++) {
            if (gp.npcArray[i] != null && gp.npcArray[i].isNextToPlayer) {
                handleDialogue(i);
            }
        }
    }

    /**
     * @brief Handles the dialogue with an NPC.
     * Checks if the player has pressed the enter key to start a dialogue with an NPC.
     * If so, the dialogue is displayed on the screen by calling the speak method of the NPC.
     *
     * If the NPC has finished all dialogues, the dialogue index is reset to 0 and
     * the game status is set to RUNNING.
     *
     * @param npcIndex The index of the NPC in the npcArray.
     */
    private void handleDialogue(int npcIndex) {
        this.npcIndex = npcIndex; // @NOTE not used yet.
        isReadyForNextDialogue = false;

        /*
         * If the player presses the enter key and the dialogue has finished printing,
         * then the dialogue is displayed on the screen.
         */
        if (kh.isEnterPressed && !enterKeyProcessed && gp.ui.hasFinishedPrintingDialogue) {
            gp.gameStatus = GamePanel.GameStatus.DIALOGUE;
            gp.npcArray[npcIndex].speak();

            /*
             * If this was the last dialogue, reset the dialogue index to
             * 0 and set the game status to RUNNING.
             *
             * Note that the method to draw the dialogue on the screen,
             * in the UI class, will be executed only if the game status is DIALOGUE.
             *
             * By doing this, the method won't be called anymore after the last dialogue
             * because the game status will be set to RUNNING.
             */
            if (gp.npcArray[npcIndex].hasFinishedDialogues()) {
                gp.npcArray[npcIndex].dialogueIndex = 0;
                gp.gameStatus = GamePanel.GameStatus.RUNNING;
            } else {
                gp.npcArray[npcIndex].dialogueIndex++;
                isReadyForNextDialogue = true;
            }

            /*
             * Set the flag to prevent multiple dialogues.
             */
            enterKeyProcessed = true; // Set the flag to prevent multiple dialogues
        } else if (!kh.isEnterPressed) {
            enterKeyProcessed = false; // Reset the flag when the key is released
        }
    }


    /**
     * @brief Checks for events in the game world.
     * Triggers events based on the player's position in the game world.
     */


    /**
     * @brief Moves the player entity in the current direction.
     */
//    @Override
//    public void move() {
//        switch (currentDirection) {
//            case UP -> worldY -= speed;
//            case DOWN -> worldY += speed;
//            case LEFT -> worldX -= speed;
//            case RIGHT -> worldX += speed;
//        }
//    }


    /**
     * @brief Handles player interaction with objects in the game world.
     *
     * @param index The index of the object in the objectsArray.
     */
    private void pickUpObject(int index) {
        if(index != -1) {
            SuperObject.ObjectType objectName = gp.objectsArray[index].objectType;

            switch(objectName) {
                case KEY -> {
                    hasKey++;
                    gp.ui.showMessage("You picked up a key!");
                    gp.objectsArray[index] = null;
                    gp.playSound(1);
                }

                case DOOR -> {
                    if(hasKey > 0) {
                        gp.ui.showMessage("You used a key!");
                        gp.objectsArray[index] = null;
                        hasKey--;
                        gp.playSound(3);
                    } else {
                        gp.ui.showMessage("You need a key to open this door!");
                    }
                }

                case BOOTS -> {
                    gp.ui.showMessage("You picked up boots!");
                    speed.setCurrent(speed.getCurrent() * 2);
                    gp.objectsArray[index] = null;
                    gp.playSound(2);
                }

                case CHEST -> {
                    gp.stopMusic();
                    gp.playSound(4);
                    gp.ui.gameFinished = true;
                }
            }
        }
    }

    /**
     * @brief Check for events
     */
    public void checkForEvents() {
        gp.eh.checkEvent();
    }
}