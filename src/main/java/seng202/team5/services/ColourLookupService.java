package seng202.team5.services;

/**
 * A class to look up colours for tags.
 *
 * @author Martyn Gascoigne
 */
public class ColourLookupService {

    /**
     * Get the label colour for the tag.
     *
     * @param tagId the id of the tag
     * @return a string of the CSS colour of the tag
     */
    public static String getTagLabelColour(int tagId) {

        return switch (tagId) {
            case (0) -> "purple";
            case (1) -> "green";
            case (2) -> "blue";
            case (3) -> "red";
            case (4) -> "yellow";
            default -> "";
        };
    }
}
