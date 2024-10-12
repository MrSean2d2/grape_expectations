package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Review;
import seng202.team5.models.User;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.ReviewService;
import seng202.team5.services.UserService;

/**
 * Tests for ReviewService.
 */
public class ReviewServiceTest {
    private ReviewService reviewService;
    private User user;
    private Wine wine;
    private static DatabaseService databaseService;

    /**
     * Initialise test database.
     * @throws InstanceAlreadyExistsException if the database instance already exists
     */
    @BeforeAll
    public static void setUp() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl("jdbc:sqlite:./src/test/resources/test.db");
        databaseService.resetDb();
    }

    /**
     * Initial setup before each test.
     */
    @BeforeEach
    public void init() {
        databaseService.resetDb();
        reviewService = new ReviewService();
        user = UserService.getInstance().registerUser("User", "password");
        UserService.getInstance().setCurrentUser(user);
        wine = new Wine("Test", "Nice", 1990, 89, 20, "Variety", "Red", new Vineyard("Vineyard", "Region"));
        WineDAO wineDAO = new WineDAO(new VineyardDAO());
        wine.setId(wineDAO.add(wine));
    }

    /**
     * Test creating a review.
     */
    @Test
    public void testCreateReview() {

        Review review = reviewService.createReviewIfNotExists(null, wine.getId(), user.getId());
        assertNotNull(review);
    }

    /**
     * Test not creating a review.
     *
     * @throws DuplicateEntryException if the different wine is a duplicate
     */
    @Test
    public void testReviewAlreadyExists() throws DuplicateEntryException {
        Review review = new Review(wine.getId(), user.getId());
        ReviewDAO reviewDAO = new ReviewDAO();
        reviewDAO.add(review);
        Wine differentWine = new Wine("Different", "Not nice", 1990, 89, 20,
                "Variety", "Red", new Vineyard("Vineyard", "Region"));
        WineDAO wineDAO = new WineDAO(new VineyardDAO());
        differentWine.setId(wineDAO.add(differentWine));
        Review result = reviewService.createReviewIfNotExists(review, differentWine.getId(),
                1);
        assertEquals(result, review);
    }
}
