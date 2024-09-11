package seng202.team5.models;

/**
 * Default Wine class.
 *
 * @author Martyn Gascoigne
 */
public class Wine {
    private int id;
    private String name = null;
    private String description = null;
    private int year = 0;
    private int ratingValue = 0;
    private double price = 0.0;
    private boolean favourite = false;
    private WineVariety wineVariety;
    private Region region;
    private Vineyard vineyard;

    /**
     * Gets the wine name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the wine's current market price.
     *
     * @return price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Gets the bottled year.
     *
     * @return year
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the wine's rating value.
     *
     * @return ratingValue
     */
    public double getRating() {
        return ratingValue;
    }

    /**
     * Gets the favourite status of the wine.
     *
     * @return favourite
     */
    public boolean isFavourite() {
        return favourite;
    }

    /**
     * Constructor for creating a wine object with a name and description.
     *
     * @param name name of wine
     * @param description description of wine
     */
    public Wine(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor for creating a wine object with a name,
     * description, year, rating, price and favourite status
     *
     * @param name name of wine
     * @param description description of wine
     * @param year year of wine
     * @param ratingValue rating value of wine
     * @param price current market price of wine
     * @param favourite favourite status of wine
     * @param winevariety variety of the wine
     * @param region the region the wine comes from
     * @param vineYar the vineyard the wine comes from
     */
    public Wine(int id, String name, String description, int year, int ratingValue, double price, WineVariety wineVariety, Region region, Vineyard vineYard) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.year = year;
        this.ratingValue = ratingValue;
        this.price = price;
       // this.favourite = favourite;
        this.wineVariety = wineVariety;
        this.region = region;
        this.vineyard = vineYard;
    }
}
