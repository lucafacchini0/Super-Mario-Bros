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
     */
    public void checkTile(Entity entity, boolean isPlayer) {

        /*
         * This calculates the actual coordinates of the bounding box of the entity.
         *
         * Imagine having a player at position worldX = 80, worldY = 38 in the world, and
         * the bounding box of the player being x = 0; y = 0; width = 64; height = 64.
         *
         * EntityLeftWorldX = 80 + 0 = 80 = 80
         * EntityRightWorldX = 80 + 0 + 64 = 144
         *
         * And therefore, the player occupies spaces between x: 80 and x: 144 in the world.
         *
         * The same logic applies to the y-axis.
         */
        int entityLeftWorldX = entity.worldX + entity.boundingBox.x;
        int entityRightWorldX = entity.worldX + entity.boundingBox.x + entity.boundingBox.width - entity.speed;
        int entityTopWorldY = entity.worldY + entity.boundingBox.y;
        int entityBottomWorldY = entity.worldY + entity.boundingBox.y + entity.boundingBox.height - entity.speed;

        /*
         * After having the actual coordinates of the bounding box of the entity, we can calculate
         * the position of the entity in the map. We do so by dividing the entity coordinates by the tile size.
         *
         * This is because, the player entity are in pixels, and the map is in tiles.
         * If the tile size is 64px, and the entity is at x: 70, we need to know in which tile the entity is.
         * We do so by dividing 70 by 64, which gives us 1.09375. We can then round it down to 1, and we know
         * that entity is in the first tile.
         *
         * Remember that, we calculated both the LeftWorldX and RightWorldX (by adding the width of the bounding box)
         * This means that, if the entity is even 1px inside the next tile, it will be considered as being in the next tile.
         *
         * This way, the entity will always have two tiles to check for collision. And since the entity will occupy
         * maximum 1 tile of space, it's impossible that a collision occurs with more than 2 tiles of distance.
         */
        int entityLeftColumn = entityLeftWorldX / gp.TILE_SIZE;
        int entityRightColumn = entityRightWorldX / gp.TILE_SIZE;
        int entityTopRow = entityTopWorldY / gp.TILE_SIZE;
        int entityBottomRow = entityBottomWorldY / gp.TILE_SIZE;

        // Debugging

        /*
         * Based on the current direction of the entity, we recalculate the position of the entity in the map.
         *
         * This is because, if the entity is moving to the right, we need to check prevent the entity from moving
         * "inside" the tile before the collision is detected. This is done by removing the entity's speed from the
         * entity's position in the map.
         *
         * Note: this is fore sure not the best way to handle this, but it works for now.
         * I'll think about a better way to handle this.
         */
        switch(entity.currentDirection) {
            case Entity.Direction.UP -> entityTopRow = (entityTopWorldY - entity.speed) / gp.TILE_SIZE;
            case Entity.Direction.DOWN -> entityBottomRow = (entityBottomWorldY + entity.speed) / gp.TILE_SIZE;
            case Entity.Direction.LEFT -> entityLeftColumn = (entityLeftWorldX - entity.speed) / gp.TILE_SIZE;
            case Entity.Direction.RIGHT -> entityRightColumn = (entityRightWorldX + entity.speed) / gp.TILE_SIZE;
        }

        /*
         * Once we have recalculated the position of the entity in the map, we can check if the entity is colliding
         */
        checkTileCollision(entity, entityLeftColumn, entityRightColumn, entityTopRow, entityBottomRow);
    }

    /**
     * @brief Prepare arrays of tiles to check for collision.
     *
     * This method is always called by the checkTile method.
     * It operates the following way
     *
     * - In the checkTile method, we calculated the position of the entity in the map.
     *   This method now retrieves the tile IDs of the tiles that the entity is "standing on"
     *   and stores them in two arrays: topTiles and bottomTiles.
     *
     * @param entity The entity to check for collision.
     * @param entityLeftColumn The left column of the entity.
     * @param entityRightColumn The right column of the entity.
     * @param entityTopRow The top row of the entity.
     * @param entityBottomRow The bottom row of the entity.
     *
     *
     */
    private void checkTileCollision(Entity entity, int entityLeftColumn, int entityRightColumn, int entityTopRow, int entityBottomRow) {
        int[] topTiles = {
                gp.maps.get(GamePanel.MapType.BACKGROUND).GAME_MAP[entityLeftColumn][entityTopRow],
                gp.maps.get(GamePanel.MapType.BACKGROUND).GAME_MAP[entityRightColumn][entityTopRow]
        };

        int[] bottomTiles = {
                gp.maps.get(GamePanel.MapType.BACKGROUND).GAME_MAP[entityLeftColumn][entityBottomRow],
                gp.maps.get(GamePanel.MapType.BACKGROUND).GAME_MAP[entityRightColumn][entityBottomRow]
        };

        if (isTileColliding(topTiles) || isTileColliding(bottomTiles)) {
            entity.isCollidingWithTile = true;
        }
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
                        entity.boundingBox.y -= entity.speed;
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) index = i;
                        }
                    }

                    case Entity.Direction.DOWN -> {
                        entity.boundingBox.y += entity.speed;
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) index = i;
                        }
                    }

                    case Entity.Direction.LEFT -> {
                        entity.boundingBox.x -= entity.speed;
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) index = i;
                        }
                    }

                    case Entity.Direction.RIGHT -> {
                        entity.boundingBox.x += entity.speed;
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
                        entity.boundingBox.y -= entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                    }

                    case Entity.Direction.DOWN -> {
                        entity.boundingBox.y += entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                    }

                    case Entity.Direction.LEFT -> {
                        entity.boundingBox.x -= entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                    }

                    case Entity.Direction.RIGHT -> {
                        entity.boundingBox.x += entity.speed;
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
                entity.boundingBox.y -= entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
            }

            case Entity.Direction.DOWN -> {
                entity.boundingBox.y += entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
            }

            case Entity.Direction.LEFT -> {
                entity.boundingBox.x -= entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
            }

            case Entity.Direction.RIGHT -> {
                entity.boundingBox.x += entity.speed;
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