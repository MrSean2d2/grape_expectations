package seng202.team5.models;

import java.util.Objects;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

/**
 * Default Wine class.
 *
 * @author Martyn Gascoigne
 */
public class Wine {
    private int id;
    private StringProperty name;
    private StringProperty description;
    private IntegerProperty year;
    private IntegerProperty ratingValue;
    private DoubleProperty price;
    private StringProperty wineVariety;
    private StringProperty colour;
    private ObjectProperty<Vineyard> vineyard;

    /**
     * Constructor for creating a wine object with a name,
     * description, year, rating, price and favourite status.
     *
     * @param id          the wine's id
     * @param name        name of wine
     * @param description description of wine
     * @param year        year of wine
     * @param ratingValue rating value of wine
     * @param price       current market price of wine
     * @param wineVariety variety of the wine
     * @param colour      the wine's colour
     * @param vineYard    the vineyard the wine comes from
     */
    public Wine(int id, String name, String description, int year, int ratingValue, double price,
                String wineVariety, String colour, Vineyard vineYard) {
        this.id = id;
        setName(name);
        setDescription(description);
        setYear(year);
        setRating(ratingValue);
        setVariety(wineVariety);
        setPrice(price);
        setColour(colour);
        setVineyard(vineYard);
    }

    /**
     * Constructor for creating a wine object with a name,
     * description, year, rating, price and favourite status. The wine's id is
     * determined by the database.
     *
     * @param name        the name of the wine
     * @param description the wine's description
     * @param year        the year the wine was produced
     * @param ratingValue the wine's rating value (0 - 100)
     * @param price       the wine's price
     * @param wineVariety the grape variety of the wine
     * @param colour      the wine's colour
     * @param vineyard    the vineyard which produced the wine
     */
    public Wine(String name, String description, int year, int ratingValue,
                double price, String wineVariety, String colour, Vineyard vineyard) {
        setColour(colour);
        this.id = 0;
        setName(name);
        setDescription(description);
        setYear(year);
        setRating(ratingValue);
        setPrice(price);
        setVariety(wineVariety);
        setVineyard(vineyard);
    }

    /**
     * Property getter for name.
     *
     * @return the StringProperty for the wine's name
     */
    public final StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty();
        }
        return name;
    }

    /**
     * Property getter for the description.
     *
     * @return the StringProperty for the wine's description
     */
    public final StringProperty descriptionProperty() {
        if (description == null) {
            description = new SimpleStringProperty();
        }
        return description;
    }

    /**
     * Property getter for the year.
     *
     * @return the IntegerProperty for the wine's year
     */
    public final IntegerProperty yearProperty() {
        if (year == null) {
            year = new SimpleIntegerProperty();
        }
        return year;
    }

    /**
     * Property getter for the wine's rating.
     *
     * @return the IntegerProperty for the ratingValue
     */
    public final IntegerProperty ratingValueProperty() {
        if (ratingValue == null) {
            ratingValue = new SimpleIntegerProperty();
        }
        return ratingValue;
    }

    /**
     * Property getter for the wine's price.
     *
     * @return the DoubleProperty for the price
     */
    public final DoubleProperty priceProperty() {
        if (price == null) {
            price = new SimpleDoubleProperty();
        }
        return price;
    }

    /**
     * Property getter for the wine's variety.
     *
     * @return the StringProperty for the wine's variety
     */
    public final StringProperty wineVarietyProperty() {
        if (wineVariety == null) {
            wineVariety = new SimpleStringProperty();
        }
        return wineVariety;
    }

    /**
     * Property getter for the wine's colour.
     *
     * @return the StringProperty for the colour
     */
    public final StringProperty colourProperty() {
        if (colour == null) {
            colour = new SimpleStringProperty();
        }
        return colour;
    }

    /**
     * Property getter for the wine's vineyard object.
     *
     * @return the ObjectProperty for the vineyard
     */
    public final ObjectProperty<Vineyard> vineyardProperty() {
        if (vineyard == null) {
            vineyard = new SimpleObjectProperty<>();
        }
        return vineyard;
    }

    /**
     * Gets the wine name.
     *
     * @return name
     */
    public final String getName() {
        return nameProperty().get();
    }


    /**
     * Gets the wine's current market price.
     *
     * @return price
     */
    public final double getPrice() {
        return priceProperty().get();
    }

    /**
     * Sets the price of the wine.
     *
     * @param price the new wine to set
     */
    public final void setPrice(double price) {
        priceProperty().set(price);
    }

    /**
     * Gets the bottled year.
     *
     * @return year
     */
    public final int getYear() {
        return yearProperty().get();
    }

    /**
     * Sets the bottled year.
     *
     * @param year the year
     */
    public final void setYear(int year) {
        yearProperty().set(year);
    }

    /**
     * Gets the wine's rating value.
     *
     * @return ratingValue
     */
    public final int getRating() {
        return ratingValueProperty().get();
    }

    /**
     * Sets the wine rating.
     *
     * @param rating the rating
     */
    public final void setRating(int rating) {
        ratingValueProperty().set(rating);
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
    public final String getDescription() {
        return descriptionProperty().get();
    }

    public final void setDescription(String description) {
        descriptionProperty().set(description);
    }

    /**
     * Gets the wine vineyard.
     *
     * @return vineyard object
     */
    public final Vineyard getVineyard() {
        return vineyardProperty().get();
    }

    /**
     * Sets the wine vineyard.
     *
     * @param vineyard vineyard object
     */
    public final void setVineyard(Vineyard vineyard) {
        vineyardProperty().set(vineyard);
    }

    /**
     * Gets the wine variety.
     *
     * @return wine variety
     */
    public final String getWineVariety() {
        return wineVarietyProperty().get();
    }

    /**
     * set the variety name of a wine.
     *
     * @param unknownVariety string value to save as variety name
     */
    public final void setVariety(String unknownVariety) {
        wineVarietyProperty().set(unknownVariety);
    }

    /**
     * Gets the wine's colour.
     *
     * @return the colour of the wine
     */
    public final String getWineColour() {
        return colourProperty().get();
    }

    /**
     * Gets the wine region.
     *
     * @return wine region
     */
    public final String getRegion() {
        return vineyardProperty().get().getRegion();
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
    public final void setName(String name) {
        nameProperty().set(name);
    }


    /**
     * Set the wine's colour.
     *
     * @param colour the wine's new colour
     */
    public final void setColour(String colour) {
        colourProperty().set(colour);
    }

    /**
     * Defines extractor callback to be used by Observable lists.
     *
     * @return a Callback containing an array of Observable properties of this Object
     */
    public static Callback<Wine, Observable[]> extractor() {
        return (Wine w) -> new Observable[]{w.nameProperty(), w.descriptionProperty(),
        w.yearProperty(), w.ratingValueProperty(), w.priceProperty(), w.wineVarietyProperty(),
        w.colourProperty(), w.vineyardProperty()};
    }

    /**
     * Checking for equality of wine objects.
     *
     * @param o object to check equality
     * @return true if equal
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
        return id == wine.id && Objects.equals(getName(), wine.getName())
                && Objects.equals(getDescription(), wine.getDescription())
                && getYear() == wine.getYear()
                && getRating() == wine.getRating()
                && Double.compare(getPrice(), wine.getPrice()) == 0
                && Objects.equals(getWineVariety(), wine.getWineVariety())
                && Objects.equals(getWineColour(), wine.getWineColour())
                && getVineyard().equals(wine.getVineyard());
    }
}