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


public class Entity {
    public final int MAX_SPRITES_PER_WALKING_DIRECTION = 4;
    public final int MAX_SPRITES_PER_IDLING_DIRECTION = 2;

    public int updateFramesCounter = 0;

    public int worldX, worldY;
    public int speed;

    public enum spriteDirection {
        UP_MOVING, DOWN_MOVING, LEFT_MOVING, RIGHT_MOVING,
        UP_IDLING, DOWN_IDLING, LEFT_IDLING, RIGHT_IDLING
    };

    public HashMap<spriteDirection, ArrayList<BufferedImage>> spriteImages = new HashMap<>();

    public String currentDirection;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    }

    Utilities utilities = new Utilities();

//    public BufferedImage[] upImages = new BufferedImage[MAX_SPRITES_PER_WALKING_DIRECTION];
//    public BufferedImage[] downImages = new BufferedImage[MAX_SPRITES_PER_WALKING_DIRECTION];
//    public BufferedImage[] leftImages = new BufferedImage[MAX_SPRITES_PER_WALKING_DIRECTION];
//    public BufferedImage[] rightImages = new BufferedImage[MAX_SPRITES_PER_WALKING_DIRECTION];
//
//    public BufferedImage[] idlingDownImages = new BufferedImage[MAX_SPRITES_PER_IDLING_DIRECTION];
//    public BufferedImage[] idlingUpImages = new BufferedImage[MAX_SPRITES_PER_IDLING_DIRECTION];
//    public BufferedImage[] idlingLeftImages = new BufferedImage[MAX_SPRITES_PER_IDLING_DIRECTION];
//    public BufferedImage[] idlingRightImages = new BufferedImage[MAX_SPRITES_PER_IDLING_DIRECTION];




    // The bounding box of the entity and whether it is colliding with another entity.
    public Rectangle boundingBox = new Rectangle(0, 0, 64, 64);
    public int boundingBoxDefaultX, boundingBoxDefaultY;
    public boolean isCollidingWithTile = false;
    public boolean isCollidingWithObject = false;
    public boolean isCollidingWithEntity = false;




    GamePanel gp;
    String[] dialogues = new String[20]; // TODO: Change to HashMap
    int dialogueIndex = 0;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public void setAction() {}
    public void speak() {}

    public void update() {
        setAction();

        isCollidingWithTile = false;
        isCollidingWithEntity = false;
        isCollidingWithObject = false;

        gp.collisionManager.checkTile(this);
        gp.collisionManager.checkPlayer(this);
        gp.collisionManager.checkObject(this, false);

        if(!isCollidingWithTile && !isCollidingWithEntity && !isCollidingWithObject) {
            switch(currentDirection) {
                case "up" -> worldY -= speed;
                case "down" -> worldY += speed;
                case "left" -> worldX -= speed;
                case "right" -> worldX += speed;
                case "up-left" -> {  worldY -= speed; worldX -= speed; }
                case "up-right" -> {  worldY -= speed; worldX += speed;  }
                case "down-left" -> { worldY += speed; worldX -= speed; }
                case "down-right" -> { worldY += speed;  worldX += speed; }
            }


            spriteFramesCounter++;

            spriteFramesCounter++;
            if (spriteFramesCounter > 30) {
                spriteImageNum++;
                if (spriteImageNum > MAX_SPRITES_PER_WALKING_DIRECTION) {
                    spriteImageNum = 1;
                }
                spriteFramesCounter = 0;
            }
        }
    }



    public void setEntityImages(String folderPath, int NUM_WALK_UP, int NUM_WALK_DOWN, int NUM_WALK_LEFT, int NUM_WALK_RIGHT, int NUM_IDLE_UP, int NUM_IDLE_DOWN, int NUM_IDLE_LEFT, int NUM_IDLE_RIGHT) {

        // Initialize hashmap
        for (spriteDirection direction : spriteDirection.values()) {
            spriteImages.put(direction, new ArrayList<>());
        }

        try {
            for(int i = 0; i < NUM_WALK_UP; i++) { spriteImages.get(spriteDirection.UP_MOVING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_up_" + (i+1) + ".png")))); }
            for(int i = 0; i < NUM_WALK_DOWN; i++) { spriteImages.get(spriteDirection.DOWN_MOVING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_down_" + (i+1) + ".png")))); }
            for(int i = 0; i < NUM_WALK_LEFT; i++) { spriteImages.get(spriteDirection.LEFT_MOVING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_left_" + (i+1) + ".png")))); }
            for(int i = 0; i < NUM_WALK_RIGHT; i++) { spriteImages.get(spriteDirection.RIGHT_MOVING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_right_" + (i+1) + ".png")))); }

            for(int i = 0; i < NUM_IDLE_UP; i++) { spriteImages.get(spriteDirection.UP_IDLING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_up_" + (i+1) + ".png")))); }
            for(int i = 0; i < NUM_IDLE_DOWN; i++) { spriteImages.get(spriteDirection.DOWN_IDLING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_down_" + (i+1) + ".png")))); }
            for(int i = 0; i < NUM_IDLE_LEFT; i++) { spriteImages.get(spriteDirection.LEFT_IDLING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_left_" + (i+1) + ".png")))); }
            for(int i = 0; i < NUM_IDLE_RIGHT; i++) { spriteImages.get(spriteDirection.RIGHT_IDLING).add(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_right_" + (i+1) + ".png")))); }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2d) {
        BufferedImage image = null;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.TILE_SIZE > gp.player.worldX - gp.player.screenX &&
                worldX - gp.TILE_SIZE < gp.player.worldX + gp.player.screenX &&
                worldY + gp.TILE_SIZE > gp.player.worldY - gp.player.screenY &&
                worldY - gp.TILE_SIZE < gp.player.worldY + gp.player.screenY) {

            spriteDirection direction = switch (currentDirection) {
                case "up", "up-left", "up-right" -> spriteDirection.UP_MOVING;
                case "down", "down-left", "down-right" -> spriteDirection.DOWN_MOVING;
                case "left" -> spriteDirection.LEFT_MOVING;
                case "right" -> spriteDirection.RIGHT_MOVING;
                case "idling-up", "idling-up-right", "idling-up-left" -> spriteDirection.UP_IDLING;
                case "idling-down", "idling-down-right", "idling-down-left" -> spriteDirection.DOWN_IDLING;
                case "idling-left" -> spriteDirection.LEFT_IDLING;
                case "idling-right" -> spriteDirection.RIGHT_IDLING;
                default -> null;
            };

            if (direction != null) {
                ArrayList<BufferedImage> frames = spriteImages.get(direction);
                if (frames != null && !frames.isEmpty()) {

                    int frameIndex = (spriteImageNum - 1) % frames.size();
                    image = frames.get(frameIndex);
                }
            }

            if (image != null) {
                g2d.drawImage(image, screenX, screenY, null);
            }

            g2d.setColor(Color.RED);
            g2d.drawRect(screenX + boundingBox.x, screenY + boundingBox.y, boundingBox.width, boundingBox.height);
        }
    }


    public int spriteFramesCounter = 0; // Frames that has passed since the last sprite change.
    public int spriteImageNum = 1; // The current sprite image number.










    public void rescaleSprites(int WIDTH, int HEIGHT) {
        for (spriteDirection direction : spriteDirection.values()) {
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


}
