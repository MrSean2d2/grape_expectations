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
     */
    public Vineyard(String name, String region) {
        this.id = 0;
        this.name = name;
        this.region = region;
    }

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

    public String getRegion() {
        return region;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vineyard vineyard = (Vineyard) o;
        return id == vineyard.id && Objects.equals(name, vineyard.name) &&
                Objects.equals(region, vineyard.region);
    }
}
