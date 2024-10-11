package seng202.team5.services;

/**
 * Colour Lookup service for the tag system.
 *
 * @author Martyn Gascoigne
 */
public class ColourLookupService {

    /**
     * Get the colour to use (for stylesheet) from the tag ID.
     *
     * @param tagId the ID to use
     * @return the tag's style class
     */
    public static String getTagLabelColour(int tagId) {
        return switch (tagId) {
            case (0) -> "lavender";
            case (1) -> "mint";
            case (2) -> "seafoam";
            case (3) -> "claret";
            case (4) -> "gold";
            case (5) -> "rose";
            default -> "";
        };
    }
}
