package seng202.team5.unittests.respository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Drinks;
import seng202.team5.models.User;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.DatabaseService;
import seng202.team5.repository.DrinksDAO;
import seng202.team5.repository.UserDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

/**
 * Unit tests for the DrinksDAO.
 *
 * @author Martyn Gascoigne
 */
public class DrinksDAOTest {
    private static DrinksDAO drinksDAO;
    private static WineDAO wineDAO;
    private static UserDAO userDAO;
    private static DatabaseService databaseService;


    /**
     * Set up the testing scenario.
     */
    @BeforeAll
    public static void setUp() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test.db");
        VineyardDAO vineyardDAO = new VineyardDAO();

        wineDAO = new WineDAO(vineyardDAO);
        userDAO = new UserDAO();
        drinksDAO = new DrinksDAO();
    }


    /**
     * Reset the database before each test
     */
    @BeforeEach
    public void resetDb() {
        databaseService.resetDb();
    }


    /**
     * Test that the database is empty on creation.
     */
    @Test
    public void testEmptyOnCreation() {
        assertEquals(0, drinksDAO.getAll().size());
    }


    /**
     * Test getting all reviews.
     */
    @Test
    public void testGetAll() throws DuplicateEntryException {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine =
                new Wine("Test Wine", "Test Wine 1 is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));

        Wine testWine2 =
                new Wine("Test Wine 2", "Test Wine 2 is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine2.setId(wineDAO.add(testWine2));

        User testUser = new User("Test User", "password", "user", 0);
        testUser.setId(userDAO.add(testUser));

        User testUser2 = new User("Test User 2", "password", "user", 0);
        testUser2.setId(userDAO.add(testUser2));

        Drinks testReview1 = new Drinks(testWine.getId(), testUser.getId(), true, "Great wine1", 5);
        Drinks testReview2 =
                new Drinks(testWine.getId(), testUser2.getId(), true, "Great wine2", 5);
        Drinks testReview3 =
                new Drinks(testWine2.getId(), testUser.getId(), true, "Great wine3", 5);
        Drinks testReview4 =
                new Drinks(testWine2.getId(), testUser2.getId(), true, "Great wine4", 5);

        drinksDAO.add(testReview1);
        drinksDAO.add(testReview2);
        drinksDAO.add(testReview3);
        drinksDAO.add(testReview4);

        List<Drinks> reviews = drinksDAO.getAll();
        assertEquals(4, reviews.size());
    }


    /**
     * Test getting a single review from the database.
     */
    @Test
    public void testGetWineReview() throws DuplicateEntryException {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine =
                new Wine("Test Wine", "Test Wine is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));

        User testUser = new User("Test User", "password", "user", 0);
        testUser.setId(userDAO.add(testUser));

        Drinks testReview = new Drinks(testWine.getId(), testUser.getId(), true, "Great wine", 5);

        drinksDAO.add(testReview);

        Drinks review = drinksDAO.getWineReview(testWine.getId(), testUser.getId());
        assertNotNull(review);

        assertEquals(testReview, review);
    }


    /**
     * Test getting all reviews from a given user.
     */
    @Test
    public void testGetAllUser() throws DuplicateEntryException {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine =
                new Wine("Test Wine", "Test Wine 1 is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));

        Wine testWine2 =
                new Wine("Test Wine 2", "Test Wine 2 is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine2.setId(wineDAO.add(testWine2));

        Wine testWine3 =
                new Wine("Test Wine 3", "Test Wine 3 is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine3.setId(wineDAO.add(testWine3));

        User testUser = new User("Test User", "password", "user", 0);
        testUser.setId(userDAO.add(testUser));

        User testUser2 = new User("Test User 2", "password", "user", 0);
        testUser2.setId(userDAO.add(testUser2));

        Drinks testReview1 = new Drinks(testWine.getId(), testUser.getId(), true, "Great wine1", 5);
        Drinks testReview2 =
                new Drinks(testWine.getId(), testUser2.getId(), true, "Great wine2", 5);
        Drinks testReview3 =
                new Drinks(testWine2.getId(), testUser.getId(), true, "Great wine3", 5);
        Drinks testReview4 =
                new Drinks(testWine2.getId(), testUser2.getId(), true, "Great wine4", 5);
        Drinks testReview5 =
                new Drinks(testWine3.getId(), testUser.getId(), true, "Great wine5", 5);

        drinksDAO.add(testReview1);
        drinksDAO.add(testReview2);
        drinksDAO.add(testReview3);
        drinksDAO.add(testReview4);
        drinksDAO.add(testReview5);

        List<Drinks> reviews1 = drinksDAO.getFromUser(testUser.getId());
        assertEquals(3, reviews1.size());

        List<Drinks> reviews2 = drinksDAO.getFromUser(testUser2.getId());
        assertEquals(2, reviews2.size());
    }


    /**
     * Test getting all reviews on a given wine.
     */
    @Test
    public void testGetAllWine() throws DuplicateEntryException {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine =
                new Wine("Test Wine", "Test Wine 1 is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));

        Wine testWine2 =
                new Wine("Test Wine 2", "Test Wine 2 is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine2.setId(wineDAO.add(testWine2));

        Wine testWine3 =
                new Wine("Test Wine 3", "Test Wine 3 is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine3.setId(wineDAO.add(testWine3));

        User testUser = new User("Test User", "password", "user", 0);
        testUser.setId(userDAO.add(testUser));

        User testUser2 = new User("Test User 2", "password", "user", 0);
        testUser2.setId(userDAO.add(testUser2));

        Drinks testReview1 = new Drinks(testWine.getId(), testUser.getId(), true, "Great wine1", 5);
        Drinks testReview2 =
                new Drinks(testWine.getId(), testUser2.getId(), true, "Great wine2", 5);
        Drinks testReview3 =
                new Drinks(testWine2.getId(), testUser.getId(), true, "Great wine3", 5);
        Drinks testReview4 =
                new Drinks(testWine2.getId(), testUser2.getId(), true, "Great wine4", 5);
        Drinks testReview5 =
                new Drinks(testWine3.getId(), testUser.getId(), true, "Great wine5", 5);

        drinksDAO.add(testReview1);
        drinksDAO.add(testReview2);
        drinksDAO.add(testReview3);
        drinksDAO.add(testReview4);
        drinksDAO.add(testReview5);

        List<Drinks> reviews1 = drinksDAO.getFromWine(testWine.getId());
        assertEquals(2, reviews1.size());

        List<Drinks> reviews2 = drinksDAO.getFromWine(testWine2.getId());
        assertEquals(2, reviews2.size());

        List<Drinks> reviews3 = drinksDAO.getFromWine(testWine3.getId());
        assertEquals(1, reviews3.size());
    }

    /**
     * Test adding a single review to the database.
     */
    @Test
    public void testAdd() throws DuplicateEntryException {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine =
                new Wine("Test Wine", "Test Wine is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));

        User testUser = new User("Test User", "password", "user", 0);
        testUser.setId(userDAO.add(testUser));

        Drinks testReview = new Drinks(testWine.getId(), testUser.getId(), true, "Great wine", 5);

        drinksDAO.add(testReview);
        assertEquals(1, drinksDAO.getAll().size());
    }


    /**
     * Test the DAO deleting a review.
     */
    @Test
    public void testDelete() throws DuplicateEntryException {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine =
                new Wine("Test Wine", "Test Wine is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));

        User testUser = new User("Test User", "password", "user", 0);
        testUser.setId(userDAO.add(testUser));

        Drinks testReview = new Drinks(testWine.getId(), testUser.getId(), true, "Great wine", 5);

        drinksDAO.add(testReview);
        assertEquals(1, drinksDAO.getAll().size());

        drinksDAO.delete(testWine.getId(), testUser.getId());

        assertEquals(0, drinksDAO.getAll().size());
    }

    /**
     * Test the DAO updating a review.
     */
    @Test
    public void testUpdate() throws DuplicateEntryException {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine =
                new Wine("Test Wine", "Test Wine is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));

        User testUser = new User("Test User", "password", "user", 0);
        testUser.setId(userDAO.add(testUser));

        Drinks testReview = new Drinks(testWine.getId(), testUser.getId(), true, "Great wine", 5);

        drinksDAO.add(testReview);

        assertEquals("Great wine",
                drinksDAO.getWineReview(testWine.getId(), testUser.getId()).getNotes());
        testReview.setNotes("Could be better");

        drinksDAO.update(testReview);

        assertEquals("Could be better",
                drinksDAO.getWineReview(testWine.getId(), testUser.getId()).getNotes());
    }


    /**
     * Test a scenario when a user is deleted
     */
    @Test
    public void testUserDeleteScenario() throws DuplicateEntryException {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine =
                new Wine("Test Wine", "Test Wine 1 is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));

        Wine testWine2 =
                new Wine("Test Wine 2", "Test Wine 2 is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine2.setId(wineDAO.add(testWine2));

        User testUser = new User("Test User", "password", "user", 0);
        testUser.setId(userDAO.add(testUser));

        User testUser2 = new User("Test User 2", "password", "user", 0);
        testUser2.setId(userDAO.add(testUser2));

        Drinks testReview1 = new Drinks(testWine.getId(), testUser.getId(), true, "Great wine1", 5);
        Drinks testReview2 =
                new Drinks(testWine.getId(), testUser2.getId(), true, "Great wine2", 5);
        Drinks testReview3 =
                new Drinks(testWine2.getId(), testUser.getId(), true, "Great wine3", 5);
        Drinks testReview4 =
                new Drinks(testWine2.getId(), testUser2.getId(), true, "Great wine4", 5);

        drinksDAO.add(testReview1);
        drinksDAO.add(testReview2);
        drinksDAO.add(testReview3);
        drinksDAO.add(testReview4);

        List<Drinks> reviews = drinksDAO.getAll();
        assertEquals(4, reviews.size());

        userDAO.delete(testUser.getId());

        assertEquals(2, drinksDAO.getAll().size());
    }


    /**
     * Test a scenario when a wine is deleted
     */
    @Test
    public void testWineDeleteScenario() throws DuplicateEntryException {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine =
                new Wine("Test Wine", "Test Wine 1 is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));

        Wine testWine2 =
                new Wine("Test Wine 2", "Test Wine 2 is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine2.setId(wineDAO.add(testWine2));

        User testUser = new User("Test User", "password", "user", 0);
        testUser.setId(userDAO.add(testUser));

        User testUser2 = new User("Test User 2", "password", "user", 0);
        testUser2.setId(userDAO.add(testUser2));

        Drinks testReview1 = new Drinks(testWine.getId(), testUser.getId(), true, "Great wine1", 5);
        Drinks testReview2 =
                new Drinks(testWine.getId(), testUser2.getId(), true, "Great wine2", 5);
        Drinks testReview3 =
                new Drinks(testWine2.getId(), testUser.getId(), true, "Great wine3", 5);
        Drinks testReview4 =
                new Drinks(testWine2.getId(), testUser2.getId(), true, "Great wine4", 5);

        drinksDAO.add(testReview1);
        drinksDAO.add(testReview2);
        drinksDAO.add(testReview3);
        drinksDAO.add(testReview4);

        List<Drinks> reviews = drinksDAO.getAll();
        assertEquals(4, reviews.size());

        wineDAO.delete(testUser.getId());

        assertEquals(2, drinksDAO.getAll().size());
    }
}