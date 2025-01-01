package com.lucafacchini;

import com.lucafacchini.entity.Entity;
import com.lucafacchini.entity.Player;
import com.lucafacchini.objects.SuperObject;
import com.lucafacchini.tiles.TileManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents the game panel where the game is rendered and updated.
 *
 * This class handles the initialization, updating, and rendering of the game.
 * It implements the Runnable interface to run the game loop in a separate thread.
 */
public class GamePanel extends JPanel implements Runnable {

    // Game status
    /**
     * @brief Enumerator that contains all the possible statuses of the game.
     * This enumerator is used to determine the current status of the game.
     */
    public enum GameStatus {
        RUNNING, PAUSED, DIALOGUE
    }
    public GameStatus gameStatus = GameStatus.RUNNING;

    // Tile settings
    public final int ORIGINAL_TILE_SIZE = 16;
    public final int SCALE = 4;
    public final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;

    // Window settings
    public final int WINDOW_ROWS = 12;
    public final int WINDOW_COLUMNS = 16;
    public final int WINDOW_WIDTH = TILE_SIZE * WINDOW_COLUMNS;
    public final int WINDOW_HEIGHT = TILE_SIZE * WINDOW_ROWS;
    public final int FPS = 60;

    // Map settings
    public final int MAX_WORLD_COLUMNS = 50;
    public final int MAX_WORLD_ROWS = 50;

    public enum MapType {
        BACKGROUND
    }

    public HashMap<MapType, TileManager> maps = new HashMap<>();

    // Thread management
    Thread gameThread;

    // Manage the key events
    KeyHandler kh = new KeyHandler(this);

    // Entities
    public Entity[] npcArray = new Entity[10]; // Max number of NPCs in the game
    public Player player = new Player(this, kh); // The player

    /**
     * @brief Manages collisions between entities, objects, and tiles.
     */
    public CollisionManager cm = new CollisionManager(this);

    // Objects
    public final int MAX_OBJECTS_ARRAY = 15;
    public SuperObject[] objectsArray = new SuperObject[MAX_OBJECTS_ARRAY]; // Max number of objects in the game. This array will store all the objects in the world.
    public AssetSetter assetSetter = new AssetSetter(this); // This class will place objects in the game.

    // Music and sound
    private final Sound music = new Sound();
    private final Sound sound = new Sound();

    // UI
    public UI ui = new UI(this, kh);

    /**
     * @brief Constructor of the GamePanel class.
     * Initializes the game panel, sets its properties, and starts the main theme music.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        addKeyListener(kh);
        setFocusable(true);

        playMusic(0); // 0: Main theme
    }

    /**
     * @brief Initializes the game by placing objects and NPCs.
     */
    public void initializeGame() {
        // Set maps
        maps.put(MapType.BACKGROUND, new TileManager(this, "background.csv"));
        assetSetter.placeObject();
        assetSetter.placeNPC();
    }

    /**
     * @brief Starts the game thread.
     * This method is called from the main class.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * @brief The main game loop, which handles updating and rendering the game.
     * This method is called repeatedly to keep the game running.
     */
    @Override
    public void run() {

        double targetFrameTime = 1_000_000_000.0 / FPS; // The delay between frames in nanoseconds.
        double nextFrameTime = System.nanoTime() + targetFrameTime; // The time when the next frame should be drawn.

        while (gameThread != null) {
            updateComponents();
            repaint();

            try {
                double remainingTimeToNextFrame = nextFrameTime - System.nanoTime();
                if (remainingTimeToNextFrame > 0) {
                    Thread.sleep((long) remainingTimeToNextFrame / 1_000_000); // Sleep until the next frame time is reached.
                }
                nextFrameTime += targetFrameTime; // Calculate the time when the next frame should be drawn.
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @brief Updates the components of the game.
     * This method is called every frame to update the game state.
     */
    private void updateComponents() {
        if (gameStatus != GameStatus.PAUSED) {
            player.update();
            npcArray[0].update();
            kh.updateKeyStates();
        }
    }

    /**
     * @brief Paints the components of the game panel.
     * This method is called every frame to render the game.
     * @param g the Graphics object used to draw the components.
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (gameStatus != GameStatus.PAUSED) {
            super.paintComponent(g);

            drawAllComponents(g2d);

            g2d.dispose();

        }

        if(gameStatus == GameStatus.DIALOGUE) {

            super.paintComponent(g);
//
//            if(!npcArray[player.npcIndex].isStillTalking) {
//                npcArray[player.npcIndex].isStillTalking = true;
//                ui.draw(g2d, player.npcIndex);
//            }



            if(kh.isEnterPressed) {
                if (npcArray[player.npcIndex].hasFinishedTalking()) {
                    npcArray[player.npcIndex].dialogueIndex = 0;
                    gameStatus = GamePanel.GameStatus.RUNNING;
                } else {
                    npcArray[player.npcIndex].dialogueIndex++;
                    ui.currentDialogue = npcArray[player.npcIndex].dialogues[npcArray[player.npcIndex].dialogueIndex];
                }
            }

            g2d.dispose();

        }
    }

    /**
     * @brief Draws all the components of the game panel.
     * This method draws the map, objects, NPCs, player, and UI.
     * @param g2d the Graphics2D object used to draw the components.
     */
    private void drawAllComponents(Graphics2D g2d) {
        for (TileManager tileManager : maps.values()) {
            tileManager.draw(g2d);
        }

        for (SuperObject object : objectsArray) {
            if (object != null) {
                object.draw(g2d, this);
            }
        }

        for (Entity npc : npcArray) {
            if (npc != null) {
                npc.draw(g2d);
            }
        }

        player.draw(g2d);
        ui.draw(g2d, player.npcIndex);
    }

    /**
     * @brief Plays the specified music track.
     * @param index the index of the music track to play.
     */
    public void playMusic(int index) {
        music.setFile(index);
        music.play();
        music.loop();
    }

    /**
     * @brief Stops the currently playing music.
     */
    public void stopMusic() {
        music.stop();
    }

    /**
     * @brief Plays the specified sound effect.
     * @param index the index of the sound effect to play.
     */
    public void playSound(int index) {
        sound.setFile(index);
        sound.play();
    }
}