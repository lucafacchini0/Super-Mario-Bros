package com.lucafacchini.entity;

import com.lucafacchini.GamePanel;
import com.lucafacchini.KeyHandler;

import java.awt.*;

public class Player extends Entity {

    // ---------------------------------------------- //

    // Debugging

    public int hasKey = 0;

    // ---------------------------------------------- //

    // Sprite settings
    public final int NUM_MOVING_SPRITES = 6;
    public final int NUM_IDLING_SPRITES = 4;
    public final int SPRITE_HEIGHT_PX = 19;
    public final int SPRITE_WIDTH_PX = 11;
    public final int RESCALED_SPRITE_HEIGHT_PX;
    public final int RESCALED_SPRITE_WIDTH_PX;
    // Sprite settings -- delays & multipliers
        public final int MOVING_SPRITE_UPDATE_TIME = 5;
        public final int IDLING_SPRITE_MULTIPLIER_UPDATE_TIME = 120;
        public final int IDLING_SPRITE_MULTIPLIER_EYES_CLOSED = MOVING_SPRITE_UPDATE_TIME;

    // ---------------------------------------------- //

    // Player settings

    /* @BUG: The higher the value of DEFAULT_PLAYER_SPEED, and "more space" is added between the player and the walls.

    The BoundingBox of both the player and the tile don't change, but the player collides with the tile before reaching it.
    The distance between the player and the tile changes based on the position of the player, and it's not the same.

    Example: at X:37, Y:34, the player collides 8px on the right side before reaching the tile and 11px on the bottom side,
    but, maybe, at X:52, Y:14, the player collides 3px on the right side before reaching the tile and 6px on the bottom side. */
    public final int DEFAULT_PLAYER_SPEED = 5; // Player will move by 5px per frame. Higher the framerate, faster the player.

    // Coordinates of the player on the screen. (Always at the center of the screen)
    public final int screenX, screenY;

    // ---------------------------------------------- //

    // KeyHandler -- Exclusive for the player entity. It's used to handle the player's input, it's brought from the GamePanel class.
    KeyHandler kh;

    public Player(GamePanel gp, KeyHandler kh) {

        // ---------------------------------------------- //

        super(gp); // Call Entity constructor passing the GamePanel object
        this.kh = kh;

        // ---------------------------------------------- //

        screenX = gp.WINDOW_WIDTH / 2 - gp.TILE_SIZE / 2; // Center the player on the screen
        screenY = gp.WINDOW_HEIGHT / 2 - gp.TILE_SIZE / 2; // Center the player on the screen

        // ---------------------------------------------- //

        // Bounding box settings
        boundingBox = new Rectangle(0, 20, gp.TILE_SIZE - 20, gp.TILE_SIZE - 10);

        boundingBoxDefaultX = boundingBox.x;
        boundingBoxDefaultY = boundingBox.y;

        // ---------------------------------------------- //

        // Sprites loading
        RESCALED_SPRITE_HEIGHT_PX = SPRITE_HEIGHT_PX * gp.SCALE;
        RESCALED_SPRITE_WIDTH_PX = SPRITE_WIDTH_PX * gp.SCALE;

        loadSprites("player", NUM_MOVING_SPRITES, NUM_IDLING_SPRITES);
        rescaleSprites(RESCALED_SPRITE_WIDTH_PX, RESCALED_SPRITE_HEIGHT_PX);

        // ---------------------------------------------- //

        setDefaultValues();
        setSpriteTimers(MOVING_SPRITE_UPDATE_TIME, NUM_MOVING_SPRITES);
    }

    void setDefaultValues() {
        // Coordinates of spawn
        worldX = gp.TILE_SIZE * 25 - gp.TILE_SIZE; // Spawn at the center of the map
        worldY = gp.TILE_SIZE * 25 - gp.TILE_SIZE; // Spawn at the center of the map

        // Player speed
        speed = DEFAULT_PLAYER_SPEED;
    }


    // In loop
    @Override
    public void update() {
        updateDirection();
        updateSprite();
        updatePosition();
    }

    private void updateDirection() {
        // SECONDARY TODO: instead of checking every single key, check if one key is pressed. This can be obtained if one of the keys is pressed. (eg. a bool WASDPressed)
        boolean isMoving = kh.isUpPressed || kh.isDownPressed || kh.isLeftPressed || kh.isRightPressed;
        boolean isIdle = !isMoving;

        if (isIdle || (kh.isUpPressed && kh.isDownPressed) || (kh.isLeftPressed && kh.isRightPressed)) {
            currentStatus = Status.IDLING;
        }

        else {
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

    public void updateSprite() {
        spriteFramesCounter++; // Increase the counter every frame

        // This method has the only purpose of setting some "delays" in the sprite animation
        // depending on the current direction of the player. It does not follow any
        // logical pattern, it's just a way to make the sprite animation look better.
        setMultiplier(spriteImageNum);

        // If the player is idling, the sprite image number should not exceed the number of idling sprites.
        // If it does, reset it to 1 so that the animation starts from the beginning.
        if(currentStatus == Status.IDLING) {
            if(spriteImageNum > NUM_IDLING_SPRITES) {
                spriteImageNum = 1;
            }
        }

        // Update the sprite image number, depending on the spriteCounterMultiplier
        // That variable depends on the current direction of the player. It's
        // assigned in the setMultiplier method.
        if (spriteFramesCounter >= spriteCounterMultiplier) {
            spriteFramesCounter = 0;
            spriteImageNum++;
            if (spriteImageNum > NUM_MOVING_SPRITES) {
                spriteImageNum = 1;
            }
        }
    }

    // Update the sprite image number, depending on the spriteCounterMultiplier
    // That variable depends on the current direction of the player. It's
    // assigned in the setMultiplier method.
    private void setMultiplier(int spriteImageNum) {
        if(currentStatus == Status.IDLING) {
            if(spriteImageNum == 1) {
                spriteCounterMultiplier = IDLING_SPRITE_MULTIPLIER_UPDATE_TIME;
            } else {
                spriteCounterMultiplier = IDLING_SPRITE_MULTIPLIER_EYES_CLOSED;
            }
        } else {
            spriteCounterMultiplier = MOVING_SPRITE_UPDATE_TIME;
        }
    }

    private void updatePosition() {
        boolean isMoving = kh.isUpPressed || kh.isDownPressed || kh.isLeftPressed || kh.isRightPressed;

       // if (isMoving && !(kh.isUpPressed && kh.isDownPressed) && !(kh.isLeftPressed && kh.isRightPressed)) {
        if(currentStatus == Status.MOVING) {

            // ---------------------------- //

            // Booleans are updated in the collision manager.

            isCollidingWithTile = false;
            isCollidingWithObject = false;
            isCollidingWithEntity = false;

            // Booleans are updated in the collision manager.

            // Check tile collision
            gp.collisionManager.checkTile(this);

            // Check object collision
            int objectIndex = gp.collisionManager.checkObject(this, true);
            pickUpObject(objectIndex);

            // Check NPC collision
            int npcIndex = gp.collisionManager.checkEntity(this, gp.npcArray);
            interractionWithNPC(npcIndex);

            // ---------------------------- //

            if (!isCollidingWithTile && !isCollidingWithObject && !isCollidingWithEntity) {
                move();
            } else if (isCollidingWithTile && !isCollidingWithObject && !isCollidingWithEntity) {
                handleDiagonalCollision(); // TODO: Fix this method. Diagonal movement is not working while colliding with Objects.
            } else { // isCollidingWithObject
                // handleCollisionWithObject(objectIndex);
            }
        }
    }

    // @NOTE: Override because the player moves differently than the other entities.
    // The player can move diagonally, while the other entities can't.
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

    // TODO: Fix this method. Diagonal movement is not working while colliding with Objects.
    private void handleDiagonalCollision() {

        switch(currentDirection) {
            case UP_LEFT -> {
                if(gp.collisionManager.isCollidingFromLeft(this) && gp.collisionManager.isCollidingFromTop(this)) {}
                else if(gp.collisionManager.isCollidingFromLeft(this)) { worldY -= diagonalMove(speed); }
                else if(gp.collisionManager.isCollidingFromTop(this)) { worldX -= diagonalMove(speed); }
                else { worldY -= diagonalMove(speed); worldX -= diagonalMove(speed); } // NOTE: Might be unnecessary, because the method is called only if the player is colliding with a tile. Will check later.
            }

            case UP_RIGHT -> {
                if(gp.collisionManager.isCollidingFromRight(this) && gp.collisionManager.isCollidingFromTop(this)) {}
                else if(gp.collisionManager.isCollidingFromRight(this)) { worldY -= diagonalMove(speed); }
                else if(gp.collisionManager.isCollidingFromTop(this)) { worldX += diagonalMove(speed); }
                else { worldY -= diagonalMove(speed); worldX += diagonalMove(speed); }
            }

            case DOWN_LEFT -> {
                if(gp.collisionManager.isCollidingFromLeft(this) && gp.collisionManager.isCollidingFromBottom(this)) {}
                else if(gp.collisionManager.isCollidingFromLeft(this)) { worldY += diagonalMove(speed); }
                else if(gp.collisionManager.isCollidingFromBottom(this)) { worldX -= diagonalMove(speed); }
                else { worldY += diagonalMove(speed); worldX -= diagonalMove(speed); }
            }

            case DOWN_RIGHT -> {
                if(gp.collisionManager.isCollidingFromRight(this) && gp.collisionManager.isCollidingFromBottom(this)) {}
                else if(gp.collisionManager.isCollidingFromRight(this)) { worldY += diagonalMove(speed); }
                else if(gp.collisionManager.isCollidingFromBottom(this)) { worldX += diagonalMove(speed); }
                else { worldY += diagonalMove(speed); worldX += diagonalMove(speed); }
            }
        }


//        if (kh.isUpPressed && kh.isLeftPressed) {
//            if (gp.collisionManager.isCollidingFromLeft(this) && gp.collisionManager.isCollidingFromTop(this)) {
//            } else if (gp.collisionManager.isCollidingFromLeft(this)) {
//                worldY -= (int) (speed * Math.sqrt(2) / 2);
//            } else if (gp.collisionManager.isCollidingFromTop(this)) {
//                worldX -= (int) (speed * Math.sqrt(2) / 2);
//            } else {
//                worldY -= (int) (speed * Math.sqrt(2) / 2);
//                worldX -= (int) (speed * Math.sqrt(2) / 2);
//            }
//        } else if (kh.isUpPressed && kh.isRightPressed) {
//            if (gp.collisionManager.isCollidingFromRight(this) && gp.collisionManager.isCollidingFromTop(this)) {
//            } else if (gp.collisionManager.isCollidingFromRight(this)) {
//                worldY -= (int) (speed * Math.sqrt(2) / 2);
//            } else if (gp.collisionManager.isCollidingFromTop(this)) {
//                worldX += (int) (speed * Math.sqrt(2) / 2);
//            } else {
//                worldY -= (int) (speed * Math.sqrt(2) / 2);
//                worldX += (int) (speed * Math.sqrt(2) / 2);
//            }
//        } else if (kh.isDownPressed && kh.isLeftPressed) {
//            if (gp.collisionManager.isCollidingFromLeft(this) && gp.collisionManager.isCollidingFromBottom(this)) {
//            } else if (gp.collisionManager.isCollidingFromLeft(this)) {
//                worldY += (int) (speed * Math.sqrt(2) / 2);
//            } else if (gp.collisionManager.isCollidingFromBottom(this)) {
//                worldX -= (int) (speed * Math.sqrt(2) / 2);
//            } else {
//                worldY += (int) (speed * Math.sqrt(2) / 2);
//                worldX -= (int) (speed * Math.sqrt(2) / 2);
//            }
//        } else if (kh.isDownPressed && kh.isRightPressed) {
//            if (gp.collisionManager.isCollidingFromRight(this) && gp.collisionManager.isCollidingFromBottom(this)) {
//            } else if (gp.collisionManager.isCollidingFromRight(this)) {
//                worldY += (int) (speed * Math.sqrt(2) / 2);
//            } else if (gp.collisionManager.isCollidingFromBottom(this)) {
//                worldX += (int) (speed * Math.sqrt(2) / 2);
//            } else {
//                worldY += (int) (speed * Math.sqrt(2) / 2);
//                worldX += (int) (speed * Math.sqrt(2) / 2);
//            }
//        }
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

    public void interractionWithNPC(int index) {
        if(index != -1) {
            gp.gameStatus = GamePanel.GameStatus.DIALOGUE;
            gp.npcArray[index].speak();
        }
    }
}