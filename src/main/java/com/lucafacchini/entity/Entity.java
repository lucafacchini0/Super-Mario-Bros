package com.lucafacchini.entity;

import com.lucafacchini.GamePanel;
import com.lucafacchini.Utilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;


/**
 * Represents an entity in the game.
 * 
 * This class is the superclass of all entities in the game.
 * This class defines the properties and methods that all entities in the game share.
 * 
 * The only exception is the player class, which Overrides some methods.
 */
public class Entity {
    
    // Debugging
    private static final Logger LOGGER = Logger.getLogger(Entity.class.getName());

    public boolean blockMovement = false;

    // Sprite settings and declarations
    /**
     * @brief Enumerator that contains all the possible directions of the entity.
     * This enumerator is only used to initialize the keys of the hashmap and
     * to allow the backend to change the sprite image based on the direction.
     * The actual direction of the player has another enumerator.
     */
    public enum SpriteImagesEnum {
        UP_MOVING, DOWN_MOVING, LEFT_MOVING, RIGHT_MOVING,
        UP_IDLING, DOWN_IDLING, LEFT_IDLING, RIGHT_IDLING
    }

    /**
     * @brief The hashmap that contains the sprite images of the entity.
     * The keys are the directions of the entity, and the values are the sprite images of the entity.
     */
    public HashMap<SpriteImagesEnum, ArrayList<BufferedImage>> spriteImages = new HashMap<>();

    protected int spriteCounterMultiplier; // This variable is used to check the sprite animation speed. It's incremented by 1 every frame.
    protected int spriteFramesCounter = 0; // Frames that has passed since the last sprite change.
    protected int spriteImageNum = 1; // The current sprite num

    // TODO: This variable is defined in every subclass. It should be defined here.
    private int NUM_MOVING_SPRITES;


    // Entity properties
    public enum Status { IDLING, MOVING }
    public Status currentStatus = Status.MOVING;

    /**
     * @brief Enumerator that contains all the possible directions of the entity.
     * This enumerator is used to determine the direction of the entity, and based on
     * this direction, the backend changes the sprite image using the SpriteImagesEnum enumerator.
     */
    public enum Direction { UP, DOWN, LEFT, RIGHT }
    public Direction currentDirection = Direction.DOWN;

    public int worldX, worldY; // The position of the entity in the world.
    public int speed;

    public Rectangle boundingBox; // The bounding box of the entity.
    public int boundingBoxDefaultX, boundingBoxDefaultY, boundingBoxDefaultWidth, boundingBoxDefaultHeight;


    // Utilities
    private final Utilities utilities = new Utilities();


    // Collision booleans
    public boolean isCollidingWithTile = false;
    public boolean isCollidingWithObject = false;
    public boolean isCollidingWithEntity = false;


    // Actions 
    public int actionCounter = 0; // Counter that tracks how much time before next action.


    // Dialogues
    public String[] dialogues = new String[20]; // TODO: Change to HashMap
    public int dialogueIndex = 0;
    // Has the NPC finished talking?
    public boolean finishedTalking = false;


    // GamePanel
    GamePanel gp;


    /**
     * @brief Constructor of the Entity class.
     * Initializes the bounding box of the entity and the GamePanel.
     *
     * @param gp the GamePanel object providing game settings and state.
     */
    public Entity(GamePanel gp) {
        this.gp = gp;
        boundingBox = new Rectangle(0, 0, gp.TILE_SIZE, gp.TILE_SIZE);
    }


    /**
     * @brief Method that sets the sprite timers.
     * @param spriteCounterMultiplier the sprite counter multiplier. (After how many frames the sprite should change.)
     * @param NUM_MOVING_SPRITES the number of moving sprites. (The number of sprite images for each direction.)
     */
    void setSpriteTimers(int spriteCounterMultiplier, int NUM_MOVING_SPRITES) {
        this.spriteCounterMultiplier = spriteCounterMultiplier;
        this.NUM_MOVING_SPRITES = NUM_MOVING_SPRITES;
    }


    /**
     * @brief Method that loads the sprite images of the entity.
     * @param folderPath the path of the folder containing the sprite images.
     * @param NUM_MOVING the number of moving sprites.
     * @param NUM_IDLING the number of idling sprites.
     */
    public void loadSprites(String folderPath,
                            int NUM_MOVING,
                            int NUM_IDLING) {

        // Initialize hashmap
        for (SpriteImagesEnum direction : SpriteImagesEnum.values()) {
            spriteImages.put(direction, new ArrayList<>());
        }

        try {
            for(int i = 0; i < NUM_MOVING; i++) {
                spriteImages.get(SpriteImagesEnum.UP_MOVING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_up_" + (i+1) + ".png"))));
                spriteImages.get(SpriteImagesEnum.DOWN_MOVING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_down_" + (i+1) + ".png"))));
                spriteImages.get(SpriteImagesEnum.LEFT_MOVING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_left_" + (i+1) + ".png"))));
                spriteImages.get(SpriteImagesEnum.RIGHT_MOVING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_right_" + (i+1) + ".png"))));
            }

            for(int i = 0; i < NUM_IDLING; i++) {
                spriteImages.get(SpriteImagesEnum.UP_IDLING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_up_" + (i+1) + ".png"))));
                spriteImages.get(SpriteImagesEnum.DOWN_IDLING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_down_" + (i+1) + ".png"))));
                spriteImages.get(SpriteImagesEnum.LEFT_IDLING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_left_" + (i+1) + ".png"))));
                spriteImages.get(SpriteImagesEnum.RIGHT_IDLING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_right_" + (i+1) + ".png"))));

            }

        } catch (IOException e) {
            LOGGER.severe("Error loading images: " + e.getMessage());
        }
    }


    /**
     * @brief Secondary method that loads the sprite images of the entity.
     * @param folderPath the path of the folder containing the sprite images.
     * @param NUM_WALK_UP the number of walking up sprites.
     * @param NUM_WALK_DOWN the number of walking down sprites.
     * @param NUM_WALK_LEFT the number of walking left sprites.
     * @param NUM_WALK_RIGHT the number of walking right sprites.
     * @param NUM_IDLE_UP the number of idling up sprites.
     * @param NUM_IDLE_DOWN the number of idling down sprites.
     * @param NUM_IDLE_LEFT the number of idling left sprites.
     * @param NUM_IDLE_RIGHT the number of idling right sprites.
     */
    public void loadSprites(String folderPath,
                            int NUM_WALK_UP, int NUM_WALK_DOWN, int NUM_WALK_LEFT, int NUM_WALK_RIGHT,
                            int NUM_IDLE_UP, int NUM_IDLE_DOWN, int NUM_IDLE_LEFT, int NUM_IDLE_RIGHT) {

        // Initialize hashmap
        for (SpriteImagesEnum direction : SpriteImagesEnum.values()) {
            spriteImages.put(direction, new ArrayList<>());
        }

        try {
            for(int i = 0; i < NUM_WALK_UP; i++) { spriteImages.get(SpriteImagesEnum.UP_MOVING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_up_" + (i+1) + ".png")))); }
            for(int i = 0; i < NUM_WALK_DOWN; i++) { spriteImages.get(SpriteImagesEnum.DOWN_MOVING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_down_" + (i+1) + ".png")))); }
            for(int i = 0; i < NUM_WALK_LEFT; i++) { spriteImages.get(SpriteImagesEnum.LEFT_MOVING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_left_" + (i+1) + ".png")))); }
            for(int i = 0; i < NUM_WALK_RIGHT; i++) { spriteImages.get(SpriteImagesEnum.RIGHT_MOVING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_right_" + (i+1) + ".png")))); }

            for(int i = 0; i < NUM_IDLE_UP; i++) { spriteImages.get(SpriteImagesEnum.UP_IDLING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_up_" + (i+1) + ".png")))); }
            for(int i = 0; i < NUM_IDLE_DOWN; i++) { spriteImages.get(SpriteImagesEnum.DOWN_IDLING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_down_" + (i+1) + ".png")))); }
            for(int i = 0; i < NUM_IDLE_LEFT; i++) { spriteImages.get(SpriteImagesEnum.LEFT_IDLING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_left_" + (i+1) + ".png")))); }
            for(int i = 0; i < NUM_IDLE_RIGHT; i++) { spriteImages.get(SpriteImagesEnum.RIGHT_IDLING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_right_" + (i+1) + ".png")))); }
        } catch (IOException e) {
            LOGGER.severe("Error loading images: " + e.getMessage());
        }
    }


    /**
     * @brief Method that rescales the sprite images of the entity.
     * @param WIDTH the width of the sprite. (Width should represent the width already rescaled in px.)
     * @param HEIGHT the height of the sprite. (Height should represent the height already rescaled in px.)
     */
    public void rescaleSprites(int WIDTH, int HEIGHT) {
        for (SpriteImagesEnum direction : SpriteImagesEnum.values()) {
            ArrayList<BufferedImage> images = spriteImages.get(direction);

            if (images != null) {
                for (int i = 0; i < images.size(); i++) {
                    if (images.get(i) != null) {
                        images.set(i, utilities.rescaleImage(images.get(i), WIDTH, HEIGHT));
                    }
                }
            }
        }
    }


    /**
     * @brief Method used to set action of the entity.
     */
    public void setAction() {
        actionCounter++;
        if (actionCounter >= 120) {
            Random random = new Random();
            int index = random.nextInt(4); // Random number between 0 and 3

            switch (index) {
                case 0 -> currentDirection = Direction.UP;
                case 1 -> currentDirection = Direction.DOWN;
                case 2 -> currentDirection = Direction.LEFT;
                case 3 -> currentDirection = Direction.RIGHT;
            }
            actionCounter = 0;
        }
    }


    /**
     * @brief Method used to update the entity.
     * This is called every frame.
     */
    public void update() {
       setAction(); // So far, this updates the direction of the entity.

        spriteFramesCounter++;

        if (spriteFramesCounter > spriteCounterMultiplier) {
            spriteImageNum++;
            if (spriteImageNum > NUM_MOVING_SPRITES) {
                spriteImageNum = 1;
            }
            spriteFramesCounter = 0;
        }

        isCollidingWithTile = false;
        isCollidingWithEntity = false;
        isCollidingWithObject = false;
        // Booleans are updated in the collision manager class.

        // Check tile collisions
        gp.cm.checkTile(this, false);

        // Check object collisions
        gp.cm.checkObject(this, false);

        // Check player collisions (for NPCs)
        gp.cm.checkPlayer(this);

        if(!isCollidingWithTile && !isCollidingWithEntity && !isCollidingWithObject) {
            move();
        }
    }


    /**
     * @brief Method used to move the entity.
     */
    public void move() {
        if(blockMovement) return;

        switch (currentDirection) {
            case UP -> worldY -= speed;
            case DOWN -> worldY += speed;
            case LEFT -> worldX -= speed;
            case RIGHT -> worldX += speed;
        }
    }


    /**
     * @brief Method used to draw the entity.
     * First of all, the method calculates the screen position of the entity.
     * It is necessary to check if the entity is in the screen before drawing it.
     * If it's not, the method doesn't draw the entity. This is done to optimize the game.
     * and avoid drawing entities that are not visible. (Does it even make sense? xD)
     *
     * The method then gets the sprite direction of the entity and draws the sprite image.
     *
     * @param g2d the Graphics2D object used to draw the entity.
     */
    public void draw(Graphics2D g2d) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (isVisible()) {
            SpriteImagesEnum direction = getSpriteDirection();
            BufferedImage image;

            ArrayList<BufferedImage> frames = spriteImages.get(direction);

            // if (frames != null && !frames.isEmpty())
            int frameIndex = (spriteImageNum - 1) % frames.size();
            image = frames.get(frameIndex);

            // if (image != null)
            g2d.drawImage(image, screenX, screenY, null);
            g2d.setColor(Color.RED);

            // TODO: bounding box of NPCs is unexpectedly small. it's represented by a pixel at 0,0.
            g2d.drawRect(screenX + boundingBox.x, screenY + boundingBox.y, boundingBox.width, boundingBox.height);
        }
    }


    /**
     * @brief This method checks if the tile is visible on the screen.
     * It calculates the boundaries of the tile and checks if it is within the screen boundaries.
     *
     * @return true if the tile is visible, false otherwise.
     */
    protected boolean isVisible() {
        return worldX + gp.TILE_SIZE > gp.player.worldX - gp.player.screenX &&
                worldX - gp.TILE_SIZE < gp.player.worldX + gp.player.screenX &&
                worldY + gp.TILE_SIZE > gp.player.worldY - gp.player.screenY &&
                worldY - gp.TILE_SIZE < gp.player.worldY + gp.player.screenY;
    }


    /**
     * @brief Method used to get the sprite direction of the entity.
     * @return the sprite direction of the entity.
     */
    public SpriteImagesEnum getSpriteDirection() {
        SpriteImagesEnum direction;

        if(currentStatus == Status.IDLING) {
            direction = switch(currentDirection) {
                case Direction.UP -> SpriteImagesEnum.UP_IDLING;
                case Direction.DOWN -> SpriteImagesEnum.DOWN_IDLING;
                case Direction.LEFT -> SpriteImagesEnum.LEFT_IDLING;
                case Direction.RIGHT -> SpriteImagesEnum.RIGHT_IDLING;
            };
        } else {
            direction = switch(currentDirection) {
                case Direction.UP -> SpriteImagesEnum.UP_MOVING;
                case Direction.DOWN -> SpriteImagesEnum.DOWN_MOVING;
                case Direction.LEFT -> SpriteImagesEnum.LEFT_MOVING;
                case Direction.RIGHT -> SpriteImagesEnum.RIGHT_MOVING;
            };
        }
        return direction;
    }


    /**
     * @brief Method used to make the NPC speak.
     */
    public void speak() {
        System.out.println("Speaking");
        gp.ui.currentDialogue = dialogues[dialogueIndex];
    }




    // Debugging
    public void blockNPC(int index) {

    }



    public boolean hasFinishedTalking() {
        dialogueIndex++;

        if(dialogues[dialogueIndex] == null) {
            dialogueIndex = 0;
            return true;
        }

        gp.ui.currentDialogue = dialogues[dialogueIndex];
        return false;
    }
}