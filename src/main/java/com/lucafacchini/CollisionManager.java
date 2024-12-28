package com.lucafacchini;

import com.lucafacchini.entity.Entity;
import com.lucafacchini.objects.SuperObject;
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


    /**
     * @brief Check if the entity is colliding with a tile from any solid layer.
     *
     * @param tileNums The tile IDs to check for collision.
     *
     * @return True if the entity is colliding with a solid tile, false otherwise.
     */
    // TODO: Refactor this to make it work dynamically with maps.
    private boolean isTileColliding(int... tileNums) {
        for (int tileNum : tileNums) {
            if (tileNum >= 0) {
                if(isTileSolid(tileNum, gp.maps[0])) { return true; }
                if(isTileSolid(tileNum, gp.maps[1])) { return true; }
                if(isTileSolid(tileNum, gp.maps[2])) { return true; }
            }
        }
        return false;
    }


    /**
     * @brief Check if the entity is colliding with a tile from any solid layer.
     *
     * @param tileNum The tile IDs to check for collision.
     *
     * @return True if the entity is colliding with a solid tile, false otherwise.
     */
    private boolean isTileSolid(int tileNum, TileManager layer) {
        return layer != null && layer.tileMap.get(tileNum) != null && layer.tileMap.get(tileNum).isSolid;
    }


    /**
     * @brief Check if the entity is colliding with a tile from any solid layer.
     *
     * First of all, it stores the tile IDs of the top and bottom tiles.
     * Then, it checks if the entity is colliding with any of the tiles.
     *
     * @param entity The entity to check for collision.
     * @param entityLeftColumn The left column of the entity.
     * @param entityRightColumn The right column of the entity.
     * @param entityTopRow The top row of the entity.
     * @param entityBottomRow The bottom row of the entity.
     *
     * TODO: This has to completely be redone to make it work dynamically with maps.
     */
    private void checkTileCollision(Entity entity, int entityLeftColumn, int entityRightColumn, int entityTopRow, int entityBottomRow) {
        int[] topTiles = {
                gp.maps[0].GAME_MAP[entityLeftColumn][entityTopRow],
                gp.maps[0].GAME_MAP[entityRightColumn][entityTopRow],
                gp.maps[1].GAME_MAP[entityLeftColumn][entityTopRow],
                gp.maps[1].GAME_MAP[entityRightColumn][entityTopRow],
                gp.maps[2].GAME_MAP[entityLeftColumn][entityTopRow],
                gp.maps[2].GAME_MAP[entityRightColumn][entityTopRow]
        };

        int[] bottomTiles = {
                gp.maps[0].GAME_MAP[entityLeftColumn][entityBottomRow],
                gp.maps[0].GAME_MAP[entityRightColumn][entityBottomRow],
                gp.maps[1].GAME_MAP[entityLeftColumn][entityBottomRow],
                gp.maps[1].GAME_MAP[entityRightColumn][entityBottomRow],
                gp.maps[2].GAME_MAP[entityLeftColumn][entityBottomRow],
                gp.maps[2].GAME_MAP[entityRightColumn][entityBottomRow]
        };

        if (isTileColliding(topTiles) || isTileColliding(bottomTiles)) {
            entity.isCollidingWithTile = true;
        }
    }

    /**
     * @brief Check if the entity is colliding with a tile from any solid layer.
     *
     * First of all, it calculates the world coordinates of the entity. (Like, the "edges" of the entity)
     * Then, it calculates the column and row of the entity, so that it can check the surrounding tiles.
     * (it does this so that it can refer to the file map and check if the entity is colliding with any solid tile)
     *
     * Then, it calls the checkTileCollision method to check if the entity is colliding with any solid tile.
     *
     * TODO: This has to completely be redone to make it work dynamically with maps.
     *
     * @param entity The entity to check for collision.
     */
    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.worldX + entity.boundingBox.x;
        int entityRightWorldX = entity.worldX + entity.boundingBox.x + entity.boundingBox.width;
        int entityTopWorldY = entity.worldY + entity.boundingBox.y;
        int entityBottomWorldY = entity.worldY + entity.boundingBox.y + entity.boundingBox.height;

        int entityLeftColumn = entityLeftWorldX / gp.TILE_SIZE;
        int entityRightColumn = entityRightWorldX / gp.TILE_SIZE;
        int entityTopRow = entityTopWorldY / gp.TILE_SIZE;
        int entityBottomRow = entityBottomWorldY / gp.TILE_SIZE;

        switch (entity.currentDirection) {
            case Entity.Direction.UP:
                entityTopRow = (entityTopWorldY - entity.speed) / gp.TILE_SIZE;
                break;

            case Entity.Direction.DOWN:
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.TILE_SIZE;
                break;

            case Entity.Direction.LEFT:
                entityLeftColumn = (entityLeftWorldX - entity.speed) / gp.TILE_SIZE;
                break;

            case Entity.Direction.RIGHT:
                entityRightColumn = (entityRightWorldX + entity.speed) / gp.TILE_SIZE;
                break;

            case Entity.Direction.UP_LEFT:
                entityTopRow = (entityTopWorldY - entity.speed) / gp.TILE_SIZE;
                entityLeftColumn = (entityLeftWorldX - entity.speed) / gp.TILE_SIZE;
                break;

            case Entity.Direction.UP_RIGHT:
                entityTopRow = (entityTopWorldY - entity.speed) / gp.TILE_SIZE;
                entityRightColumn = (entityRightWorldX + entity.speed) / gp.TILE_SIZE;
                break;

            case Entity.Direction.DOWN_LEFT:
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.TILE_SIZE;
                entityLeftColumn = (entityLeftWorldX - entity.speed) / gp.TILE_SIZE;
                break;

            case Entity.Direction.DOWN_RIGHT:
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.TILE_SIZE;
                entityRightColumn = (entityRightWorldX + entity.speed) / gp.TILE_SIZE;
                break;
        }
        checkTileCollision(entity, entityLeftColumn, entityRightColumn, entityTopRow, entityBottomRow);
    }


    /**
     * @brief Check if it's colliding with a tile from the left.
     * @param entity The entity to check for collision.
     * @return True if the entity is colliding with a tile from the left, false otherwise.
     */
    public boolean isCollidingFromLeft(Entity entity) {
        int entityLeftWorldX = entity.worldX + entity.boundingBox.x;
        int nextLeftWorldX = entityLeftWorldX - entity.speed;

        int leftTile = nextLeftWorldX / gp.TILE_SIZE;
        int topTile = (entity.worldY + entity.boundingBox.y) / gp.TILE_SIZE;
        int bottomTile = (entity.worldY + entity.boundingBox.y + entity.boundingBox.height) / gp.TILE_SIZE;

        return isTileColliding(
                gp.maps[0].GAME_MAP[leftTile][topTile], gp.maps[0].GAME_MAP[leftTile][bottomTile],
                gp.maps[1].GAME_MAP[leftTile][topTile], gp.maps[1].GAME_MAP[leftTile][bottomTile],
                gp.maps[2].GAME_MAP[leftTile][topTile], gp.maps[2].GAME_MAP[leftTile][bottomTile]
        );
    }


    /**
     * @brief Check if it's colliding with a tile from the right.
     * @param entity The entity to check for collision.
     * @return True if the entity is colliding with a tile from the right, false otherwise.
     */
    public boolean isCollidingFromRight(Entity entity) {
        int entityRightWorldX = entity.worldX + entity.boundingBox.x + entity.boundingBox.width;
        int nextRightWorldX = entityRightWorldX + entity.speed;

        int rightTile = nextRightWorldX / gp.TILE_SIZE;
        int topTile = (entity.worldY + entity.boundingBox.y) / gp.TILE_SIZE;
        int bottomTile = (entity.worldY + entity.boundingBox.y + entity.boundingBox.height) / gp.TILE_SIZE;

        return isTileColliding(
                gp.maps[0].GAME_MAP[rightTile][topTile], gp.maps[0].GAME_MAP[rightTile][bottomTile],
                gp.maps[1].GAME_MAP[rightTile][topTile], gp.maps[1].GAME_MAP[rightTile][bottomTile],
                gp.maps[2].GAME_MAP[rightTile][topTile], gp.maps[2].GAME_MAP[rightTile][bottomTile]
        );
    }


    /**
     * @brief Check if it's colliding with a tile from the bottom.
     * @param entity The entity to check for collision.
     * @return True if the entity is colliding with a tile from the bottom, false otherwise.
     */
    public boolean isCollidingFromBottom(Entity entity) {
        int entityBottomWorldY = entity.worldY + entity.boundingBox.y + entity.boundingBox.height;
        int nextBottomWorldY = entityBottomWorldY + entity.speed;

        int leftTile = (entity.worldX + entity.boundingBox.x) / gp.TILE_SIZE;
        int rightTile = (entity.worldX + entity.boundingBox.x + entity.boundingBox.width) / gp.TILE_SIZE;
        int bottomTile = nextBottomWorldY / gp.TILE_SIZE;

        return isTileColliding(
                gp.maps[0].GAME_MAP[leftTile][bottomTile], gp.maps[0].GAME_MAP[rightTile][bottomTile],
                gp.maps[1].GAME_MAP[leftTile][bottomTile], gp.maps[1].GAME_MAP[rightTile][bottomTile],
                gp.maps[2].GAME_MAP[leftTile][bottomTile], gp.maps[2].GAME_MAP[rightTile][bottomTile]
        );
    }


    /**
     * @brief Check if it's colliding with a tile from the top.
     * @param entity The entity to check for collision.
     * @return True if the entity is colliding with a tile from the top, false otherwise.
     */
    public boolean isCollidingFromTop(Entity entity) {
        int entityTopWorldY = entity.worldY + entity.boundingBox.y;
        int nextTopWorldY = entityTopWorldY - entity.speed;

        int leftTile = (entity.worldX + entity.boundingBox.x) / gp.TILE_SIZE;
        int rightTile = (entity.worldX + entity.boundingBox.x + entity.boundingBox.width) / gp.TILE_SIZE;
        int topTile = nextTopWorldY / gp.TILE_SIZE;

        return isTileColliding(
                gp.maps[0].GAME_MAP[leftTile][topTile], gp.maps[0].GAME_MAP[rightTile][topTile],
                gp.maps[1].GAME_MAP[leftTile][topTile], gp.maps[1].GAME_MAP[rightTile][topTile],
                gp.maps[2].GAME_MAP[leftTile][topTile], gp.maps[2].GAME_MAP[rightTile][topTile]
        );
    }





    /**
     * @brief Check if the entity is colliding with an object.
     *
     * First of all, it iterates over the objectsArray and checks if the entity is colliding with any object.
     *
     * @param entity The entity to check for collision.
     */
    // TODO: Fix this method. Integrate with handleCollisionWithObject method in Player class.
    public int checkObject(Entity entity, boolean isPlayer) {
        int index = -1;

        for (int i = 0; i < gp.objectsArray.length; i++) {
            if (gp.objectsArray[i] != null) {
                entity.boundingBox.x = entity.worldX + entity.boundingBox.x;
                entity.boundingBox.y = entity.worldY + entity.boundingBox.y;

                gp.objectsArray[i].boundingBox.x = gp.objectsArray[i].worldX + gp.objectsArray[i].boundingBox.x;
                gp.objectsArray[i].boundingBox.y = gp.objectsArray[i].worldY + gp.objectsArray[i].boundingBox.y;

                switch (entity.currentDirection) {
                    case Entity.Direction.UP_LEFT:
                        entity.boundingBox.x -= entity.speed;
                        entity.boundingBox.y -= entity.speed;
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) {
                                index = i;
                            }
                        }
                        break;

                    case Entity.Direction.UP_RIGHT:
                        entity.boundingBox.x += entity.speed;
                        entity.boundingBox.y -= entity.speed;
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) {
                                index = i;
                            }
                        }
                        break;

                    case Entity.Direction.DOWN_LEFT:
                        entity.boundingBox.x -= entity.speed;
                        entity.boundingBox.y += entity.speed;
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) {
                                index = i;
                            }
                        }
                        break;

                    case Entity.Direction.DOWN_RIGHT:
                        entity.boundingBox.x += entity.speed;
                        entity.boundingBox.y += entity.speed;
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) {
                                index = i;
                            }
                        }
                        break;

                    case Entity.Direction.UP:
                        entity.boundingBox.y -= entity.speed;
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) {
                                index = i;
                            }
                        }
                        break;

                    case Entity.Direction.DOWN:
                        entity.boundingBox.y += entity.speed;
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) {
                                index = i;
                            }
                        }
                        break;

                    case Entity.Direction.LEFT:
                        entity.boundingBox.x -= entity.speed;
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) {
                                index = i;
                            }
                        }
                        break;

                    case Entity.Direction.RIGHT:
                        entity.boundingBox.x += entity.speed;
                        if (entity.boundingBox.intersects(gp.objectsArray[i].boundingBox)) {
                            if (gp.objectsArray[i].isSolid) {
                                entity.isCollidingWithObject = true;
                            }
                            if (isPlayer) {
                                index = i;
                            }
                        }
                        break;
                }
                entity.boundingBox.x = entity.boundingBoxDefaultX;
                entity.boundingBox.y = entity.boundingBoxDefaultY;
                gp.objectsArray[i].boundingBox.x = gp.objectsArray[i].boundingBoxDefaultX;
                gp.objectsArray[i].boundingBox.y = gp.objectsArray[i].boundingBoxDefaultY;
            }
        }
        return index;
    }

    public int checkEntity(Entity entity, Entity[] target) {
        int index = -1;

        for (int i = 0; i < target.length; i++) {
            if (target[i] != null) {
                entity.boundingBox.x = entity.worldX + entity.boundingBox.x;
                entity.boundingBox.y = entity.worldY + entity.boundingBox.y;

                target[i].boundingBox.x = target[i].worldX + target[i].boundingBox.x;
                target[i].boundingBox.y = target[i].worldY + target[i].boundingBox.y;

                switch (entity.currentDirection) {
                    case Entity.Direction.UP_LEFT:
                        entity.boundingBox.x -= entity.speed;
                        entity.boundingBox.y -= entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case Entity.Direction.UP_RIGHT:
                        entity.boundingBox.x += entity.speed;
                        entity.boundingBox.y -= entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case Entity.Direction.DOWN_LEFT:
                        entity.boundingBox.x -= entity.speed;
                        entity.boundingBox.y += entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case Entity.Direction.DOWN_RIGHT:
                        entity.boundingBox.x += entity.speed;
                        entity.boundingBox.y += entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case Entity.Direction.UP:
                        entity.boundingBox.y -= entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case Entity.Direction.DOWN:
                        entity.boundingBox.y += entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case Entity.Direction.LEFT:
                        entity.boundingBox.x -= entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case Entity.Direction.RIGHT:
                        entity.boundingBox.x += entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;
                }
                entity.boundingBox.x = entity.boundingBoxDefaultX;
                entity.boundingBox.y = entity.boundingBoxDefaultY;
                target[i].boundingBox.x = target[i].boundingBoxDefaultX;
                target[i].boundingBox.y = target[i].boundingBoxDefaultY;
            }
        }
        return index;
    }

    public void checkPlayer(Entity entity) {
        entity.boundingBox.x = entity.worldX + entity.boundingBox.x;
        entity.boundingBox.y = entity.worldY + entity.boundingBox.y;

        gp.player.boundingBox.x = gp.player.worldX + gp.player.boundingBox.x;
        gp.player.boundingBox.y = gp.player.worldY + gp.player.boundingBox.y;

        switch (entity.currentDirection) {
            case Entity.Direction.UP_LEFT:
                entity.boundingBox.x -= entity.speed;
                entity.boundingBox.y -= entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {

                    entity.isCollidingWithEntity = true;
                }
                break;

            case Entity.Direction.UP_RIGHT:
                entity.boundingBox.x += entity.speed;
                entity.boundingBox.y -= entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;

            case Entity.Direction.DOWN_LEFT:
                entity.boundingBox.x -= entity.speed;
                entity.boundingBox.y += entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;

            case Entity.Direction.DOWN_RIGHT:
                entity.boundingBox.x += entity.speed;
                entity.boundingBox.y += entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;

            case Entity.Direction.UP:
                entity.boundingBox.y -= entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;

            case Entity.Direction.DOWN:
                entity.boundingBox.y += entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;

            case Entity.Direction.LEFT:
                entity.boundingBox.x -= entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;

            case Entity.Direction.RIGHT:
                entity.boundingBox.x += entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;
        }
        entity.boundingBox.x = entity.boundingBoxDefaultX;
        entity.boundingBox.y = entity.boundingBoxDefaultY;
        gp.player.boundingBox.x = gp.player.boundingBoxDefaultX;
        gp.player.boundingBox.y = gp.player.boundingBoxDefaultY;
    }
}