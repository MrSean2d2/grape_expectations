package seng202.team5.models;

/**
 * Creates a vineyard object
 */
public class Vineyard {
    private String name;
    //private String description;
    //description removed because it was not in the csv file

    /**
     * Sets the name of the vineyard
     * @param name the name of the vineyard
     */
    public Vineyard(String name) {
        this.name = name;
    }

    /**
     * gets the name of the vineyard
     */
    public String getName() {
        return name;
    }
}
