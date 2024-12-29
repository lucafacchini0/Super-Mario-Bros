package com.lucafacchini.tiles;

import com.lucafacchini.GamePanel;
import com.lucafacchini.Utilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class manages the tiles of the game, and therefore, the map.
 */
public class TileManager {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(TileManager.class.getName());

    // Tile properties
    public static int worldX, worldY; // The actual position of the tile
    public static int screenX, screenY; // The position of the tile on the screen (calculated based on the player's position)

    // Map management
    public static final String MAPS_PATH = "/maps/";

    /**
     * @brief tileMap is a HashMap that stores all the tiles.
     * The key is the tile ID, and the value is the single Tile object.
     */
    public HashMap<Integer, Tile> tileMap; // Store all the tiles
    public final int[][] GAME_MAP; // Store the actual map

    // Objects
    private final GamePanel gp;
    private final Utilities utilities = new Utilities();

    /**
     * @brief Constructor for the TileManager class.
     * @param gp The GamePanel instance.
     * @param path The path of the map file.
     * TODO: Implement a way to set the solid tiles
     */
    public TileManager(GamePanel gp, String path) {
        this.gp = gp;
        GAME_MAP = new int[gp.MAX_WORLD_COLUMNS][gp.MAX_WORLD_ROWS];
        tileMap = new HashMap<>();

        // Load the map
        loadMap(MAPS_PATH + path);
        rescaleAllTileImages();

        setSolid(38193); // DEBUG

    }


    /**
     * @brief This method loads the map from a file.
     * First of all, it reads the file provided as a parameter.
     * Then, it reads the lines of the file and stores the values in the GAME_MAP array.
     *
     * The map file is a CSV file where each number represents a tile.
     *
     * After loading the map, it calls the loadAllTileImages method.
     *
     * @param filePath The path of the map file.
     */
    public void loadMap(String filePath) {
        try (InputStream inputFile = getClass().getResourceAsStream(filePath)) {
            assert inputFile != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputFile))) {

                int currentWorldRow = 0;

                while (currentWorldRow < gp.MAX_WORLD_ROWS) {
                    String line = reader.readLine();
                    if (line == null) break; // Stop if no more lines

                    String[] numbers = line.split(",");
                    for (int currentWorldColumn = 0; currentWorldColumn < gp.MAX_WORLD_COLUMNS && currentWorldColumn < numbers.length; currentWorldColumn++) {
                        GAME_MAP[currentWorldColumn][currentWorldRow] = Integer.parseInt(numbers[currentWorldColumn]);
                    }
                    currentWorldRow++;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load map: " + filePath, e);
        }
        loadAllTileImages();
    }


    /**
     * @brief This method loads all the tile images.
     * It iterates over the GAME_MAP array and calls the loadTileImage method for each tile.
     */
    private void loadAllTileImages() {
        for (int row = 0; row < gp.MAX_WORLD_ROWS; row++) {
            for (int col = 0; col < gp.MAX_WORLD_COLUMNS; col++) {
                int tileID = GAME_MAP[col][row];
                if (tileID != -1) {
                    loadTileImage(tileID);
                }
            }
        }
    }


    /**
     * @brief This method loads a single tile image.
     * It checks if the tile is already in the tileMap HashMap.
     * If it is not, it reads the image from the file and stores it in the tileMap HashMap.
     *
     * @param id The ID of the tile.
     */
    private void loadTileImage(int id) {
        if (!tileMap.containsKey(id)) {
            try {
                String imagePath = "/tiles/tile_" + id + ".png";
                InputStream imageStream = getClass().getResourceAsStream(imagePath);

                if (imageStream == null) {
                    LOGGER.log(Level.WARNING, "Tile image not found for ID: {0}", id);
                } else {
                    Tile tile = new Tile();
                    tile.image = ImageIO.read(imageStream);
                    if (tile.image != null) {
                        tileMap.put(id, tile);
                        LOGGER.log(Level.INFO, "Successfully loaded tile for ID: {0}", id);
                    } else {
                        LOGGER.log(Level.SEVERE, "Failed to read tile image for ID: {0}", id);
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error loading tile image for ID: {0}");
            }
        }
    }


    /**
     * @brief This method rescales all the tile images.
     * It iterates over the tileMap HashMap and calls the rescaleImage method for each tile.
     */
    private void rescaleAllTileImages() {
        for (Tile tile : tileMap.values()) {
            if (tile.image != null) {
                tile.image = utilities.rescaleImage(tile.image, gp.TILE_SIZE, gp.TILE_SIZE);
            }
        }
    }


    /**
     * @brief This method sets a tile as solid.
     * It gets the tile from the tileMap HashMap and sets the isSolid property to true.
     *
     * @param id The ID of the tile.
     */
    public void setSolid(int id) {
        Tile tile = tileMap.get(id);
        if (tile != null) {
            tile.isSolid = true;
        }
    }


    /**
     * @brief This method draws the map.
     * It iterates over the GAME_MAP array and draws the tiles on the screen.
     * It calculates the position of the tile on the screen based on the player's position.
     * It also checks if the tile is visible on the screen before drawing it. (Optimization)
     *
     * @param g2d The Graphics2D object.
     */
    public void draw(Graphics2D g2d) {
        for (int row = 0; row < gp.MAX_WORLD_ROWS; row++) {
            for (int col = 0; col < gp.MAX_WORLD_COLUMNS; col++) {
                int tileID = GAME_MAP[col][row];

                if (tileID == -1) continue; // Skip empty tiles

                worldX = col * gp.TILE_SIZE;
                worldY = row * gp.TILE_SIZE;
                screenX = worldX - gp.player.worldX + gp.player.screenX;
                screenY = worldY - gp.player.worldY + gp.player.screenY;

                if (isVisible()) {
                    Tile tile = tileMap.get(tileID);
                    if (tile != null && tile.image != null) {
                        g2d.drawImage(tile.image, screenX, screenY, null);
                    }
                }
            }
        }
    }

    /**
     * @brief This method checks if the tile is visible on the screen.
     * It calculates the boundaries of the tile and checks if it is within the screen boundaries.
     *
     * @return true if the tile is visible, false otherwise.
     */
    private boolean isVisible() {
        return worldX + gp.TILE_SIZE > gp.player.worldX - gp.player.screenX &&
                worldX - gp.TILE_SIZE < gp.player.worldX + gp.player.screenX &&
                worldY + gp.TILE_SIZE > gp.player.worldY - gp.player.screenY &&
                worldY - gp.TILE_SIZE < gp.player.worldY + gp.player.screenY;
    }








    public boolean isTileSolid(int tileID) {
        Tile tile = tileMap.get(tileID);
        return tile != null && tile.isSolid;
    }
}
