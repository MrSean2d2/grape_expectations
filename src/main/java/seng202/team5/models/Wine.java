package seng202.team5.models;

import java.time.Year;
import java.util.Objects;
import seng202.team5.repository.DrinksDAO;

/**
 * Default Wine class.
 *
 * @author Martyn Gascoigne
 */
public class Wine {
    private int id;
    private String name;
    private String description = null;
    private int year = 0;
    private int ratingValue = 0;
    private double price = 0.0;
    private boolean favourite = false;
    private String notes = null;
    private String wineVariety;
    private String colour;
    private Vineyard vineyard;


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
     * Constructor for creating a wine object with a name,
     * description, year, rating, price, favourite status and notes.
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
                String wineVariety, String colour, Vineyard vineYard,
                boolean favourite, String notes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.year = year;
        this.ratingValue = ratingValue;
        this.price = price;
        this.wineVariety = wineVariety;
        this.colour = colour;
        this.vineyard = vineYard;
        this.favourite = favourite;
        this.notes = notes;
    }



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


    /**
     * Gets the wine's colour.
     *
     * @return the colour of the wine
     */
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
     * Set the wine's id.
     *
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Set the wine's name.
     *
     * @param name the wine's new name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Set the wine's colour.
     *
     * @param colour the wine's new colour
     */
    public void setColour(String colour) {
        this.colour = colour;
    }


    /**
     * Check if this object is equal to another given object.
     *
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Wine wine = (Wine) o;
        return id == wine.id && year == wine.year
                &&
                ratingValue == wine.ratingValue
                &&
                Double.compare(price, wine.price) == 0
                &&
                Objects.equals(name, wine.name)
                &&
                Objects.equals(description, wine.description)
                &&
                Objects.equals(wineVariety, wine.wineVariety)
                &&
                Objects.equals(colour, wine.colour)
                &&
                (Objects.equals(vineyard.getName(), wine.vineyard.getName()));
    }

    /**
     * set the variety name of a wine.
     *
     * @param unknownVariety string value to save as variety name
     */
    public void setVariety(String unknownVariety) {
        wineVariety = unknownVariety;
    }


    /**
     * checks if a wine has valid values for its attributes.
     * eg: year and price aren't 0
     *
     * @return boolean whether given wine is valid enough to be added to database
     */
    public boolean isValidWine() {
        //wine must have a name
        if (name.isEmpty()) {
            return false;
        }
        //description can be empty
        int currentYear = Year.now().getValue(); //gets current year for future-proofing
        if (year < 1700 | year > currentYear) { //1700 is arbitrary boundary
            return false;
        }
        if (ratingValue < 0 | ratingValue > 100) {
            return false;
        }
        if (price < 0) {
            return false;
        }
        if (wineVariety.isEmpty()) {
            wineVariety = "Unknown Variety";
        }
        return true;

    }

}