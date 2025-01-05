package com.lucafacchini;

import com.lucafacchini.entity.Entity;
import com.lucafacchini.tiles.TileManager;

import java.util.logging.Logger;

/**
 * CollisionManager class
 */
public class CollisionManager {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(CollisionManager.class.getName());

    // GamePanel instance
    GamePanel gp;

    /**
     * @brief Constructor for the CollisionManager class.
     *
     * @param gp The GamePanel instance.
     */
    public CollisionManager(GamePanel gp) {
        this.gp = gp;
    }





    /* TILE COLLISION CHECKING METHODS */



    /**
     * @brief Check if the entity is colliding with a tile from any solid layer.
     *
     * This is the only method is called by the entity's update method. It operates the following way:
     *
     * - It calculates the entity's position in the world. It does so by calculating the coordinates
     *   of its bounding box. (the bounding box as a rectangle that surrounds the entity).
     *
     * - It calculates the entity's position in the map. It does that by dividing the player
     *   coordinates by the tile size, to get a valid index for the map array.
     *
     * - After that, based on the current direction of the entity, it recalculates the position of the
     *   entity in the map. It does so by adding or subtracting the entity's speed to the entity's
     *   This because, otherwise, the player would "step-inside" the tile before the collision is detected.
     *   @note This can be improved. I don't know yet how to, but I'm sure it can be improved, and I'll think about it.
     *
     * @param entity The entity to check for collision. (Player, NPC, etc.)
     * @param isPlayer True if the entity is the player, false otherwise. (NOT USED YET)
     */
    public void checkTile(Entity entity, boolean isPlayer) {
        int entityLeftWorldX = entity.worldX + entity.boundingBox.x;
        int entityRightWorldX = entity.worldX + entity.boundingBox.x + entity.boundingBox.width;
        int entityTopWorldY = entity.worldY + entity.boundingBox.y;
        int entityBottomWorldY = entity.worldY + entity.boundingBox.y + entity.boundingBox.height;

        int entityLeftColumn = entityLeftWorldX / gp.TILE_SIZE;
        int entityRightColumn = entityRightWorldX / gp.TILE_SIZE;
        int entityTopRow = entityTopWorldY / gp.TILE_SIZE;
        int entityBottomRow = entityBottomWorldY / gp.TILE_SIZE;

        switch(entity.currentDirection) {
            case Entity.Direction.UP -> {
                int newTopRow = (entityTopWorldY - entity.speed.getCurrent()) / gp.TILE_SIZE;
                for (int row = entityTopRow; row >= newTopRow; row--) {
                    if (checkTileCollision(entity, entityLeftColumn, entityRightColumn, row, row)) {
                        entity.isCollidingWithTile = true;
                        return;
                    }
                }
            }
            case Entity.Direction.DOWN -> {
                int newBottomRow = (entityBottomWorldY + entity.speed.getCurrent() - 1) / gp.TILE_SIZE;
                for (int row = entityBottomRow; row <= newBottomRow; row++) {
                    if (checkTileCollision(entity, entityLeftColumn, entityRightColumn, row, row)) {
                        entity.isCollidingWithTile = true;
                        return;
                    }
                }
            }
            case Entity.Direction.LEFT -> {
                int newLeftColumn = (entityLeftWorldX - entity.speed.getCurrent()) / gp.TILE_SIZE;
                for (int col = entityLeftColumn; col >= newLeftColumn; col--) {
                    if (checkTileCollision(entity, col, col, entityTopRow, entityBottomRow)) {
                        entity.isCollidingWithTile = true;
                        return;
                    }
                }
            }
            case Entity.Direction.RIGHT -> {
                int newRightColumn = (entityRightWorldX + entity.speed.getCurrent() - 1) / gp.TILE_SIZE;
                for (int col = entityRightColumn; col <= newRightColumn; col++) {
                    if (checkTileCollision(entity, col, col, entityTopRow, entityBottomRow)) {
                        entity.isCollidingWithTile = true;
                        return;
                    }
                }
            }
        }
    }

    private boolean checkTileCollision(Entity entity, int leftColumn, int rightColumn, int topRow, int bottomRow) {
        int[] topTiles = {
                gp.maps.get(GamePanel.MapType.BACKGROUND).GAME_MAP[leftColumn][topRow],
                gp.maps.get(GamePanel.MapType.BACKGROUND).GAME_MAP[rightColumn][topRow]
        };

        int[] bottomTiles = {
                gp.maps.get(GamePanel.MapType.BACKGROUND).GAME_MAP[leftColumn][bottomRow],
                gp.maps.get(GamePanel.MapType.BACKGROUND).GAME_MAP[rightColumn][bottomRow]
        };

        return isTileColliding(topTiles) || isTileColliding(bottomTiles);
    }


    /**
     * @brief Check if the entity is colliding with a tile from any solid layer.
     *
     * @param tileNums The tile IDs to check for collision.
     *
     * @return True if the entity is colliding with a solid tile, false otherwise.
     */
    private boolean isTileColliding(int... tileNums) {
        for (int tileNum : tileNums) {
            if (tileNum >= 0) {
                for (TileManager tileManager : gp.maps.values()) {
                    if (tileManager.isTileSolid(tileNum)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }





    /* OBJECT COLLISION CHECKING METHODS */



    /**
     * @brief Check if the entity is colliding with an object.
     *
     * First of all, it iterates over the objectsArray and checks if there is an object in the current index.
     * (if there is not, it's skipped, for optimization purposes)
     *
     * After that, it calculates the actual coordinates of the bounding box of the entity.
     * Imagine the BoundingBox as some sort of Entity, that has a position in the world.
     * The BoundingBox width and height is already defined in the entity class.
     *
     * Therefore, to "move" the BoundingBox in the world, we need to add the entity's worldX and worldY to the BoundingBox x and y.
     * It's like "attaching" the BoundingBox to the entity. to its x and y.
     *
     * We do the same for the object. We calculate the actual coordinates of the object's bounding box.
     *
     * Then, based on the entity current direction, we "move" the BoundingBox in the world, at the same position of the entity.
     *
     * If at some point, the BoundingBox intersects with the object's BoundingBox, we check if the object is solid,
     * and we set the entity isCollidingWithObject to true. This boolean will be used in the Entity class to
     * prevent the entity from moving in the direction of the collision.
     *
     * @param entity The entity to check for collision.
     */

//TODO check if its solid before (performance)
    public int checkObject(Entity entity, boolean isPlayer) {
        int index = -1;

        for (int i = 0; i < gp.objectsArray.length; i++) {
            if (gp.objectsArray[i] != null) {
                entity.boundingBox.x = entity.worldX + entity.boundingBox.x;
                entity.boundingBox.y = entity.worldY + entity.boundingBox.y;

                gp.objectsArray[i].boundingBox.x = gp.objectsArray[i].worldX + gp.objectsArray[i].boundingBox.x;
                gp.objectsArray[i].boundingBox.y = gp.objectsArray[i].worldY + gp.objectsArray[i].boundingBox.y;


                switch(entity.currentDirection) {
                    case Entity.Direction.UP -> {
                        entity.boundingBox.y -= entity.speed.getCurrent();
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) index = i;
                        }
                    }

                    case Entity.Direction.DOWN -> {
                        entity.boundingBox.y += entity.speed.getCurrent();
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) index = i;
                        }
                    }

                    case Entity.Direction.LEFT -> {
                        entity.boundingBox.x -= entity.speed.getCurrent();
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) index = i;
                        }
                    }

                    case Entity.Direction.RIGHT -> {
                        entity.boundingBox.x += entity.speed.getCurrent();
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) index = i;
                        }
                    }
                }

                entity.boundingBox.x = entity.boundingBoxDefaultX;
                entity.boundingBox.y = entity.boundingBoxDefaultY;
                gp.objectsArray[i].boundingBox.x = gp.objectsArray[i].boundingBoxDefaultX;
                gp.objectsArray[i].boundingBox.y = gp.objectsArray[i].boundingBoxDefaultY;
            }
        }
        return index;
    }





    /* ENTITY COLLISION CHECKING METHODS */



    /**
     * @brief Check if the entity is colliding with an object.
     *
     * First of all, it iterates over the objectsArray and checks if there is an object in the current index.
     * (if there is not, it's skipped, for optimization purposes)
     *
     * After that, it calculates the actual coordinates of the bounding box of the entity.
     * Imagine the BoundingBox as some sort of Entity, that has a position in the world.
     * The BoundingBox width and height is already defined in the entity class.
     *
     * Therefore, to "move" the BoundingBox in the world, we need to add the entity's worldX and worldY to the BoundingBox x and y.
     * It's like "attaching" the BoundingBox to the entity. to its x and y.
     *
     * We do the same for the object. We calculate the actual coordinates of the object's bounding box.
     *
     * Then, based on the entity current direction, we "move" the BoundingBox in the world, at the same position of the entity.
     *
     * If at some point, the BoundingBox intersects with the object's BoundingBox, we check if the object is solid,
     * and we set the entity isCollidingWithObject to true. This boolean will be used in the Entity class to
     * prevent the entity from moving in the direction of the collision.
     *
     * @param entity The entity to check for collision.
     * @param target The array containing every entity
     */
    public int checkEntity(Entity entity, Entity[] target) {
        int index = -1;

        for (int i = 0; i < target.length; i++) {
            if (target[i] != null) {

                entity.boundingBox.x = entity.worldX + entity.boundingBox.x;
                entity.boundingBox.y = entity.worldY + entity.boundingBox.y;

                target[i].boundingBox.x = target[i].worldX + target[i].boundingBox.x;
                target[i].boundingBox.y = target[i].worldY + target[i].boundingBox.y;

                switch(entity.currentDirection) {
                    case Entity.Direction.UP -> {
                        entity.boundingBox.y -= entity.speed.getCurrent();
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                    }

                    case Entity.Direction.DOWN -> {
                        entity.boundingBox.y += entity.speed.getCurrent();
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                    }

                    case Entity.Direction.LEFT -> {
                        entity.boundingBox.x -= entity.speed.getCurrent();
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                    }

                    case Entity.Direction.RIGHT -> {
                        entity.boundingBox.x += entity.speed.getCurrent();
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                    }
                }

                entity.boundingBox.x = entity.boundingBoxDefaultX;
                entity.boundingBox.y = entity.boundingBoxDefaultY;
                target[i].boundingBox.x = target[i].boundingBoxDefaultX;
                target[i].boundingBox.y = target[i].boundingBoxDefaultY;
            }
        }
        return index;
    }





    /* PLAYER COLLISION CHECKING METHODS */



    /**
     * @brief Check if the entity is colliding with an object.
     *
     * First of all, it iterates over the objectsArray and checks if there is an object in the current index.
     * (if there is not, it's skipped, for optimization purposes)
     *
     * After that, it calculates the actual coordinates of the bounding box of the entity.
     * Imagine the BoundingBox as some sort of Entity, that has a position in the world.
     * The BoundingBox width and height is already defined in the entity class.
     *
     * Therefore, to "move" the BoundingBox in the world, we need to add the entity's worldX and worldY to the BoundingBox x and y.
     * It's like "attaching" the BoundingBox to the entity. to its x and y.
     *
     * We do the same for the object. We calculate the actual coordinates of the object's bounding box.
     *
     * Then, based on the entity current direction, we "move" the BoundingBox in the world, at the same position of the entity.
     *
     * If at some point, the BoundingBox intersects with the object's BoundingBox, we check if the object is solid,
     * and we set the entity isCollidingWithObject to true. This boolean will be used in the Entity class to
     * prevent the entity from moving in the direction of the collision.
     *
     * @param entity The entity to check for collision. (Entity won't ever be the player)
     */
    public void checkPlayer(Entity entity) {

        entity.boundingBox.x = entity.worldX + entity.boundingBox.x;
        entity.boundingBox.y = entity.worldY + entity.boundingBox.y;

        gp.player.boundingBox.x = gp.player.worldX + gp.player.boundingBox.x;
        gp.player.boundingBox.y = gp.player.worldY + gp.player.boundingBox.y;

        switch(entity.currentDirection) {
            case Entity.Direction.UP -> {
                entity.boundingBox.y -= entity.speed.getCurrent();
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
            }

            case Entity.Direction.DOWN -> {
                entity.boundingBox.y += entity.speed.getCurrent();
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
            }

            case Entity.Direction.LEFT -> {
                entity.boundingBox.x -= entity.speed.getCurrent();
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
            }

            case Entity.Direction.RIGHT -> {
                entity.boundingBox.x += entity.speed.getCurrent();
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
            }
        }

        entity.boundingBox.x = entity.boundingBoxDefaultX;
        entity.boundingBox.y = entity.boundingBoxDefaultY;
        gp.player.boundingBox.x = gp.player.boundingBoxDefaultX;
        gp.player.boundingBox.y = gp.player.boundingBoxDefaultY;
    }

    public void isNextToPlayer(Entity entity) {

        entity.boundingBox.x = entity.worldX + entity.boundingBox.x - gp.TILE_SIZE / 2;
        entity.boundingBox.y = entity.worldY + entity.boundingBox.y - gp.TILE_SIZE / 2;
        entity.boundingBox.width = entity.boundingBox.width + gp.TILE_SIZE;
        entity.boundingBox.height = entity.boundingBox.height + gp.TILE_SIZE;

        gp.player.boundingBox.x = gp.player.worldX + gp.player.boundingBox.x;
        gp.player.boundingBox.y = gp.player.worldY + gp.player.boundingBox.y;

        if (entity.boundingBox.intersects(gp.player.boundingBox)) {
            entity.isNextToPlayer = true;
        }

        entity.boundingBox.x = entity.boundingBoxDefaultX;
        entity.boundingBox.y = entity.boundingBoxDefaultY;
        entity.boundingBox.width = entity.boundingBoxDefaultWidth;
        entity.boundingBox.height = entity.boundingBoxDefaultHeight;

        gp.player.boundingBox.x = gp.player.boundingBoxDefaultX;
        gp.player.boundingBox.y = gp.player.boundingBoxDefaultY;
    }
}