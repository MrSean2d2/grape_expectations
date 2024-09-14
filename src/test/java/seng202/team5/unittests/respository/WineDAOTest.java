package seng202.team5.unittests.respository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.DatabaseService;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

/**
 * Unit tests for the WineDAO.
 *
 * @author Caitlin Tam
 * @author Sean Reitsma
 * @author Martyn Gascoigne
 */
public class WineDAOTest {
    private static WineDAO wineDAO;
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
        assertEquals(0, wineDAO.getAll().size());
    }


    /**
     * Test getting a single wine from the database.
     */
    @Test
    public void testGetOne() {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine =
                new Wine("Test Wine", "Test Wine is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));
        Wine wine = wineDAO.getOne(1);
        assertNotNull(wine);

        assertEquals(testWine, wine);
    }


    /**
     * Test retrieving a wine from the database via its name.
     */
    @Test
    public void testGetFromName() throws NotFoundException {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine =
                new Wine("Test Wine", "Test Wine is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));
        Wine wine = wineDAO.getWineFromName("Test Wine");
        assertNotNull(wine);

        assertEquals(wine, testWine);
    }


    /**
     * Test adding a single wine to the database.
     */
    @Test
    public void testAdd() {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine =
                new Wine("Test Wine", "Test Wine is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));
        Wine wine = wineDAO.getAll().getFirst();
        assertNotNull(wine);

        assertEquals(testWine, wine);
    }


    /**
     * Test adding a small batch of wines at once to the database.
     */
    @Test
    public void testBatchAdd() {
        Vineyard testVineyard1 = new Vineyard("tv1", "testreg1");
        Vineyard testVineyard2 = new Vineyard("tv2", "testreg2");
        Vineyard testVineyard3 = new Vineyard("tv3", "testreg3");

        Wine testWine1 =
                new Wine("testWine", "tasty", 2023, 85, 15.99, "testVariety", "Red", testVineyard1);
        Wine testWine2 = new Wine("testWine2", "tasty", 2023, 85, 15.99, "testVariety", "Red",
                testVineyard1);
        Wine testWine3 = new Wine("testWine3", "tasty", 2023, 85, 15.99, "testVariety", "Red",
                testVineyard1);

        List<Wine> testWineList = new ArrayList<>() {{
            add(testWine1);
            add(testWine2);
            add(testWine3);
        }};

        wineDAO.batchAdd(testWineList);
        assertEquals(3, wineDAO.getAll().size());
    }


    /**
     * Test the DAO deleting a wine.
     */
    @Test
    public void testDelete() {
        Vineyard testVineyard = new Vineyard("Test Vineyard", "Test Region");
        Wine testWine = new Wine("Test Wine", "A very nice wine", 2024,
                87, 200, "testVariety", "Red", testVineyard);
        int toDeleteID = wineDAO.add(testWine);

        wineDAO.delete(toDeleteID);

        assertEquals(0, wineDAO.getAll().size());

    }

    /**
     * Test the DAO updating a wine.
     */
    @Test
    public void testUpdate() {
        Vineyard testVineyard = new Vineyard("Test Vineyard", "Test Region");
        Wine testWine = new Wine("Test Wine", "A very nice wine", 2024,
                87, 200, "testVariety", "Red", testVineyard);
        int toUpdateID = wineDAO.add(testWine);
        testWine.setId(toUpdateID);

        assertEquals("Test Wine", wineDAO.getOne(toUpdateID).getName());
        assertEquals("Red", wineDAO.getOne(toUpdateID).getWineColour());
        assertEquals(toUpdateID, wineDAO.getOne(toUpdateID).getId());

        testWine.setName("Evil Wine");
        testWine.setColour("Blue");

        wineDAO.update(testWine);

        assertEquals("Evil Wine", wineDAO.getOne(toUpdateID).getName());
        assertEquals("Blue", wineDAO.getOne(toUpdateID).getWineColour());
    }
}
