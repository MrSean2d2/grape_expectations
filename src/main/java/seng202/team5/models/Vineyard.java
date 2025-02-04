package seng202.team5.models;

import java.util.Objects;

/**
 * Creates a vineyard object.
 */
public class Vineyard {
    private int id;
    private final String name;
    private double lat;
    private double lon;
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
     * Constructor for vineyard including coords.
     *
     * @param id of vineyard
     * @param name of vineyard
     * @param region vineyard is located in
     * @param lat latitude coord of vineyard
     * @param lon longitude coord of vineyard
     */
    public Vineyard(int id, String name, String region, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.lat = lat;
        this.lon = lon;
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
     *
     * @return vineyard name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets latitude.
     *
     * @return latitude
     */
    public double getLat() {
        return lat;
    }

    /**
     * Gets longitude.
     *
     * @return longitude
     */
    public double getLon() {
        return lon;
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
     * Sets latitude.
     *
     * @param lat latitude
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Sets longitude.
     *
     * @param lon longitude
     */
    public void setLon(double lon) {
        this.lon = lon;
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
