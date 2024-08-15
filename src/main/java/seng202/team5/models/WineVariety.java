package seng202.team5.models;

import seng202.team5.models.WineType;

/**
 *  Class to describe a wine's variety
 */
public class WineVariety {
    String name;
    WineType type;

    public WineVariety(String name, WineType wineType) {
        this.name = name;
        this.type = wineType;
    }

    /**
     * Get the name of the wine variety
     * @return the wine variety's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of the wine variety
     * @return the wine variety's type
     */
    public WineType getType() {
        return type;
    }
}
