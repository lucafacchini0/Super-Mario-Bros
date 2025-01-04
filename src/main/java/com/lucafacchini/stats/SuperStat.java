package com.lucafacchini.stats;

import java.util.logging.Logger;

public class SuperStat {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(SuperStat.class.getName());

    public enum StatType {
        HP,
        SPEED
    }

    public StatType statType;

    // Values
    public int max;
    public int current;

    public SuperStat(int max) {
        this.max = max;
        this.current = max;
    }

    public SuperStat(int max, int current) {
        this.max = max;
        this.current = current;
    }

    // Setters

    /**
     * @brief Set the max value of the stat.
     * @param max The max value of the stat.
     */
    public void setMax(int max) {
        if(max <= 0) {
            LOGGER.warning("Max value must be greater than 0.");
            this.max = 1;
            return;
        }

        if(current > max) {
            current = max;
        }

        this.max = max;
    }


    /**
     * @brief Set the current value of the stat.
     * @param current The current value of the stat.
     */
    public void setCurrent(int current) {
        if(current < 0) {
            LOGGER.warning("Current value must be greater than or equal to 0.");
            this.current = 0;
            return;
        }

        if(current > max) {
            LOGGER.warning("Current value must be less than or equal to max value.");
            this.current = max;
            return;
        }

        this.current = current;
    }


    /**
     * @brief Add a value to the stat.
     * @param value The value to add.
     */
    public void add(int value) {
        if(value < 0) {
            LOGGER.warning("Value must be greater than or equal to 0.");
            return;
        }

        current += value;

        if(current > max) {
            current = max;
        }
    }

    /**
     * @brief Remove a value from the stat.
     * @param value The value to remove.
     */
    public void remove(int value) {
        if(value < 0) {
            LOGGER.warning("Value must be greater than or equal to 0.");
            return;
        }

        current -= value;

        if(current < 0) {
            current = 0;
        }
    }


    // Getters
    public int getMax() { return max; }
    public int getCurrent() { return current; }
}
