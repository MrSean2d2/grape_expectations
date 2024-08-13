package seng202.team5;

/** Default Wine class
 *
 *
 */
public class Wine {
    private String name = null;
    private String description = null;
    private int year = 0;
    private int ratingValue = 0;
    private double price = 0.0;
    private boolean favourite = false;

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getYear() { return year; }
    public double getRating() { return ratingValue; }
    public boolean isFavourite() { return favourite; }

    /**
     * Constructor for creating a wine object with a name 
     * and description
     */
    public Wine(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor for creating a wine object with a name,
     * description, year, rating, price and favourite status
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
