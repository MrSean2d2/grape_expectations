package seng202.team5.models;

/**
 * Creates a vineyard object.
 */
public class Vineyard {
    private final int id;
    private final String name;
    private final double lat;
    private final double lon;
    /**
     * Sets the name of the vineyard.
     *
     * @param name the name of the vineyard
     */
    public Vineyard(String name) {
        this.name = name;
        this.lat = 40.9006F;
        this.lon = 174.8860F;
        this.id = 0;
    }

    /**
     * Sets the name of the vineyard.
     *
     * @param name the name of the vineyard
     * @param lat the latitude
     * @param lon the longitude
     */
    public Vineyard(int id, String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.id = 0;
    }

    public int getId() {
        return id;
    }

    /**
     * Gets the name of the vineyard.
     */
    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
