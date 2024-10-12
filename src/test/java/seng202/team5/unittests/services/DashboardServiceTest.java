package seng202.team5.unittests.services;


import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Review;
import seng202.team5.models.Role;
import seng202.team5.models.User;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.UserDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.DashboardService;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.UserService;

/**
 * Unit tests for Dashboard Service Class
 */
public class DashboardServiceTest {
    private DashboardService dashboardService;
    private ReviewDAO reviewDAO;
    private WineDAO wineDAO;
    private VineyardDAO vineyardDAO;
    private TagsDAO tagsDAO;
    public static DatabaseService databaseService;

    /**
     * Sets up database.
     *
     * @throws DuplicateEntryException if duplicate entry added.
     * @throws InstanceAlreadyExistsException if entry already exists.
     */
    @BeforeAll
    public static void setup() throws DuplicateEntryException, InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test.db");
    }

    /**
     * Resets the database before each test.
     *
     * @throws DuplicateEntryException if duplicate entry added.
     */
    @BeforeEach
    public void resetDB() throws DuplicateEntryException {
        databaseService.resetDb();
        UserService.removeInstance();
        UserService userService = UserService.getInstance();
        userService.signOut();

        reviewDAO = new ReviewDAO();
        vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);
        UserDAO userDAO = new UserDAO();
        tagsDAO = new TagsDAO();

        User testUser = new User("Test User!", "password", Role.USER, 0);
        testUser.setId(userDAO.add(testUser));

        this.dashboardService = new DashboardService(testUser.getId(),
                vineyardDAO, wineDAO, reviewDAO);

        Vineyard vineyard1 = new Vineyard("Vineyard A", "Region A");
        Vineyard vineyard2 = new Vineyard("Vineyard B", "Region B");
        Vineyard vineyard3 = new Vineyard("Vineyard C", "Region C");

        Wine wine1 = new Wine("TestWine1", "delicious", 2020,
                65, 12, "Pinot Noir", "white", vineyard1);
        Wine wine2 = new Wine("TestWine2", "yummy", 2018,
                84, 38, "Syrah", "red", vineyard2);
        Wine wine3 = new Wine("TestWine3", "excellent", 2003,
                91, 55, "Sauvingon Blanc", "white", vineyard3);

        wine1.setId(wineDAO.add(wine1));
        wine2.setId(wineDAO.add(wine2));
        wine3.setId(wineDAO.add(wine3));

        reviewDAO.add(new Review(wine1.getId(), testUser.getId(), true, "Great", 5));
        reviewDAO.add(new Review(wine2.getId(), testUser.getId(), true, "Mid", 3));
        reviewDAO.add(new Review(wine3.getId(), testUser.getId(), true, "bad", 1));

        dashboardService.initializeData();
    }

    /**
     * Tests method retrieving the top wine varieties based on ratings.
     */
    @Test
    public void testGetTopVariety() {
        List<Map.Entry<String, Integer>> topVariety = dashboardService.getTopVariety();
        Assertions.assertEquals(3, topVariety.size());

        Assertions.assertEquals("Pinot Noir", topVariety.getFirst().getKey());
        Assertions.assertEquals(5, topVariety.getFirst().getValue().intValue());

    }

    /**
     * Tests method retrieving the top wine regions based on ratings.
     */
    @Test
    public void testTopRegion() {
        List<Map.Entry<String, Integer>> topRegion = dashboardService.getTopRegion();

        Assertions.assertEquals("Region A", topRegion.getFirst().getKey());
        Assertions.assertEquals(5, topRegion.getFirst().getValue().intValue());

    }

    /**
     * Tests method retrieving the top wine years based on ratings.
     */
    @Test
    public void testTopYear() {
        List<Map.Entry<Integer, Integer>> topYear = dashboardService.getTopYear();

        Assertions.assertEquals(2020, topYear.getFirst().getKey());
        Assertions.assertEquals(5, topYear.getFirst().getValue().intValue());

    }

    /**
     * Tests method retrieving the top wine colours based on ratings.
     */
    @Test
    public void testTopColour() {
        List<Map.Entry<String, Integer>> topColour = dashboardService.getTopColour();
        Assertions.assertEquals("white", topColour.getFirst().getKey());
    }

    //TODO: implement tag test
//    /**
//     * Tests method retrieving the top wine tags based on ratings
//     */
//    @Test
//    public void testTopTags(){
//        List<Map.Entry<String, Integer>> topTag = dashboardService.getTopTags();
//        Assertions.assertEquals("white", topTag.get(0).getKey());
//    }

    /**
     * Tests the sorting of Hashmaps containing wine varieties and years.
     * @throws DuplicateEntryException if a duplicate entry is added.
     */
    @Test
    public void testSortHashMap() throws DuplicateEntryException {
        Wine wine4 = new Wine("TestWine4", "nice", 2021,
                50, 10, "Pinot Noir", "red", vineyardDAO.getOne(1));
        wine4.setId(wineDAO.add(wine4));

        reviewDAO.add(new Review(wine4.getId(),
                dashboardService.getUserReviews().getFirst().getUserId(),
                true, "Decent", 2));

        dashboardService.initializeData();
        List<Map.Entry<String, Integer>> topVariety = dashboardService.getTopVariety();
        List<Map.Entry<Integer, Integer>> topYear = dashboardService.getTopYear();

        Assertions.assertEquals("Pinot Noir", topVariety.getFirst().getKey());

        Assertions.assertEquals(2020, topYear.getFirst().getKey().intValue());
    }

    /**
     * Tests method with an invalid label.
     */
    @Test
    public void testSetandGetSelectedPieSearchWithInvalidLabel(){
        String category = "Wine Variety";
        String filterTerm = "Pinot Noir";
        dashboardService.setSelectedPieSliceSearch(category,filterTerm);

        List<String> result = dashboardService.getSelectedPieSliceSearch();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2,result.size());
        Assertions.assertEquals(category,result.getFirst());
        Assertions.assertEquals(filterTerm,result.get(1));
    }

    /**
     * Tests the initialisation of data. Ensures user reviews is not null.
     */
    @Test
    public void testInitialiseData(){
        dashboardService.initializeData();
        Assertions.assertNotNull(dashboardService.getUserReviews());

    }
}
