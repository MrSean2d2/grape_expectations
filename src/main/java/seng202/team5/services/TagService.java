package seng202.team5.services;

import java.util.List;
import seng202.team5.models.Tag;
import seng202.team5.repository.TagsDAO;

/**
 * Service Class to manage wine actions.
 */
public class TagService {
    private Tag selectedTag;
    private Tag createdTag;
    private final TagsDAO tagsDAO;
    private static TagService instance;

    /**
     * Default constructor for the tag service class.
     */
    public TagService() {
        tagsDAO = new TagsDAO();
    }

    /**
     * Returns the singleton WineService instance.
     *
     * @return instance of wine service class
     */
    public static TagService getInstance() {
        if (instance == null) {
            instance = new TagService();
        }
        return instance;
    }

    /**
     * Sets selectedTag to the tag clicked on in DashboardPage.
     *
     * @param tag the selected tag
     */
    public void setSelectedTag(Tag tag) {
        this.selectedTag = tag;
    }

    /**
     * Returns the tag selected in DashboardPage.
     *
     * @return selectedTag
     */
    public Tag getSelectedTag() {
        return selectedTag;
    }

    /**
     * Sets createdTag to the created tag.
     *
     * @param tag the tag that was just created
     */
    public void setCreatedTag(Tag tag) {
        this.createdTag = tag;
    }

    /**
     * Returns the most recently createdTag.
     *
     * @return createTag
     */
    public Tag getCreatedTag() {
        return createdTag;
    }

    /**
     * Check if a tag with this name already exists.
     *
     * @param tagName the tag's name
     * @param userId the user's id
     * @return if the tag already exists
     */
    public boolean checkTagExists(String tagName, int userId) {
        // Get a list of the user's tags
        List<Tag> userCreatedTags = tagsDAO.getFromUser(userId);

        for (Tag userTag : userCreatedTags) {
            // Using custom "get and check"
            if (userTag.getName().equals(tagName)) {
                // Tag exists so return true
                return true;
            }
        }

        // Tag doesn't exist
        return false;
    }

    /**
     * Verify the tag name length.
     *
     * @param name the name
     * @return an error message, null if the name is valid
     */
    public String checkName(String name) {
        int maxChars = 20;
        if (name.isBlank()) {
            return "Tag name can't be blank!";
        } else if (name.length() > maxChars) {
            return String.format("Tag name is too long! Must be no more than %s characters.",
                    maxChars);
        } else {
            return null;
        }
    }

    /**
     * Create a tag, add it to the database, and return it. You must check for
     * duplicates with {@link TagService#checkTagExists(String, int)} first.
     *
     * @param userId the user id of the user who created the tag
     * @param name the name of the tag
     * @param colourId the colour id
     * @return the added tag object
     */
    public Tag createTag(int userId, String name, int colourId) {
        Tag tag = new Tag(userId, name, colourId);
        int id = tagsDAO.add(tag);
        if (id != -1) {
            tag.setTagId(id);
            setCreatedTag(tag);
            return tag;
        } else {
            return null;
        }
    }

}
