package seng202.team5.models;

/**
 * Creates a vineyard object.
 */
public class Vineyard {
    private final String name;

    /**
     * Sets the name of the vineyard.
     *
     * @param name the name of the vineyard
     */
    public Vineyard(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the vineyard.
     */
    public String getName() {
        return name;
    }
}
