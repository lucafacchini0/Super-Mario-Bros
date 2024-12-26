package com.lucafacchini;

import com.lucafacchini.entity.Entity;
import com.lucafacchini.entity.Player;
import com.lucafacchini.objects.SuperObject;
import com.lucafacchini.tiles.TileManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    // ---------------------------------------------- //

    // Game status
    public enum GameStatus {
        RUNNING,
        PAUSED,
        DIALOGUE
    }
    public GameStatus gameStatus = GameStatus.RUNNING;

    // ---------------------------------------------- //

    // Tile settings
    public final int ORIGINAL_TILE_SIZE = 16;
    public final int SCALE = 4;
    public final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;

    // ---------------------------------------------- //

    // Window settings
    public final int WINDOW_ROWS = 12;
    public final int WINDOW_COLUMNS = 16;
    public final int WINDOW_WIDTH = TILE_SIZE * WINDOW_COLUMNS;
    public final int WINDOW_HEIGHT = TILE_SIZE * WINDOW_ROWS;
    public final int FPS = 60;

    // ---------------------------------------------- //

    // Map settings
    public final int MAX_WORLD_COLUMNS = 50;
    public final int MAX_WORLD_ROWS = 50;

    // TODO: Set as a vector
    public TileManager firstLayerMap = new TileManager(this, "background.csv");
    public TileManager secondLayerMap = new TileManager(this, "groundDecoration.csv");
    public TileManager thirdLayerMap = new TileManager(this, "background.csv");

    // ---------------------------------------------- //

    // Thread management
    Thread gameThread;

    // ---------------------------------------------- //

    // Manage the key events
    KeyHandler kh = new KeyHandler(this);

    // ---------------------------------------------- //

    // Entities
    public Entity[] npcArray = new Entity[10]; // Max number of NPCs in the game
    public Player player = new Player(this, kh); // The player

        // Entities -- Collision Manager
        public CollisionManager collisionManager = new CollisionManager(this);

    // ---------------------------------------------- //

    // Objects
    public final int MAX_OBJECTS_ARRAY = 15;
    public SuperObject[] objectsArray = new SuperObject[MAX_OBJECTS_ARRAY]; // Max number of objects in the game. This array will store all the objects in the world.
    public AssetSetter assetSetter = new AssetSetter(this); // This class will place objects in the game.

    // ---------------------------------------------- //

    // Music and sound
    private Sound music = new Sound();
    private Sound sound = new Sound();

    // ---------------------------------------------- //

    // UI
    public UI ui = new UI(this);

    // ---------------------------------------------- //

    // Constructor
    public GamePanel() {
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        this.addKeyListener(kh);
        this.setFocusable(true);

        playMusic(0); // 0: Main theme
    }

    // ---------------------------------------------- //


    // --------------- INIT GAME ------------------- //

    public void initializeGame() {
        assetSetter.placeObject();
        assetSetter.placeNPC();
    }

    // called from the main class
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // ------------END INIT GAME ------------------- //


    // Game loop
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

    private void updateComponents() {
        if(gameStatus == GameStatus.RUNNING) {
            player.update();
            npcArray[0].update();

        }
    }

    @Override
    public void paintComponent(Graphics g)  {
        if(gameStatus == GameStatus.RUNNING) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D)g;

            drawAllComponents(g2d);

            g2d.dispose();
        } else if(gameStatus == GameStatus.DIALOGUE && !ui.isDrawing) {
            ui.draw((Graphics2D)g);
        }

    }

    // Draw the components of the panel.
    private void drawAllComponents(Graphics2D g2d) {

        firstLayerMap.draw(g2d);

        for(int i = 0; i < objectsArray.length; i++) {
            if(objectsArray[i] != null) {
                objectsArray[i].draw(g2d, this);
            }
        }

        for(int i = 0; i < npcArray.length; i++) {
            if(npcArray[i] != null) {
                npcArray[i].draw(g2d);
            }
        }

        player.draw(g2d);
        secondLayerMap.draw(g2d);
        ui.draw(g2d);
    }

    public void playMusic(int index) {
        music.setFile(index);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSound(int index) {
        sound.setFile(index);
        sound.play();
    }
}