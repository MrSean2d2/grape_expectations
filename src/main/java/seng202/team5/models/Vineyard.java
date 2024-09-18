package seng202.team5.models;

import java.util.Objects;

/**
 * Creates a vineyard object.
 */
public class Vineyard {
    private int id;
    private final String name;
    private final String region;

    /**
     * Sets the name of the vineyard.
     *
     * @param name the name of the vineyard
     * @param region where vineyard is located
     */
    public Vineyard(String name, String region) {
        this.id = 0;
        this.name = name;
        this.region = region;
    }

    /**
     * constructor for a vineyard object,
     * contains the ID of the vineyard.
     *
     * @param id the id of the vineyard (unique)
     * @param name the name of the vineyard
     * @param region where vineyard is located
     */
    public Vineyard(int id, String name, String region) {
        this.id = id;
        this.name = name;
        this.region = region;
    }

    /**
     * Gets the name of the vineyard.
     */
    public String getName() {
        return name;
    }

    /**
     * gets the region of the vineyard.
     *
     * @return String name of vineyard
     */
    public String getRegion() {
        return region;
    }

    /**
     * gets the id of the vineyard.
     *
     * @return int id of vineyard
     */
    public int getId() {
        return id;
    }

    /**
     * sets the vineyard id to the given value.
     *
     * @param id specified value to change id to
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * checks if the vineyard is equal to another vineyard object
     * does this by checking each attribute of vineyards against each other.
     *
     * @param o vineyard object that is compared to the vineyard
     * @return boolean whether all attributes of the 2 vineyards are the same
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vineyard vineyard = (Vineyard) o;
        return id == vineyard.id && Objects.equals(name, vineyard.name)
                && Objects.equals(region, vineyard.region);
    }
}
