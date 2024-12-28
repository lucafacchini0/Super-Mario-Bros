package com.lucafacchini.objects;

import com.lucafacchini.GamePanel;
import com.lucafacchini.Utilities;

import javax.imageio.ImageIO;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Chest_Object class
 */
public class Chest_Object extends SuperObject {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(Chest_Object.class.getName());

    /**
     * @brief Constructor for the Chest_Object class.
     * @param gp The GamePanel instance.
     * @param utilities to rescale the image (call the rescaleImage method).
     */
    public Chest_Object(GamePanel gp, Utilities utilities) {

        name = "Chest";

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/chest.png")));
            image = utilities.rescaleImage(image, gp.TILE_SIZE, gp.TILE_SIZE);
        } catch (Exception e) {
            LOGGER.severe("Error loading chest image: " + e.getMessage());
        }
        isSolid = true;
    }
}