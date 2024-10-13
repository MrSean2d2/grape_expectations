package seng202.team5.models;

/**
 * Class to store which tag goes where.
 *
 * @author Martyn Gascoigne
 */
public class AssignedTag {
    private int tagId;
    private int userId;
    private int wineId;

    /**
     * Constructor with IDs only.
     *
     * @param tagId the id for the tag
     * @param userId the id for the user
     * @param wineId the id of the wine
     */
    public AssignedTag(int tagId, int userId, int wineId) {
        this.tagId = tagId;
        this.userId = userId;
        this.wineId = wineId;
    }


    // Getters

    /**
     * Get the tag ID.
     *
     * @return the tag ID
     */
    public int getTagId() {
        return tagId;
    }

    /**
     * Get the user ID.
     *
     * @return the user ID of this tag
     */
    public int getUserId() {
        return userId;
    }


    // Setters


    /**
     * Get the wine ID.
     *
     * @return the wine ID of this tag
     */
    public int getWineId() {
        return wineId;
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
        AssignedTag that = (AssignedTag) o;
        return tagId == that.tagId
                && userId == that.userId
                && wineId == that.wineId;
    }
}