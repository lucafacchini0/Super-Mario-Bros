package com.lucafacchini;

// Objects
import com.lucafacchini.objects.Boots_Object;
import com.lucafacchini.objects.Chest_Object;
import com.lucafacchini.objects.Door_Object;
import com.lucafacchini.objects.Key_Object;

// Entities
import com.lucafacchini.entity.NPC_OldMan;

import java.util.logging.Logger;

/**
 * AssetSetter class
 * This class is used to place objects and NPCs in the game.
 */
public class AssetSetter {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(AssetSetter.class.getName());

    // GamePanel instance
    GamePanel gp;

    /**
     * @brief Constructor for the AssetSetter class.
     * @param gp The GamePanel instance.
     */
    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * @brief Place objects in the game.
     */
    public void placeObject() {
        gp.objectsArray[0] = new Key_Object(gp, new Utilities());
        gp.objectsArray[0].worldX = 9 * gp.TILE_SIZE;
        gp.objectsArray[0].worldY = 14 * gp.TILE_SIZE;

        gp.objectsArray[1] = new Key_Object(gp, new Utilities());
        gp.objectsArray[1].worldX = 18 * gp.TILE_SIZE;
        gp.objectsArray[1].worldY = 8 * gp.TILE_SIZE;

        gp.objectsArray[2] = new Door_Object(gp, new Utilities());
        gp.objectsArray[2].worldX = 23 * gp.TILE_SIZE;
        gp.objectsArray[2].worldY = 34 * gp.TILE_SIZE;

        gp.objectsArray[3] = new Boots_Object(gp, new Utilities());
        gp.objectsArray[3].worldX = 30 * gp.TILE_SIZE;
        gp.objectsArray[3].worldY = 31 * gp.TILE_SIZE;

        gp.objectsArray[4] = new Chest_Object(gp, new Utilities());
        gp.objectsArray[4].worldX = 23 * gp.TILE_SIZE;
        gp.objectsArray[4].worldY = 36 * gp.TILE_SIZE;
    }

    /**
     * @brief Place NPCs in the game.
     */
    public void placeNPC() {
        gp.npcArray[0] = new NPC_OldMan(gp);
    }
}