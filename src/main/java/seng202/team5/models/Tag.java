package seng202.team5.models;

import java.util.Objects;

/**
 * Class to record tag information.
 *
 * @author Martyn Gascoigne
 */
public class Tag {
    String name;
    int colour;
    private int tagId;
    private int userId;

    /**
     * Constructor with IDs only.
     */
    public Tag(int tagId, int userId) {
        this.tagId = tagId;
        this.userId = userId;
        this.name = "";
        this.colour = 0; // Not set
    }


    /**
     * Constructor with all the parameters.
     */
    public Tag(int tagId, int userId, String name, int colour) {
        this.tagId = tagId;
        this.userId = userId;
        this.name = name;
        this.colour = colour; // Not set
    }


    /**
     * Constructor with no tagId.
     */
    public Tag(int userId, String name, int colour) {
        this.userId = userId;
        this.name = name;
        this.colour = colour; // Not set
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
     * Set the tag's id.
     */
    public void setTagId(int id) {
        this.tagId = id;
    }

    /**
     * Get the user ID.
     *
     * @return the user ID of this tag
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Set the tag's user id.
     */
    public void setUserId(int id) {
        this.userId = id;
    }


    // Setters

    /**
     * Get the stored tag name.
     *
     * @return the name of this tag
     */
    public String getName() {
        return name;
    }

    /**
     * Set the tag's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the stored tag colour.
     *
     * @return the colour of this tag
     */
    public int getColour() {
        return colour;
    }

    /**
     * Set the tag's colour.
     */
    public void setColour(int colour) {
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return tagId == tag.tagId &&
                userId == tag.userId &&
                colour == tag.colour &&
                Objects.equals(name, tag.name);
    }

    /**
     * Update default toString() to make it easier for combo box
     */
    @Override
    public String toString() {
        return this.getName();
    }
}
