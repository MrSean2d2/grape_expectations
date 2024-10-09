package seng202.team5.unittests.respository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.*;
import seng202.team5.repository.*;
import seng202.team5.services.DatabaseService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Unit tests for the AssignedTagsDAO.
 *
 * @author Martyn Gascoigne
 */
public class AssignedTagsDAOTest {
    private static TagsDAO tagsDAO;
    private static AssignedTagsDAO assignedTagsDAO;
    private static UserDAO userDAO;
    private static VineyardDAO vineyardDAO;
    private static WineDAO wineDAO;
    private static DatabaseService databaseService;

    public static int defaultTagCount;


    /**
     * Set up the testing scenario.
     */
    @BeforeAll
    public static void setUp() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test.db");

        assignedTagsDAO = new AssignedTagsDAO();
        tagsDAO = new TagsDAO();
        userDAO = new UserDAO();
        vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);
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
     * Because of the default tags that are added in the SQLite definition file,
     * the actual "empty" state of the database contains {defaultTagCount} default tags.
     */
    @Test
    public void testEmptyOnCreation() {
        assertEquals(0, assignedTagsDAO.getAll().size());
    }


    /**
     * Test getting all tags.
     */
    @Test
    public void testGetAll() throws DuplicateEntryException {
        User testUser = new User("test", "pass", Role.USER, 0);
        testUser.setId(userDAO.add(testUser));
        User testUser2 = new User("test2", "pass", Role.USER, 0);
        testUser2.setId(userDAO.add(testUser2));

        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine1 = new Wine("Test Wine 1",
                        "Test Wine is a nice wine",
                        2024,
                        99,
                        7.99,
                        "Pinot Noir",
                        "Red", testVineyard);

        testWine1.setId(wineDAO.add(testWine1));

        assignedTagsDAO.add(new AssignedTag(0, testUser.getId(), testWine1.getId()));
        assignedTagsDAO.add(new AssignedTag(1, testUser2.getId(), testWine1.getId()));
        assignedTagsDAO.add(new AssignedTag(2, testUser.getId(), testWine1.getId()));
        assignedTagsDAO.add(new AssignedTag(3, testUser.getId(), testWine1.getId()));

        List<AssignedTag> assignedTags = assignedTagsDAO.getAll();
        assertEquals(4, assignedTags.size());
    }


    /**
     * Test getting a single tag.
     */
    @Test
    public void testGetOne() throws DuplicateEntryException {
        User testUser = new User("test", "pass", Role.USER, 0);
        testUser.setId(userDAO.add(testUser));
        User testUser2 = new User("test2", "pass", Role.USER, 0);
        testUser2.setId(userDAO.add(testUser2));

        // Create a tag from this user to use
        Tag testTag = new Tag(testUser.getId(), "Test Tag", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        Tag testTag2 = new Tag(testUser.getId(), "Test Tag 2", 1);
        testTag2.setTagId(tagsDAO.add(testTag2));


        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine1 = new Wine("Test Wine 1",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine1.setId(wineDAO.add(testWine1));

        assignedTagsDAO.add(new AssignedTag(testTag.getTagId(), testUser.getId(), testWine1.getId()));
        assignedTagsDAO.add(new AssignedTag(testTag2.getTagId(), testUser2.getId(), testWine1.getId()));

        AssignedTag assignedTag = assignedTagsDAO.getSingleTag(testTag.getTagId(), testUser.getId(), testWine1.getId());
        Tag foundTag = tagsDAO.getOne(assignedTag.getTagId());
        assertEquals("Test Tag", foundTag.getName());

        AssignedTag assignedTag2 = assignedTagsDAO.getSingleTag(testTag2.getTagId(), testUser2.getId(), testWine1.getId());
        Tag foundTag2 = tagsDAO.getOne(assignedTag2.getTagId());
        assertEquals("Test Tag 2", foundTag2.getName());
    }


    /**
     * Test getting all of a user's assigned tags.
     */
    @Test
    public void testGetFromUser() throws DuplicateEntryException {
        User testUser = new User("test", "pass", Role.USER, 0);
        testUser.setId(userDAO.add(testUser));
        User testUser2 = new User("test2", "pass", Role.USER, 0);
        testUser2.setId(userDAO.add(testUser2));

        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine1 = new Wine("Test Wine 1",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine1.setId(wineDAO.add(testWine1));

        Wine testWine2 = new Wine("Test Wine 2",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine2.setId(wineDAO.add(testWine2));

        assignedTagsDAO.add(new AssignedTag(0, testUser.getId(), testWine1.getId()));
        assignedTagsDAO.add(new AssignedTag(1, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(2, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(0, testUser2.getId(), testWine1.getId()));

        List<AssignedTag> assignedTags = assignedTagsDAO.getFromUser(testUser.getId());
        assertEquals(3,  assignedTags.size());
    }


    /**
     * Test getting all of a user's assigned tags on a given wine.
     */
    @Test
    public void testGetFromUserOnGivenWine() throws DuplicateEntryException {
        User testUser = new User("test", "pass", Role.USER, 0);
        testUser.setId(userDAO.add(testUser));
        User testUser2 = new User("test2", "pass", Role.USER, 0);
        testUser2.setId(userDAO.add(testUser2));

        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine1 = new Wine("Test Wine 1",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine1.setId(wineDAO.add(testWine1));

        Wine testWine2 = new Wine("Test Wine 2",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine2.setId(wineDAO.add(testWine2));

        assignedTagsDAO.add(new AssignedTag(0, testUser.getId(), testWine1.getId()));
        assignedTagsDAO.add(new AssignedTag(1, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(2, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(0, testUser2.getId(), testWine1.getId()));

        List<AssignedTag> assignedTags = assignedTagsDAO.getAllAssigned(testWine1.getId(), testUser.getId());
        assertEquals(1,  assignedTags.size());

        assignedTags = assignedTagsDAO.getAllAssigned(testWine2.getId(), testUser.getId());
        assertEquals(2,  assignedTags.size());

        assignedTags = assignedTagsDAO.getAllAssigned(testWine1.getId(), testUser2.getId());
        assertEquals(1,  assignedTags.size());
    }

    /**
     * Test deleting an assigned tag.
     */
    @Test
    public void testDeleteAssignedTag() throws DuplicateEntryException {
        User testUser = new User("test", "pass", Role.USER, 0);
        testUser.setId(userDAO.add(testUser));
        User testUser2 = new User("test2", "pass", Role.USER, 0);
        testUser2.setId(userDAO.add(testUser2));

        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine1 = new Wine("Test Wine 1",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine1.setId(wineDAO.add(testWine1));

        Wine testWine2 = new Wine("Test Wine 2",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine2.setId(wineDAO.add(testWine2));

        assignedTagsDAO.add(new AssignedTag(0, testUser.getId(), testWine1.getId()));
        assignedTagsDAO.add(new AssignedTag(1, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(2, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(0, testUser2.getId(), testWine1.getId()));

        List<AssignedTag> assignedTags = assignedTagsDAO.getAll();
        assertEquals(4, assignedTags.size());

        assignedTagsDAO.deleteAssignedTag(0, testUser.getId(), testWine1.getId());
        assignedTags = assignedTagsDAO.getAll();

        assertEquals(3, assignedTags.size());
    }


    /**
     * Test deleting all of a user's tags.
     */
    @Test
    public void testDeleteAssignedTagsFromUser() throws DuplicateEntryException {
        User testUser = new User("test", "pass", Role.USER, 0);
        testUser.setId(userDAO.add(testUser));
        User testUser2 = new User("test2", "pass", Role.USER, 0);
        testUser2.setId(userDAO.add(testUser2));

        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine1 = new Wine("Test Wine 1",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine1.setId(wineDAO.add(testWine1));

        Wine testWine2 = new Wine("Test Wine 2",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine2.setId(wineDAO.add(testWine2));

        assignedTagsDAO.add(new AssignedTag(0, testUser.getId(), testWine1.getId()));
        assignedTagsDAO.add(new AssignedTag(1, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(2, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(0, testUser2.getId(), testWine1.getId()));

        List<AssignedTag> assignedTags = assignedTagsDAO.getAll();
        assertEquals(4, assignedTags.size());

        assignedTagsDAO.deleteFromUserId(testUser.getId());
        assignedTags = assignedTagsDAO.getAll();

        assertEquals(1, assignedTags.size());
    }

    /**
     * Test deleting all of a user's tags.
     */
    @Test
    public void testDeleteAssignedTagsFromUserSpecificWine() throws DuplicateEntryException {
        User testUser = new User("test", "pass", Role.USER, 0);
        testUser.setId(userDAO.add(testUser));
        User testUser2 = new User("test2", "pass", Role.USER, 0);
        testUser2.setId(userDAO.add(testUser2));

        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine1 = new Wine("Test Wine 1",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine1.setId(wineDAO.add(testWine1));

        Wine testWine2 = new Wine("Test Wine 2",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine2.setId(wineDAO.add(testWine2));

        assignedTagsDAO.add(new AssignedTag(0, testUser.getId(), testWine1.getId()));
        assignedTagsDAO.add(new AssignedTag(1, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(2, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(0, testUser2.getId(), testWine1.getId()));

        List<AssignedTag> assignedTags = assignedTagsDAO.getAll();
        assertEquals(4, assignedTags.size());

        assignedTagsDAO.deleteFromUserWineId(testUser.getId(), testWine2.getId());
        assignedTags = assignedTagsDAO.getAll();

        assertEquals(2, assignedTags.size());
    }

    /**
     * Test deleting all of a user's tags.
     */
    @Test
    public void testDeleteFromWine() throws DuplicateEntryException {
        User testUser = new User("test", "pass", Role.USER, 0);
        testUser.setId(userDAO.add(testUser));
        User testUser2 = new User("test2", "pass", Role.USER, 0);
        testUser2.setId(userDAO.add(testUser2));

        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine1 = new Wine("Test Wine 1",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine1.setId(wineDAO.add(testWine1));

        Wine testWine2 = new Wine("Test Wine 2",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine2.setId(wineDAO.add(testWine2));

        assignedTagsDAO.add(new AssignedTag(0, testUser.getId(), testWine1.getId()));
        assignedTagsDAO.add(new AssignedTag(1, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(2, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(0, testUser2.getId(), testWine1.getId()));

        List<AssignedTag> assignedTags = assignedTagsDAO.getAll();
        assertEquals(4, assignedTags.size());

        assignedTagsDAO.deleteFromWineId(testWine2.getId());
        assignedTags = assignedTagsDAO.getAll();

        assertEquals(2, assignedTags.size());
    }

    /**
     * Test deleting all of a user's tags.
     */
    @Test
    public void testDeleteFromTag() throws DuplicateEntryException {
        User testUser = new User("test", "pass", Role.USER, 0);
        testUser.setId(userDAO.add(testUser));
        User testUser2 = new User("test2", "pass", Role.USER, 0);
        testUser2.setId(userDAO.add(testUser2));

        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine1 = new Wine("Test Wine 1",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine1.setId(wineDAO.add(testWine1));

        Wine testWine2 = new Wine("Test Wine 2",
                "Test Wine is a nice wine",
                2024,
                99,
                7.99,
                "Pinot Noir",
                "Red", testVineyard);

        testWine2.setId(wineDAO.add(testWine2));

        assignedTagsDAO.add(new AssignedTag(0, testUser.getId(), testWine1.getId()));
        assignedTagsDAO.add(new AssignedTag(1, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(2, testUser.getId(), testWine2.getId()));
        assignedTagsDAO.add(new AssignedTag(0, testUser2.getId(), testWine1.getId()));

        List<AssignedTag> assignedTags = assignedTagsDAO.getAll();
        assertEquals(4, assignedTags.size());

        assignedTagsDAO.deleteFromTagId(0);
        assignedTags = assignedTagsDAO.getAll();

        assertEquals(2, assignedTags.size());
    }
}
