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
import java.util.logging.Logger;


/**
 * {@code @brief} The Entity class is the superclass of all entities in the game.
 * <p>
 *  It contains the properties and methods that are common to all entities.
 */
public class Entity {

    // ---------------------------------------------- //

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(Entity.class.getName());

    // ---------------------------------------------- //

    // Entity properties
    public int worldX, worldY;
    public int speed;

    // ---------------------------------------------- //

                // SPRITES //

    /**
     * {@code @brief} Enumerator that contains all the possible directions of the entity.
     * <p>
     * {@code @note} This enumerator is only used to initialize the keys of the hashmap.
     * The actual direction is stored in the currentDirection variable and for backend drawing.
     * <p>
     * SpriteImagesEnum is an enumerator that contains all the possible directions of the entity.
     * It is used to initialize the keys of the hashmap that contains the sprite images of the entity.
     * The actual direction of the Entity is not stored in this enumerator, but in the currentDirection variable,
     * which is another enumerator.
     */
    public enum SpriteImagesEnum {
        UP_MOVING, DOWN_MOVING, LEFT_MOVING, RIGHT_MOVING,
        UP_IDLING, DOWN_IDLING, LEFT_IDLING, RIGHT_IDLING
    }


    /**
     * @brief The hashmap that contains the sprite images of the entity.
     *
     * The keys are the directions of the entity, and the values are the sprite images of the entity.
     */
    public HashMap<SpriteImagesEnum, ArrayList<BufferedImage>> spriteImages = new HashMap<>();

    /**
     * @brief Variables to manage the sprite images of the entity.
     * <p>
     * spriteCounterMultiplier is used to check the sprite animation speed. It's incremented by 1 every frame.
     * spriteFramesCounter is the number of frames that has passed since the last sprite change.
     * spriteImageNum is the current sprite num.
     * NUM_MOVING_SPRITES is the number of moving sprites (the images).
     */
    protected int spriteCounterMultiplier; // This variable is used to check the sprite animation speed. It's incremented by 1 every frame.
    protected int spriteFramesCounter = 0; // Frames that has passed since the last sprite change.
    protected int spriteImageNum = 1; // The current sprite num
    private int NUM_MOVING_SPRITES = 0;


    // ---------------------------------------------- //

    // Entity status and direction.
    public enum Status { IDLING, MOVING }

    // Direction is used to determine the direction of the entity. It is used to determine which sprite image to draw.
    // Note: based on the direction, the sprite image is selected from the hashmap.
    public enum Direction { UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT }

    // ---------------------------------------------- //

    // Current status of the entity
    public Status currentStatus = Status.MOVING;
    public Direction currentDirection = Direction.DOWN;

    // ---------------------------------------------- //

    // Utilities, used for rescaling images.
    private final Utilities utilities = new Utilities();

    // ---------------------------------------------- //

    // The bounding box of the entity and whether it is colliding with another entity.
    public Rectangle boundingBox; // The bounding box of the entity.
    public int boundingBoxDefaultX, boundingBoxDefaultY;

    public boolean isCollidingWithTile = false;
    public boolean isCollidingWithObject = false;
    public boolean isCollidingWithEntity = false;

    // ---------------------------------------------- //

    GamePanel gp;

    String[] dialogues = new String[20]; // TODO: Change to HashMap
    int dialogueIndex = 0;

    public Entity(GamePanel gp) {
        this.gp = gp;
        boundingBox = new Rectangle(0, 0, gp.TILE_SIZE, gp.TILE_SIZE);
    }

    // ---------------------------------------------- //


    // ---------------------------------------------- //

                // ACCESSORY METHODS //

    int diagonalMove(int speed) { return (int)(speed * Math.sqrt(2) / 2); }

    void setSpriteTimers(int spriteCounterMultiplier, int NUM_MOVING_SPRITES) {
        this.spriteCounterMultiplier = spriteCounterMultiplier;
        this.NUM_MOVING_SPRITES = NUM_MOVING_SPRITES;
    }

    // ---------------------------------------------- //


    // ---------------------------------------------- //

                    // SPRITES //

    // ---------------------------------------------- //

    // If the entity has the same number of sprites for each status (MOVING, IDLING), use this method.
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

    // If the entity has different number of sprites for each status (MOVING, IDLING), use this method.
    // NOTE: Animations are not optimized. Optimizing this is not a priority.
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







    public void setAction() {}
    public void speak() {}

    public void update() {
       setAction();

        spriteFramesCounter++;

        if (spriteFramesCounter > spriteCounterMultiplier) {
            spriteImageNum++;
            if (spriteImageNum > NUM_MOVING_SPRITES) {
                spriteImageNum = 1;
            }
            spriteFramesCounter = 0;
        }

        // Direction is updated in the subclass of NPCs.

        // ---------------------------- //

        isCollidingWithTile = false;
        isCollidingWithEntity = false;
        isCollidingWithObject = false;

        // Booleans are updated in the collision manager.

        // Check tile collisions
        gp.collisionManager.checkTile(this);

        // Check object collisions
        gp.collisionManager.checkObject(this, false);

        // Check player collisions (for NPCs)
        gp.collisionManager.checkPlayer(this);

        // ---------------------------- //

        if(!isCollidingWithTile && !isCollidingWithEntity && !isCollidingWithObject) {
            move();
        }
    }


    public void move() {
        switch (currentDirection) {
            case UP -> worldY -= speed;
            case DOWN -> worldY += speed;
            case LEFT -> worldX -= speed;
            case RIGHT -> worldX += speed;
        }
    }





    public void draw(Graphics2D g2d) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.TILE_SIZE > gp.player.worldX - gp.player.screenX &&
                worldX - gp.TILE_SIZE < gp.player.worldX + gp.player.screenX &&
                worldY + gp.TILE_SIZE > gp.player.worldY - gp.player.screenY &&
                worldY - gp.TILE_SIZE < gp.player.worldY + gp.player.screenY) {

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

    public SpriteImagesEnum getSpriteDirection() {
        SpriteImagesEnum direction;

        if(currentStatus == Status.IDLING) {
            direction = switch(currentDirection) {
                case Direction.UP, Direction.UP_LEFT, Direction.UP_RIGHT -> SpriteImagesEnum.UP_IDLING;
                case Direction.DOWN, Direction.DOWN_LEFT, Direction.DOWN_RIGHT -> SpriteImagesEnum.DOWN_IDLING;
                case Direction.LEFT -> SpriteImagesEnum.LEFT_IDLING;
                case Direction.RIGHT -> SpriteImagesEnum.RIGHT_IDLING;
            };
        } else {
            direction = switch(currentDirection) {
                case Direction.UP, Direction.UP_LEFT, Direction.UP_RIGHT -> SpriteImagesEnum.UP_MOVING;
                case Direction.DOWN, Direction.DOWN_LEFT, Direction.DOWN_RIGHT -> SpriteImagesEnum.DOWN_MOVING;
                case Direction.LEFT -> SpriteImagesEnum.LEFT_MOVING;
                case Direction.RIGHT -> SpriteImagesEnum.RIGHT_MOVING;
            };
        }
        return direction;
    }
}