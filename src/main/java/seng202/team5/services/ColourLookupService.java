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

    /**
     * Get the colour to use (hexadecimal) from the tag ID.
     *
     * @param tagId the ID to use
     * @return the tag's hexadecimal colour
     */
    public static String getColourValue(int tagId) {
        return switch (tagId) {
            case (0) -> "#9f8fef";
            case (1) -> "#aeee85";
            case (2) -> "#8ddCdC";
            case (3) -> "#ef8f8f";
            case (4) -> "#efcd8f";
            case (5) -> "#e58fef";
            default -> "white";
        };
    }


    /**
     * Get the colour id use from the colour name.
     *
     * @param colourName the name to use
     * @return the colour id
     */
    public static int getColourName(String colourName) {
        return switch (colourName) {
            case ("lavender") -> 0;
            case ("mint") -> 1;
            case ("seafoam") -> 2;
            case ("claret") -> 3;
            case ("gold") -> 4;
            case ("rose") -> 5;
            default -> -1;
        };
    }
}
