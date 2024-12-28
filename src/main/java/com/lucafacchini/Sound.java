package com.lucafacchini;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.logging.Logger;
import java.net.URL;

/**
 * Manages sound effects and music for the game.
 *
 * This class handles loading, playing, looping, and stopping sound files.
 */
public class Sound {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(Sound.class.getName());

    // Paths
    public final String SOUND_PATH = "/sounds/";

    // Sound files
    Clip clip; // The exact audio clip to be played
    URL[] soundURL = new URL[30]; // Array of URLs for sound files

    /**
     * @brief Constructor of the Sound class.
     * Loads the sound files upon initialization.
     */
    public Sound() {
        loadSound();
    }

    /**
     * @brief Loads sound files into an array of URLs.
     */
    private void loadSound() {
        soundURL[0] = getClass().getResource(SOUND_PATH + "merchant.wav");
        soundURL[1] = getClass().getResource(SOUND_PATH + "coin.wav");
        soundURL[2] = getClass().getResource(SOUND_PATH + "powerup.wav");
        soundURL[3] = getClass().getResource(SOUND_PATH + "unlock.wav");
        soundURL[4] = getClass().getResource(SOUND_PATH + "fanfare.wav");
    }

    /**
     * @brief Sets the audio file to be played.
     * @param index the index of the sound file in the soundURL array.
     */
    public void setFile(int index) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[index]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        }catch(Exception e) {
            LOGGER.severe("Error loading sound file: " + e.getMessage());
        }
    }

    /**
     * @brief Plays the currently set audio file.
     */
    public void play() {
        clip.start();
    }

    /**
     * @brief Loops the currently set audio file continuously.
     */
    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * @brief Stops the currently playing audio file.
     */
    public void stop() {
        clip.stop();
    }
}