package com.lucafacchini.objects;

import com.lucafacchini.Utilities;
import com.lucafacchini.GamePanel;

import javax.imageio.ImageIO;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Key_Object class
 */
public class Key_Object extends SuperObject {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(Key_Object.class.getName());

    /**
     * @brief Constructor for the Key_Object class.
     * @param gp The GamePanel instance.
     * @param utilities to rescale the image (call the rescaleImage method).
     */
    public Key_Object(GamePanel gp, Utilities utilities) {

        objectType = ObjectType.KEY;

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/key.png")));
            image = utilities.rescaleImage(image, gp.TILE_SIZE, gp.TILE_SIZE);
        } catch (Exception e) {
            LOGGER.severe("Error loading key image: " + e.getMessage());
        }
        isSolid = true;
    }
}