package seng202.team5.models;

import java.util.Objects;

/**
 * Default Wine class.
 *
 * @author Martyn Gascoigne
 */
public class Wine {
    private int id;
    private final String name;
    private final String description;
    private int year = 0;
    private int ratingValue = 0;
    private double price = 0.0;
    private boolean favourite = false;
    private String wineVariety;
    private String colour;
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
    public int getRating() {
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
     * Gets the wine id.
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the wine description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the wine vineyard.
     *
     * @return vineyard object
     */
    public Vineyard getVineyard() {
        return vineyard;
    }

    /**
     * Gets the wine variety.
     *
     * @return wine variety
     */
    public String getWineVariety() {
        return wineVariety;
    }

    public String getWineColour() {
        return colour;
    }

    /**
     * Gets the wine region.
     *
     * @return wine region
     */
    public String getRegion() {
        return vineyard.getRegion();
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
     * description, year, rating, price and favourite status.
     *
     * @param id the wine's id
     * @param name name of wine
     * @param description description of wine
     * @param year year of wine
     * @param ratingValue rating value of wine
     * @param price current market price of wine
     * @param wineVariety variety of the wine
     * @param vineYard the vineyard the wine comes from
     */
    public Wine(int id, String name, String description, int year, int ratingValue, double price,
                String wineVariety, String colour, Vineyard vineYard) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.year = year;
        this.ratingValue = ratingValue;
        this.price = price;
        // this.favourite = favourite;
        this.wineVariety = wineVariety;
        this.colour = colour;
        this.vineyard = vineYard;
    }

    /**
     * Constructor for creating a wine object with a name,
     * description, year, rating, price and favourite status. The wine's id is
     * determined by the database.
     *
     * @param name the name of the wine
     * @param description the wine's description
     * @param year the year the wine was produced
     * @param ratingValue the wine's rating value (0 - 100)
     * @param price the wine's price
     * @param wineVariety the grape variety of the wine
     * @param vineyard the vineyard which produced the wine
     */
    public Wine(String name, String description, int year, int ratingValue,
                double price, String wineVariety, String colour, Vineyard vineyard) {
        this.colour = colour;
        this.id = 0;
        this.name = name;
        this.description = description;
        this.year = year;
        this.ratingValue = ratingValue;
        this.price = price;
        this.wineVariety = wineVariety;
        this.vineyard = vineyard;
    }

    /**
     * Set the wine's id
     *
     * @param id the new id
     */
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
        Wine wine = (Wine) o;
        return id == wine.id && year == wine.year && ratingValue == wine.ratingValue &&
                Double.compare(price, wine.price) == 0 && favourite == wine.favourite &&
                Objects.equals(name, wine.name) &&
                Objects.equals(description, wine.description) &&
                Objects.equals(wineVariety, wine.wineVariety) &&
                Objects.equals(colour, wine.colour) &&
                (Objects.equals(vineyard.getName(), wine.vineyard.getName()));
    }
}
