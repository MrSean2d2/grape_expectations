package seng202.team5.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import seng202.team5.models.Review;
import seng202.team5.models.Tag;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

/**
 * Service class that handles operations related to the dashboard.
 */
public class DashboardService {

    private final int userId;
    private final VineyardDAO vineyardDAO;
    private final WineDAO wineDAO;
    private final ReviewDAO reviewDAO;
    private static DashboardService instance;

    private List<Review> userReviews;
    private Map<String, Integer> varietyMap = new HashMap<>();
    private Map<String, Integer> regionMap = new HashMap<>();
    private Map<Integer, Integer> yearMap = new HashMap<>();
    private Map<String, Integer> colourMap = new HashMap<>();
    private Map<String, Integer> tagMap = new HashMap<>();
    private String selectedCategory;
    private String selectedFilterTerm;

    /**
     * Constructor for Dashboard Services.
     *
     * @param userId of logged in user
     * @param vineyardDAO DAO for vineyard operations
     * @param wineDAO DAO for wine operations
     * @param reviewDAO DAO for review operations
     */
    public DashboardService(int userId, VineyardDAO vineyardDAO,
                            WineDAO wineDAO, ReviewDAO reviewDAO) {
        this.userId = userId;
        this.vineyardDAO = vineyardDAO;
        this.wineDAO = wineDAO;
        this.reviewDAO = reviewDAO;
        initializeData();
    }

    /**
     * Empty constructor for creating/retrieving instances of this service class.
     */
    public DashboardService() {
        this.userId = 1;
        this.vineyardDAO = new VineyardDAO();
        this.wineDAO = new WineDAO(vineyardDAO);
        this.reviewDAO = new ReviewDAO();
    }

    /**
     * Gets the singleton instance of DashboardService.
     *
     * @return the singleton instance of DashboardService
     */
    public static DashboardService getInstance() {
        if (instance == null) {
            instance = new DashboardService();
        }
        return instance;
    }

    /**
     * Initialises data for service, aggregates ratings by variety, region, year.
     */
    public void initializeData() {
        userReviews = reviewDAO.getFromUser(userId);
        TagsDAO tagsDAO = new TagsDAO();

        // Create a hash map for each property
        for (Review review : userReviews) {
            Wine wine = wineDAO.getOne(review.getWineId());
            Vineyard vineyard = wine.getVineyard();
            List<Tag> tags = tagsDAO.getFromWine(review.getWineId());

            // Populate the maps
            varietyMap.merge(wine.getWineVariety(), review.getRating(), Integer::sum);
            regionMap.merge(vineyard.getRegion(), review.getRating(), Integer::sum);
            yearMap.merge(wine.getYear(), review.getRating(), Integer::sum);
            colourMap.merge(wine.getWineColour(), review.getRating(), Integer::sum);
            for (Tag tag : tags) {
                tagMap.merge(tag.getName(), review.getRating(), Integer::sum);
            }
        }

    }

    /**
     * Retrieves the top varieties based on user ratings.
     *
     * @return sorted list of entries containing wine varieties
     */
    public List<Map.Entry<String, Integer>> getTopVariety() {
        return sortHashMap(varietyMap);
    }

    /**
     * Retrieves the top regions based on user ratings.
     *
     * @return sorted list of entries containing wine region
     */
    public List<Map.Entry<String, Integer>> getTopRegion() {
        return sortHashMap(regionMap);
    }

    /**
     * Retrieves the top years based on user ratings.
     *
     * @return sorted list of entries containing wine year
     */
    public List<Map.Entry<Integer, Integer>> getTopYear() {
        return sortHashMap(yearMap);
    }

    /**
     * Retrieves the top colour based on user ratings.
     *
     * @return sorted list of entries containing wine colour
     */
    public List<Map.Entry<String, Integer>> getTopColour() {
        return sortHashMap(colourMap);
    }

    /**
     * Retrieves the top tags based on user ratings.
     *
     * @return sorted list of entries containing wine tags
     */
    public List<Map.Entry<String, Integer>> getTopTags() {
        return sortHashMap(tagMap);
    }

    /**
     * Retrieves the list of user reviews.
     *
     * @return the list of reviews for the logged-in user
     */
    public List<Review> getUserReviews() {
        return userReviews;
    }

    /**
     * Find the max value of a hash map.
     *
     * @param inputMap the map to search
     * @return the maximum entry if found, null otherwise.
     */
    public <K, V extends Comparable<V>> List<Map.Entry<K, V>> sortHashMap(Map<K, V> inputMap) {

        List<Map.Entry<K, V>> list = new ArrayList<>(inputMap.entrySet());
        list.sort(Map.Entry.<K, V>comparingByValue().reversed());

        return list;
    }

    /**
     * Sets the selected category and filter term for pie slice searches.
     *
     * @param selectedCategory category selected for filtering
     * @param selectedFilterTerm filter term applied
     */
    public void setSelectedPieSliceSearch(String selectedCategory, String selectedFilterTerm) {
        this.selectedCategory = selectedCategory;
        this.selectedFilterTerm = selectedFilterTerm;

    }

    /**
     * Retrieves the selected pie slice search criteria.
     *
     * @return a list containing the selected category and filter term
     */
    public List<String> getSelectedPieSliceSearch() {
        List<String> selectedValues = new ArrayList<>();
        selectedValues.add(selectedCategory);
        selectedValues.add(selectedFilterTerm);
        return selectedValues;

    }



}
