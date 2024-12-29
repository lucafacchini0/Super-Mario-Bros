package com.lucafacchini.objects;

import com.lucafacchini.Utilities;
import com.lucafacchini.GamePanel;

import javax.imageio.ImageIO;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Boots_Object class
 */
public class Boots_Object extends SuperObject {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(Boots_Object.class.getName());

    /**
     * @brief Constructor for the Boots_Object class.
     * @param gp The GamePanel instance.
     * @param utilities to rescale the image (call the rescaleImage method).
     */
    public Boots_Object(GamePanel gp, Utilities utilities) {

        objectType = ObjectType.BOOTS;

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/boots.png")));
            image = utilities.rescaleImage(image, gp.TILE_SIZE, gp.TILE_SIZE);
        } catch (Exception e) {
            LOGGER.severe("Error loading boots image: " + e.getMessage());
        }
    }
}