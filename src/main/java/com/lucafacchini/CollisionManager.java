package com.lucafacchini;

import com.lucafacchini.entity.Entity;
import com.lucafacchini.objects.SuperObject;
import com.lucafacchini.tiles.TileManager;

public class CollisionManager {

    GamePanel gp;

    // ------------------- Constructor -------------------

    public CollisionManager(GamePanel gp) {
        this.gp = gp;
    }

    // ------------------- Utility methods -------------------

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

    private boolean isTileSolid(int tileNum, TileManager layer) {
        return layer != null && layer.tileMap.get(tileNum) != null && layer.tileMap.get(tileNum).isSolid;
    }

    private void resetEntityBoundingBox(Entity entity) {
        entity.boundingBox.x = entity.boundingBoxDefaultX;
        entity.boundingBox.y = entity.boundingBoxDefaultY;
    }

    private void resetObjectBoundingBox(SuperObject object) {
        object.boundingBox.x = object.boundingBoxDefaultX;
        object.boundingBox.y = object.boundingBoxDefaultY;
    }


    // ------------------- Collision methods -------------------

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



    // ------------------- Directional collision methods -------------------

    public void checkTile(Entity entity) {
//        int entityLeftWorldX = entity.worldX + entity.boundingBox.x;
//        int entityRightWorldX = entity.worldX + entity.boundingBox.x + entity.boundingBox.width;
//        int entityTopWorldY = entity.worldY + entity.boundingBox.y;
//        int entityBottomWorldY = entity.worldY + entity.boundingBox.y + entity.boundingBox.height;
//
//        int entityLeftColumn = entityLeftWorldX / gp.TILE_SIZE;
//        int entityRightColumn = entityRightWorldX / gp.TILE_SIZE;
//        int entityTopRow = entityTopWorldY / gp.TILE_SIZE;
//        int entityBottomRow = entityBottomWorldY / gp.TILE_SIZE;
//
//        switch (entity.currentDirection) {
//            case "up":
//                entityTopRow = (entityTopWorldY - entity.speed) / gp.TILE_SIZE;
//                break;
//
//            case "down":
//                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.TILE_SIZE;
//                break;
//
//            case "left":
//                entityLeftColumn = (entityLeftWorldX - entity.speed) / gp.TILE_SIZE;
//                break;
//
//            case "right":
//                entityRightColumn = (entityRightWorldX + entity.speed) / gp.TILE_SIZE;
//                break;
//
//            case "up-left":
//                entityTopRow = (entityTopWorldY - entity.speed) / gp.TILE_SIZE;
//                entityLeftColumn = (entityLeftWorldX - entity.speed) / gp.TILE_SIZE;
//                break;
//
//            case "up-right":
//                entityTopRow = (entityTopWorldY - entity.speed) / gp.TILE_SIZE;
//                entityRightColumn = (entityRightWorldX + entity.speed) / gp.TILE_SIZE;
//                break;
//
//            case "down-left":
//                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.TILE_SIZE;
//                entityLeftColumn = (entityLeftWorldX - entity.speed) / gp.TILE_SIZE;
//                break;
//
//            case "down-right":
//                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.TILE_SIZE;
//                entityRightColumn = (entityRightWorldX + entity.speed) / gp.TILE_SIZE;
//                break;
//        }
//        checkTileCollision(entity, entityLeftColumn, entityRightColumn, entityTopRow, entityBottomRow);
    }



    // ------------------- Side collision methods -------------------

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



    // ------------------- Object collision methods -------------------

    // TODO: Fix this method. Integrate with handleCollisionWithObject method in Player class.

    // IMPORTANT TODO: Uncomment and remove return -1
    public int checkObject(Entity entity, boolean isPlayer) {
        int index = -1;

        for (int i = 0; i < gp.objectsArray.length; i++) {
            if (gp.objectsArray[i] != null) {
                entity.boundingBox.x = entity.worldX + entity.boundingBox.x;
                entity.boundingBox.y = entity.worldY + entity.boundingBox.y;

                gp.objectsArray[i].boundingBox.x = gp.objectsArray[i].worldX + gp.objectsArray[i].boundingBox.x;
                gp.objectsArray[i].boundingBox.y = gp.objectsArray[i].worldY + gp.objectsArray[i].boundingBox.y;

                switch (entity.currentDirection) {
                    case "up-left":
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

                    case "up-right":
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

                    case "down-left":
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

                    case "down-right":
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

                    case "up":
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

                    case "down":
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

                    case "left":
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

                    case "right":
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

         return -1;
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
                    case "up-left":
                        entity.boundingBox.x -= entity.speed;
                        entity.boundingBox.y -= entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case "up-right":
                        entity.boundingBox.x += entity.speed;
                        entity.boundingBox.y -= entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case "down-left":
                        entity.boundingBox.x -= entity.speed;
                        entity.boundingBox.y += entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case "down-right":
                        entity.boundingBox.x += entity.speed;
                        entity.boundingBox.y += entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case "up":
                        entity.boundingBox.y -= entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case "down":
                        entity.boundingBox.y += entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case "left":
                        entity.boundingBox.x -= entity.speed;
                        if (entity.boundingBox.intersects(target[i].boundingBox)) {
                            index = i;
                            entity.isCollidingWithEntity = true;
                        }
                        break;

                    case "right":
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
            case "up-left":
                entity.boundingBox.x -= entity.speed;
                entity.boundingBox.y -= entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {

                    entity.isCollidingWithEntity = true;
                }
                break;

            case "up-right":
                entity.boundingBox.x += entity.speed;
                entity.boundingBox.y -= entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;

            case "down-left":
                entity.boundingBox.x -= entity.speed;
                entity.boundingBox.y += entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;

            case "down-right":
                entity.boundingBox.x += entity.speed;
                entity.boundingBox.y += entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;

            case "up":
                entity.boundingBox.y -= entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;

            case "down":
                entity.boundingBox.y += entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;

            case "left":
                entity.boundingBox.x -= entity.speed;
                if (entity.boundingBox.intersects(gp.player.boundingBox)) {
                    entity.isCollidingWithEntity = true;
                }
                break;

            case "right":
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