package seng202.team5.models;

import java.util.Objects;
import seng202.team5.repository.ReviewDAO;

/**
 * Class to record drink review information.
 *
 * @author Martyn Gascoigne
 */
public class Review {
    private final int wineId;
    private final int userId;
    private boolean favourite;
    private String notes;
    private int rating;

    private final ReviewDAO reviewDAO;


    /**
     * Constructor with IDs only.
     *
     * @param userId id of the user
     * @param wineId id of the wine
     */
    public Review(int wineId, int userId) {
        this.wineId = wineId;
        this.userId = userId;
        this.favourite = false;
        this.notes = "";
        this.rating = -1; // Not set
        this.reviewDAO = new ReviewDAO();
    }


    /**
     * Constructor with all the parameters.
     */
    public Review(int wineId, int userId, boolean favourite, String notes, int rating) {
        this.wineId = wineId;
        this.userId = userId;
        this.favourite = favourite;
        this.notes = notes;
        this.rating = rating;
        this.reviewDAO = new ReviewDAO();
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
     * Set the notes and updates the database.
     *
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
        reviewDAO.update(this);
    }

    /**
     * Set the review value and updates the database.
     *
     * @param rating the number of stars
     */
    public void setRating(int rating) {
        this.rating = rating;
        reviewDAO.update(this);
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
        Review review = (Review) o;
        return wineId == review.wineId
                && userId == review.userId
                && favourite == review.favourite
                && rating == review.rating
                && Objects.equals(notes, review.notes);
    }

}
