package seng202.team5.models;

import java.util.Objects;

/**
 * Class to record drink review information.
 *
 * @author Martyn Gascoigne
 */
public class Drinks {
    private int wineId;
    private int userId;
    private boolean favourite;
    private String notes;
    private int rating;


    /**
     * Constructor with IDs only.
     */
    public Drinks(int wineId, int userId) {
        this.wineId = wineId;
        this.userId = userId;
        this.favourite = false;
        this.notes = "";
        this.rating = -1; // Not set
    }


    /**
     * Constructor with all the parameters.
     */
    public Drinks(int wineId, int userId, boolean favourite, String notes, int rating) {
        this.wineId = wineId;
        this.userId = userId;
        this.favourite = favourite;
        this.notes = notes;
        this.rating = rating;
    }


    // Getters

    /**
     * Get the wine ID.
     *
     * @return the wine ID of this review
     */
    public int getWineId() {
        return wineId;
    }


    /**
     * Get the user ID.
     *
     * @return the user ID of this review
     */
    public int getUserId() {
        return userId;
    }


    /**
     * Get the favourite status.
     *
     * @return whether the wine is favourite or not
     */
    public boolean isFavourite() {
        return favourite;
    }


    /**
     * Get the stored notes.
     *
     * @return the notes of this review
     */
    public String getNotes() {
        return notes;
    }


    /**
     * Get the number of stars the review currently has.
     *
     * @return the number of stars
     */
    public int getRating() {
        return rating;
    }


    // Setters

    /**
     * Set the user ID.
     *
     * @param userId the ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }


    /**
     * Set the wine ID.
     *
     * @param wineId the ID to set
     */
    public void setWineId(int wineId) {
        this.wineId = wineId;
    }


    /**
     * Set the wine's favourite status.
     *
     * @param favourite whether the wine is favourite
     */
    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }


    /**
     * Set the notes.
     *
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Set the review value.
     *
     * @param rating the number of stars
     */
    public void setRating(int rating) {
        this.rating = rating;
    }


    /**
     * Check if this object is equal to another given object.
     *
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Drinks drinks = (Drinks) o;
        return wineId == drinks.wineId &&
                userId == drinks.userId &&
                favourite == drinks.favourite &&
                rating == drinks.rating &&
                Objects.equals(notes, drinks.notes);
    }

}
