package seng202.team5.services;

import seng202.team5.models.Review;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardService {

    private final int userID;
    private final VineyardDAO vineyardDAO;
    private final WineDAO wineDAO;
    private final ReviewDAO reviewDAO;

    private List<Review> userReviews;
    private Map<String, Integer> varietyMap = new HashMap<>();
    private Map<String, Integer> regionMap = new HashMap<>();
    private Map<Integer, Integer> yearMap = new HashMap<>();



    /**
     * Get pie chart numbers
     */
    String sql = "SELECT Count(*) FROM __ group by ";

    public DashboardService(int userID, VineyardDAO vineyardDAO, WineDAO wineDAO, ReviewDAO reviewDAO) {
        this.userID = userID;
        this.vineyardDAO = vineyardDAO;
        this.wineDAO = wineDAO;
        this.reviewDAO = reviewDAO;
        initializeData();
    }

    private void initializeData() {
        userReviews = reviewDAO.getFromUser(userID);

        // Create a hash map for each property
        for(Review review : userReviews) {
            Wine wine = wineDAO.getOne(review.getWineId());
            Vineyard vineyard = wine.getVineyard();

            // Add to maps
            varietyMap.merge(wine.getWineVariety(), review.getRating(), Integer::sum);
            regionMap.merge(vineyard.getRegion(), review.getRating(), Integer::sum);
            yearMap.merge(wine.getYear(), review.getRating(), Integer::sum);
        }

    }
    public List<Map.Entry<String, Integer>> getTopVariety() {
        return sortHashMap(varietyMap);
    }
    public List<Map.Entry<String, Integer>> getTopRegion() {
        return sortHashMap(regionMap);
    }
    public List<Map.Entry<Integer, Integer>> getTopYear() {
        return sortHashMap(yearMap);
    }

    public List<Review> getUserReviews(){
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
//        System.out.println(list.getFirst().getKey());
        list.sort(Map.Entry.<K, V>comparingByValue().reversed());
//        System.out.println(list.getFirst().getKey());
        return list;
    }
    // region pie chart
    // variety
    // tags


    /**
     * get preferences
     */
    // top region
    // top variety
    // Most used tag?
}
