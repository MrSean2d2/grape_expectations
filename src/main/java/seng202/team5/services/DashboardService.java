package seng202.team5.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import seng202.team5.models.Review;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

/**
 * Service class for the dashboard page.
 */
public class DashboardService {

    private final int userID;
    private final VineyardDAO vineyardDAO;
    private final WineDAO wineDAO;
    private final ReviewDAO reviewDAO;

    private List<Review> userReviews;
    private Map<String, Integer> varietyMap = new HashMap<>();
    private Map<String, Integer> regionMap = new HashMap<>();
    private Map<Integer, Integer> yearMap = new HashMap<>();

    String sql = "SELECT Count(*) FROM __ group by ";

    /**
     * Get pie chart numbers.
     */
    public DashboardService(int userID, VineyardDAO vineyardDAO, WineDAO wineDAO, ReviewDAO reviewDAO) {
        this.userID = userID;
        this.vineyardDAO = vineyardDAO;
        this.wineDAO = wineDAO;
        this.reviewDAO = reviewDAO;
        initializeData();
    }

    /**
     * Initialise the data.
     */
    public void initializeData() {
        userReviews = reviewDAO.getFromUser(userID);

        // Create a hash map for each property
        for (Review review : userReviews) {
            Wine wine = wineDAO.getOne(review.getWineId());
            Vineyard vineyard = wine.getVineyard();

            // Populate the maps
            varietyMap.merge(wine.getWineVariety(), review.getRating(), Integer::sum);
            regionMap.merge(vineyard.getRegion(), review.getRating(), Integer::sum);
            yearMap.merge(wine.getYear(), review.getRating(), Integer::sum);
        }

    }

    /**
     * Get a list of the user's top varieties.
     *
     * @return a sorted list of the user's top varieties
     */
    public List<Map.Entry<String, Integer>> getTopVariety() {
        return sortHashMap(varietyMap);
    }

    /**
     * Get a list of the user's top regions.
     *
     * @return a sorted list of the user's top regions
     */
    public List<Map.Entry<String, Integer>> getTopRegion() {
        return sortHashMap(regionMap);
    }

    /**
     * Get a list of the user's top years.
     *
     * @return a sorted list of the user's top years
     */
    public List<Map.Entry<Integer, Integer>> getTopYear() {
        return sortHashMap(yearMap);
    }

    /**
     * Get a list of the user's reviews.
     *
     * @return a list of the user's reviews
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
    // region pie chart
    // variety
    // tags


    /**
     * Get the user's preferences.
     */
    // top region
    // top variety
    // Most used tag?
}
