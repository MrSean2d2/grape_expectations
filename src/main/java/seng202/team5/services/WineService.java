package seng202.team5.services;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.models.AssignedTag;
import seng202.team5.models.Wine;
import seng202.team5.repository.AssignedTagsDAO;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

/**
 * Service Class to manage wine actions.
 */
public class WineService {
    private Wine selectedWine;
    private final WineDAO wineDAO;
    private ObservableList<Wine> wineList;
    private static WineService instance;

    public WineService() {
        wineDAO = new WineDAO(new VineyardDAO());
    }

    /**
     * Populates the database.
     *
     * @param dataLoadService instance
     */
    public void populateDatabase(DataLoadService dataLoadService) {
        wineDAO.truncateWines();
        wineDAO.getVineyardDAO().truncateVineyards();
        List<Wine> wines = dataLoadService.processWinesFromCsv(true);
        wineDAO.batchAdd(wines);
    }

    /**
     * Get list of wines from the database.
     *
     * @return Wine list
     */
    public ObservableList<Wine> getWineList() {
        if (wineList == null) {
            List<Wine> dbWines = wineDAO.getAll();
            if (dbWines.isEmpty()) {
                populateDatabase(new DataLoadService());
                dbWines = wineDAO.getAll();
            }
            wineList = FXCollections.observableList(dbWines, Wine.extractor());
        }
        return wineList;
    }

    /**
     * Execute the search query and store the results in the observable wine list.
     *
     * @param search the search term
     * @param variety the variety
     * @param colour the colour
     * @param region the region
     * @param year the year
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param minRating the minimum rating
     */
    public void searchWines(String search, String variety,
                            String colour, String region,
                            String year, double minPrice,
                            double maxPrice, double minRating,
                            String selectedTag) {
        String sql = wineDAO.queryBuilder(search, variety, colour, region, year, minPrice,
                maxPrice, minRating);
        List<Wine> wines = wineDAO.executeSearchFilter(sql, search);
        wineList = FXCollections.observableList(wines, Wine.extractor());
        filterWinesByTag(selectedTag);
    }

    /**
     * Filters wines by the selected tag.
     *
     * @param selectedTag tag selected to filter by.
     */
    public void filterWinesByTag(String selectedTag) {
        if (UserService.getInstance().getCurrentUser() != null && selectedTag != null) {
            ReviewDAO reviewDAO = new ReviewDAO();
            TagsDAO tagsDAO = new TagsDAO();
            AssignedTagsDAO assignedTagsDAO = new AssignedTagsDAO();
            int currentUserId = UserService.getInstance().getCurrentUser().getId();

            List<Integer> wineIds;
            if (!selectedTag.equals("Tag")) {
                if (selectedTag.equals("All Reviews")) {
                    // Show all wines that the current user has reviewed
                    wineIds = reviewDAO.getIdsFromUser(currentUserId);
                    System.out.println("user id: " + currentUserId);
                } else {
                    // Filter reviews that contain the selected tag
                    int tagId = tagsDAO.getIdFromName(selectedTag, currentUserId);
                    wineIds = assignedTagsDAO.getTagsFromUser(currentUserId, tagId).stream()
                            .map(AssignedTag::getWineId).toList();

                }
                List<Wine> filteredWines = getWineList().stream()
                        .filter(wine -> wineIds.contains(wine.getId()))   // Tag filtering
                        .collect(Collectors.toList());

                wineList = FXCollections.observableList(filteredWines, Wine.extractor());
            }
        }
    }

    /**
     * Returns the singleton WineService instance.
     *
     * @return instance of wine service class
     */
    public static WineService getInstance() {
        if (instance == null) {
            instance = new WineService();
        }
        return instance;
    }


    /**
     * Sets selectedWine to the wine clicked on in DataListPage.
     *
     * @param wine the selected wine
     */
    public void setSelectedWine(Wine wine) {
        this.selectedWine = wine;
    }

    /**
     * Returns the wine selected in DataListPage.
     *
     * @return selectedWine
     */
    public Wine getSelectedWine() {
        return selectedWine;
    }

    /**
     * Check if the wine's name is a duplicate of one in the database.
     *
     * @param wineName the name of the wine to check
     * @return true if the wine's name is already taken, false otherwise
     */
    public boolean checkExistingWine(String wineName) {
        boolean result = false;
        try {
            Wine existing = wineDAO.getWineFromName(wineName);
            if (existing != null) {
                result = true;
            }
        } catch (NotFoundException e) {
            result = false;
        }
        return result;
    }

    /**
     * Returns true if the year is valid.
     *
     * @param year the year
     * @return true if valid, false otherwise
     */
    public boolean validYear(int year) {
        int currentYear = Year.now().getValue(); //gets current year for future-proofing
        return (year >= 1700 && year <= currentYear); //1700 is arbitrary boundary
    }

    /**
     * Returns true if the name is valid.
     *
     * @param name the name
     * @return true if valid, false otherwise
     */
    public boolean validName(String name) {
        return (!name.isBlank() && name.length() < 100);
    }

    /**
     * Returns true if the price is valid.
     *
     * @param price the price
     * @return true if valid, false otherwise
     */
    public boolean validPrice(double price) {
        return (price >= 0);
    }

    /**
     * Returns true if the description is valid.
     *
     * @param description the description
     * @return true if valid, false otherwise
     */
    public boolean validDescription(String description) {
        return (description.length() < 500);
    }

    /**
     * Returns true if the rating is valid.
     *
     * @param rating the rating
     * @return true if valid, false otherwise
     */
    public boolean validRating(int rating) {
        return (rating >= 0 && rating <= 100);
    }

    /**
     * Returns the passed colour if it is valid, otherwise a placeholder value
     * will be returned.
     *
     * @param colour the colour to validate
     * @return the passed colour if valid, otherwise the string "Unknown"
     */
    public String validColour(String colour) {
        return (colour.isBlank()) ? "Unknown" : colour;
    }

    /**
     * Returns a valid string for the variety string given. If the string given
     * is blank, then the value returned will be a placeholder value.
     *
     * @param wineVariety the variety string to validate
     * @return the passed wineVariety if non-blank, otherwise the string
     *         "Unknown Variety"
     */
    public String validVariety(String wineVariety) {
        return (wineVariety.isBlank()) ? "Unknown Variety" : wineVariety;
    }

    /**
     * checks if a wine has valid values for its attributes.
     * eg: year and price aren't 0
     *
     * @return boolean whether given wine is valid enough to be added to database
     */
    public boolean isValidWine(Wine wine) {
        return (validName(wine.getName()) && validYear(wine.getYear())
                && validRating(wine.getRating()) && validPrice(wine.getPrice()));
    }
}
