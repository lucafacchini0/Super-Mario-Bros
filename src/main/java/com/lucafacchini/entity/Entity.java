package com.lucafacchini.entity;

import com.lucafacchini.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

// TODO: move similar code of the entities to this class

public class Entity {
    // INFO: Every entity must have exactly the sprites per direction specified in the constants.
    // TODO: For each entity, specify the number of sprites per direction, so that every entity can have a different number of sprites per direction.
    public final int MAX_SPRITES_PER_WALKING_DIRECTION = 2;
    public final int MAX_SPRITES_PER_IDLING_DIRECTION = 2;

    public int updateFramesCounter = 0;

    public int worldX, worldY;
    public int speed;

    public BufferedImage[] upImages = new BufferedImage[MAX_SPRITES_PER_WALKING_DIRECTION];
    public BufferedImage[] downImages = new BufferedImage[MAX_SPRITES_PER_WALKING_DIRECTION];
    public BufferedImage[] leftImages = new BufferedImage[MAX_SPRITES_PER_WALKING_DIRECTION];
    public BufferedImage[] rightImages = new BufferedImage[MAX_SPRITES_PER_WALKING_DIRECTION];

    public BufferedImage[] idlingDownImages = new BufferedImage[MAX_SPRITES_PER_IDLING_DIRECTION];
    public BufferedImage[] idlingUpImages = new BufferedImage[MAX_SPRITES_PER_IDLING_DIRECTION];
    public BufferedImage[] idlingLeftImages = new BufferedImage[MAX_SPRITES_PER_IDLING_DIRECTION];
    public BufferedImage[] idlingRightImages = new BufferedImage[MAX_SPRITES_PER_IDLING_DIRECTION];

    public String currentDirection;

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



    public void draw(Graphics2D g2d) {
        BufferedImage image = null;


        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // If the object is within the screen boundaries, draw it.
        if (worldX + gp.TILE_SIZE > gp.player.worldX - gp.player.screenX &&
                worldX - gp.TILE_SIZE < gp.player.worldX + gp.player.screenX &&
                worldY + gp.TILE_SIZE > gp.player.worldY - gp.player.screenY &&
                worldY - gp.TILE_SIZE < gp.player.worldY + gp.player.screenY) {

            switch (currentDirection) {
                case "up", "up-left", "up-right" -> image = switch (spriteImageNum) {
                    case 1 -> upImages[0];
                    case 2 -> upImages[1];
                    case 3 -> upImages[2];
                    case 4 -> upImages[3];
                    case 5 -> upImages[4];
                    case 6 -> upImages[5];
                    default -> upImages[0];
                };

                case "down", "down-left", "down-right" -> image = switch (spriteImageNum) {
                    case 1 -> downImages[0];
                    case 2 -> downImages[1];
                    case 3 -> downImages[2];
                    case 4 -> downImages[3];
                    case 5 -> downImages[4];
                    case 6 -> downImages[5];
                    default -> downImages[0];
                };

                case "left" -> image = switch (spriteImageNum) {
                    case 1 -> leftImages[0];
                    case 2 -> leftImages[1];
                    case 3 -> leftImages[2];
                    case 4 -> leftImages[3];
                    case 5 -> leftImages[4];
                    case 6 -> leftImages[5];
                    default -> leftImages[0];
                };

                case "right" -> image = switch (spriteImageNum) {
                    case 1 -> rightImages[0];
                    case 2 -> rightImages[1];
                    case 3 -> rightImages[2];
                    case 4 -> rightImages[3];
                    case 5 -> rightImages[4];
                    case 6 -> rightImages[5];
                    default -> rightImages[0];
                };

                case "idling-up", "idling-up-right", "idling-up-left" -> image = switch (spriteImageNum) {
                    case 1 -> idlingUpImages[0];
                    case 2 -> idlingUpImages[1];
                    case 3 -> idlingUpImages[2];
                    case 4 -> idlingUpImages[3];
                    default -> idlingUpImages[0];
                };

                case "idling-down", "idling-down-right", "idling-down-left" -> image = switch (spriteImageNum) {
                    case 1 -> idlingDownImages[0];
                    case 2 -> idlingDownImages[1];
                    case 3 -> idlingDownImages[2];
                    case 4 -> idlingDownImages[3];
                    default -> idlingDownImages[0];
                };

                case "idling-left" -> image = switch (spriteImageNum) {
                    case 1 -> idlingLeftImages[0];
                    case 2 -> idlingLeftImages[1];
                    case 3 -> idlingLeftImages[2];
                    case 4 -> idlingLeftImages[3];
                    default -> idlingLeftImages[0];
                };

                case "idling-right" -> image = switch (spriteImageNum) {
                    case 1 -> idlingRightImages[0];
                    case 2 -> idlingRightImages[1];
                    case 3 -> idlingRightImages[2];
                    case 4 -> idlingRightImages[3];
                    default -> idlingRightImages[0];
                };

                default -> image = null;
            }

            if (image != null) {
                g2d.drawImage(image, screenX, screenY, null);

                // Debug
                g2d.setColor(Color.RED);
                g2d.drawRect(screenX + boundingBox.x, screenY + boundingBox.y, boundingBox.width, boundingBox.height);
            }

            g2d.drawImage(image, screenX, screenY, null);
        }

        //Debug ##IMPORTANT
        g2d.setColor(Color.BLACK);
        g2d.drawRect(screenX + boundingBox.x, screenY + boundingBox.y, boundingBox.width, boundingBox.height);

    }

    public int spriteFramesCounter = 0; // Frames that has passed since the last sprite change.
    public int spriteImageNum = 1; // The current sprite image number.

    public void getImages(String folderPath) {
        try {
            for(int i = 0; i < MAX_SPRITES_PER_WALKING_DIRECTION; i++) {
                upImages[i] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_up_" + (i+1) + ".png")));
                downImages[i] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_down_" + (i+1) + ".png")));
                leftImages[i] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_left_" + (i+1) + ".png")));
                rightImages[i] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/walk_right_" + (i+1) + ".png")));
            }

            for(int i = 0; i < MAX_SPRITES_PER_IDLING_DIRECTION; i++) {
                idlingUpImages[i] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_up_" + (i+1) + ".png")));
                idlingDownImages[i] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_down_" + (i+1) + ".png")));
                idlingLeftImages[i] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_left_" + (i+1) + ".png")));
                idlingRightImages[i] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/" + folderPath + "/idling/idling_right_" + (i+1) + ".png")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
