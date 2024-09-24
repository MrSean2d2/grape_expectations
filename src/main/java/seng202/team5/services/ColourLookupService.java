package seng202.team5.services;

public class ColourLookupService {

    public static String getTagColour(int tagId) {
        String col = "";
        switch (tagId) {
            case (0):
                col = "#9f8fef";
                break;
            case (1):
                col = "#aaef8f";
                break;
            case (2):
                col = "#8fb1ef";
                break;
            case (3):
                col = "#ef8f8f";
                break;
            case (4):
                col = "#efcd8f";
                break;
        }

        return col;
    }

    public static String getTagLabelColour(int tagId) {
        String col = "";
        switch (tagId) {
            case (0):
                col = "purple";
                break;
            case (1):
                col = "green";
                break;
            case (2):
                col = "blue";
                break;
            case (3):
                col = "red";
                break;
            case (4):
                col = "yellow";
                break;
        }

        return col;
    }
}
