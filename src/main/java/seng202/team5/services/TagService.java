package seng202.team5.services;

import seng202.team5.models.Tag;
import seng202.team5.models.Wine;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

import java.time.Year;
import java.util.List;

/**
 * Service Class to manage wine actions.
 */
public class TagService {
    private Tag selectedTag;
    private final TagsDAO tagsDAO;
    private static TagService instance;

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
     * Check if a tag with this name already exists
     *
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
}
