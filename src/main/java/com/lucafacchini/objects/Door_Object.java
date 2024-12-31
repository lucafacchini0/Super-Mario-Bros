package com.lucafacchini.objects;

import com.lucafacchini.GamePanel;
import com.lucafacchini.Utilities;

import javax.imageio.ImageIO;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Door_Object class
 */
public class Door_Object extends SuperObject {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(Door_Object.class.getName());

    /**
     * @brief Constructor for the Door_Object class.
     * @param gp The GamePanel instance.
     * @param utilities to rescale the image (call the rescaleImage method).
     */
    public Door_Object(GamePanel gp, Utilities utilities) {

        objectType = ObjectType.DOOR;

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/door.png")));
            image = utilities.rescaleImage(image, gp.TILE_SIZE, gp.TILE_SIZE);
        } catch (Exception e) {
            LOGGER.severe("Error loading door image: " + e.getMessage());
        }
        isSolid = true;
    }
}