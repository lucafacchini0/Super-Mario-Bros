package com.lucafacchini.entity;

import com.lucafacchini.GamePanel;
import com.lucafacchini.KeyHandler;

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
    public final int DEFAULT_SPEED = 5;

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

        // Initialize bounding box dimensions and default values
        boundingBox.x = 0;
        boundingBox.y = 20;
        boundingBox.width = gp.TILE_SIZE - 20;
        boundingBox.height = gp.TILE_SIZE - 10;
        boundingBoxDefaultX = boundingBox.x;
        boundingBoxDefaultY = boundingBox.y;

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
        worldX = gp.TILE_SIZE * 25 - gp.TILE_SIZE;
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
        boolean isMoving = kh.isUpPressed || kh.isDownPressed || kh.isLeftPressed || kh.isRightPressed;
        boolean isIdle = !isMoving;

        if (isIdle || (kh.isUpPressed && kh.isDownPressed) || (kh.isLeftPressed && kh.isRightPressed)) {
            currentStatus = Status.IDLING;
        } else {
            currentStatus = Status.MOVING;

            if (kh.isUpPressed && kh.isLeftPressed) { currentDirection = Direction.UP_LEFT; }
            else if (kh.isUpPressed && kh.isRightPressed) { currentDirection = Direction.UP_RIGHT; }
            else if (kh.isDownPressed && kh.isLeftPressed) { currentDirection = Direction.DOWN_LEFT; }
            else if (kh.isDownPressed && kh.isRightPressed) { currentDirection = Direction.DOWN_RIGHT; }
            else if (kh.isUpPressed) { currentDirection = Direction.UP; }
            else if (kh.isDownPressed) { currentDirection = Direction.DOWN; }
            else if (kh.isLeftPressed) { currentDirection = Direction.LEFT; }
            else { currentDirection = Direction.RIGHT; }
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
            gp.cm.checkTile(this);
            int objectIndex = gp.cm.checkObject(this, true);
            pickUpObject(objectIndex);
            int npcIndex = gp.cm.checkEntity(this, gp.npcArray);
            interactionWithNPC(npcIndex);

            // Resolve movement based on collision results
            if (!isCollidingWithTile && !isCollidingWithObject && !isCollidingWithEntity) {
                move();
            } else if (isCollidingWithTile && !isCollidingWithObject) {
                handleDiagonalCollision(); // TODO: Fix diagonal movement when colliding.
            }
        }
    }


    /**
     * @brief Moves the player entity in the current direction.
     * Supports diagonal movement and overrides the base entity movement behavior.
     */
    @Override
    public void move() {
        switch (currentDirection) {
            case UP_LEFT -> { worldY -= diagonalMove(speed); worldX -= diagonalMove(speed); }
            case UP_RIGHT -> { worldY -= diagonalMove(speed); worldX += diagonalMove(speed); }
            case DOWN_LEFT -> { worldY += diagonalMove(speed); worldX -= diagonalMove(speed); }
            case DOWN_RIGHT -> { worldY += diagonalMove(speed); worldX += diagonalMove(speed); }
            case UP -> worldY -= speed;
            case DOWN -> worldY += speed;
            case LEFT -> worldX -= speed;
            case RIGHT -> worldX += speed;
        }
    }


    /**
     * @brief Handles diagonal movement collision resolution.
     */
// TODO: Fix this method. Diagonal movement is not working while colliding with Objects.
    private void handleDiagonalCollision() {
        boolean collidingLeft = gp.cm.isCollidingFromLeft(this);
        boolean collidingRight = gp.cm.isCollidingFromRight(this);
        boolean collidingTop = gp.cm.isCollidingFromTop(this);
        boolean collidingBottom = gp.cm.isCollidingFromBottom(this);

        switch (currentDirection) {
            case UP_LEFT -> {
                if (!collidingLeft && !collidingTop) {
                    worldY -= diagonalMove(speed);
                    worldX -= diagonalMove(speed);
                } else if (!collidingLeft) {
                    worldX -= diagonalMove(speed);
                } else if (!collidingTop) {
                    worldY -= diagonalMove(speed);
                }
            }

            case UP_RIGHT -> {
                if (!collidingRight && !collidingTop) {
                    worldY -= diagonalMove(speed);
                    worldX += diagonalMove(speed);
                } else if (!collidingRight) {
                    worldX += diagonalMove(speed);
                } else if (!collidingTop) {
                    worldY -= diagonalMove(speed);
                }
            }

            case DOWN_LEFT -> {
                if (!collidingLeft && !collidingBottom) {
                    worldY += diagonalMove(speed);
                    worldX -= diagonalMove(speed);
                } else if (!collidingLeft) {
                    worldX -= diagonalMove(speed);
                } else if (!collidingBottom) {
                    worldY += diagonalMove(speed);
                }
            }

            case DOWN_RIGHT -> {
                if (!collidingRight && !collidingBottom) {
                    worldY += diagonalMove(speed);
                    worldX += diagonalMove(speed);
                } else if (!collidingRight) {
                    worldX += diagonalMove(speed);
                } else if (!collidingBottom) {
                    worldY += diagonalMove(speed);
                }
            }
        }
    }


//    TODO: Fix: Check the sides of the Object. If the player is moving diagonally, check the sides of the object.
//    public void handleCollisionWithObject(int objectIndex) {
//        if (kh.isUpPressed && kh.isLeftPressed) {
//            System.out.println("UP-LEFT DETECTED");
//
//            // Check for collision with the object
//            System.out.println("NOT HITTING OBJECT BB FROM UP-LEFT" + boundingBox);
//            if (boundingBox.intersects(gp.objectsArray[objectIndex].boundingBox)) {
//                System.out.println("UP-LEFT INTERSECTION DETECTED");
//
//                // Check if the player is coming from the left
//                // Player is coming from the left if the player's right side intersects with the object's left side
//                // and there's vertical overlap.
//                if (boundingBox.getMaxX() > gp.objectsArray[objectIndex].boundingBox.getMinX() &&
//                        boundingBox.getMinX() < gp.objectsArray[objectIndex].boundingBox.getMaxX() && // The player is left of the object
//                        boundingBox.getMinY() < gp.objectsArray[objectIndex].boundingBox.getMaxY() &&
//                        boundingBox.getMaxY() > gp.objectsArray[objectIndex].boundingBox.getMinY() &&
//                        boundingBox.getMaxY() <= gp.objectsArray[objectIndex].boundingBox.getMaxY()) { // Ensure player is not hitting from below
//                    // Move up
//                    worldY -= (int) (speed * Math.sqrt(2) / 2); // Move up
//                    System.out.println("Player coming from left, moved up");
//                }
//                // Check if the player is coming from below
//                // Player is coming from below if the player's bottom side intersects with the object's top side
//                // and there's horizontal overlap.
//            }
//        }
//    }


    /**
     * @brief Handles diagonal movement speed.
     *
     * @param index The index of the object in the objectsArray.
     */
    // TODO: Use more specific sound titles (e.g. "key_pickup", "door_open", etc.)
    private void pickUpObject(int index) {
        if(index != -1) {
            String objectName = gp.objectsArray[index].name;

            switch(objectName) {
                case "Key":
                    gp.playSound(1);
                    hasKey++;
                    gp.ui.showMessage("You picked up a key!");
                    gp.objectsArray[index] = null;
                    break;

                case "Door":
                    if(hasKey > 0) {
                        gp.ui.showMessage("You used a key!");
                        gp.playSound(3);
                        hasKey--;
                        gp.objectsArray[index] = null;
                    } else {
                        gp.ui.showMessage("You need a key to open this door!");
                    }
                    break;

                case "Boots":
                    gp.ui.showMessage("You picked up boots!");
                    gp.playSound(2);
                    speed *= 2;
                    gp.objectsArray[index] = null;
                    break;

                case "Chest":
                    gp.stopMusic();
                    gp.playSound(4);
                    gp.ui.gameFinished = true;
                    break;
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
        }
    }
}