package com.lucafacchini.entity;

import com.lucafacchini.GamePanel;
import com.lucafacchini.KeyHandler;
import com.lucafacchini.objects.SuperObject;

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
    /**
     * @BUG The higher the value of DEFAULT_SPEED, and "more space" is added between the player and the walls.
     *
     *     The BoundingBox of both the player and the tile don't change, but the player collides with the tile before reaching it.
     *     The distance between the player and the tile changes based on the position of the player, and it's not the same.
     *
     *     Example: at X:37, Y:34, the player collides 8px on the right side before reaching the tile and 11px on the bottom side,
     *     but, maybe, at X:52, Y:14, the player collides 3px on the right side before reaching the tile and 6px on the bottom side
     *
     */
    public final int DEFAULT_SPEED = 8;

    /**
     * @brief screenX and screenY are the coordinates of the player on the screen.
     * They are used to center the player on the screen. Their purpose is to
     * make the player "static" on the screen while the world moves. This will make
     * the illusion that the player is moving.
     */
    public final int screenX;
    public final int screenY;

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

        // Set player speed
        speed = DEFAULT_SPEED;
    }


    /**
     * @brief Updates the player's state during the game loop.
     */
    @Override
    public void update() {
        updateDirection();
        updateSprite();
        updatePosition();
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

            npcIndex = gp.cm.checkEntity(this, gp.npcArray);
            interactionWithNPC(npcIndex);


            if (!isCollidingWithTile && !isCollidingWithObject && !isCollidingWithEntity) {
                move();
            }
        }
    }


    /**
     * @brief Moves the player entity in the current direction.
     */
    @Override
    public void move() {
        switch (currentDirection) {
            case UP -> worldY -= speed;
            case DOWN -> worldY += speed;
            case LEFT -> worldX -= speed;
            case RIGHT -> worldX += speed;
        }
    }


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
                    speed *= 2;
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
     * @brief Handles player interaction with NPCs.
     *
     * @param index The index of the NPC in the npcArray.
     */
    public void interactionWithNPC(int index) {
        if(index != -1) {
            gp.gameStatus = GamePanel.GameStatus.DIALOGUE;
            gp.npcArray[index].speak();
        } else {
            gp.gameStatus = GamePanel.GameStatus.RUNNING;
        }
    }
}