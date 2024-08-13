package seng202.team5.models;

import seng202.team5.models.WineType;

/**
 *  Class to describe a wine's variety.
 */
public class WineVariety {
    String name;
    WineType type;

    /**
     * Get the name of the wine variety.
     *
     * @return the wine variety's name
     */
    String getName() {
        return name;
    }

    /**
     * Get the type of the wine variety.
     *
     * @return the wine variety's type
     */
    WineType getType() {
        return type;
    }
}
