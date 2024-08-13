package seng202.team5.models;

/**
 * Creates a vineyard object
 */
public class Vineyard {
    private String name;
    private String description;

    /**
     * Sets the name and description of the vineyard
     * @param name the name of the vineyard
     * @param description brief description of the vineyard
     */
    public Vineyard(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
