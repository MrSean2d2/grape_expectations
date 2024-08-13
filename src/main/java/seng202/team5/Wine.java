package seng202.team5;

/** 
 * Default Wine class
 * @author Martyn Gascoigne
 */
public class Wine {
    private String name = null; // Default wine name
    private String description = null; // Default description
    private int year = 0; // Bottled Year
    private int ratingValue = 0; // Rating value (from critic)
    private double price = 0.0; // Market price
    private boolean favourite = false; // Favourited by user

    /**
     * Gets the wine name
     * @return name
     */
    public String getName() { return name; }

    /**
     * Gets the wine's current market price
     * @return price
     */
    public double getPrice() { return price; }

    /**
     * Gets the bottled year
     * @return year
     */
    public int getYear() { return year; }

    /**
     * Gets the wine's rating value
     * @return ratingValue
     */
    public double getRating() { return ratingValue; }

    /**
     * Gets the favourite status of the wine
     * @return favourite
     */
    public boolean isFavourite() { return favourite; }

    /**
     * Constructor for creating a wine object with a name 
     * and description
     * 
     * @param String name of wine
     * @param String description of wine
     */
    public Wine(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor for creating a wine object with a name,
     * description, year, rating, price and favourite status
     * 
     * @param String name of wine
     * @param String description of wine
     * @param int year of wine
     * @param int rating value of wine
     * @param double current market price of wine
     * @param boolean favourite status of wine
     */
    public Wine(String name, String description, int year, int ratingValue, double price, boolean favourite) {
        this.name = name;
        this.description = description;

        this.year = year;
        this.ratingValue = ratingValue;
        this.price = price;
        this.favourite = favourite;
    }
}
