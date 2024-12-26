package com.lucafacchini.tiles;

import com.lucafacchini.GamePanel;
import com.lucafacchini.Utilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TileManager {

    // ------------------- Fields -------------------

    // Coordinates
    public static int worldX, worldY;
    public static int screenX, screenY;

    // Map management
    public static final String MAPS_PATH = "/maps/";
    public HashMap<Integer, Tile> tileMap; // Store all the tiles
    public final int[][] GAME_MAP; // Store the actual map

    // Objects
    private final GamePanel gp;
    private final Utilities utilities = new Utilities();

    // Logger
    private static final Logger LOGGER = Logger.getLogger(TileManager.class.getName());

    // ------------------- Constructor -------------------

    public TileManager(GamePanel gp, String path) {
        this.gp = gp;
        GAME_MAP = new int[gp.MAX_WORLD_COLUMNS][gp.MAX_WORLD_ROWS];
        tileMap = new HashMap<>();

        loadMap(MAPS_PATH + path);
        rescaleAllTileImages();

        // TODO: Implement a way to set the solid tiles
        // [ DEBUG ]
        setSolid(38193);
    }

    // ------------------- Tile Loading -------------------

    public void loadMap(String filePath) {
        try (InputStream inputFile = getClass().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputFile))) {

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
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load map: " + filePath, e);
        }
        loadAllTileImages();
    }

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

    // ------------------- Tile Rescaling -------------------

    private void rescaleAllTileImages() {
        for (Tile tile : tileMap.values()) {
            if (tile.image != null) {
                tile.image = utilities.rescaleImage(tile.image, gp.TILE_SIZE, gp.TILE_SIZE);
            }
        }
    }

    // ------------------- Tile Setting -------------------

    public void setSolid(int id) {
        Tile tile = tileMap.get(id);
        if (tile != null) {
            tile.isSolid = true;
        }
    }

    // ------------------- Drawing -------------------

    public void draw(Graphics2D g2d) {
        for (int row = 0; row < gp.MAX_WORLD_ROWS; row++) {
            for (int col = 0; col < gp.MAX_WORLD_COLUMNS; col++) {
                int tileID = GAME_MAP[col][row];

                if (tileID == -1) continue;

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

    private boolean isVisible() {
        return worldX + gp.TILE_SIZE > gp.player.worldX - gp.player.screenX &&
                worldX - gp.TILE_SIZE < gp.player.worldX + gp.player.screenX &&
                worldY + gp.TILE_SIZE > gp.player.worldY - gp.player.screenY &&
                worldY - gp.TILE_SIZE < gp.player.worldY + gp.player.screenY;
    }
}
