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

    @Test
    public void testTruncateDatabase() {
        wineDAO.add(new Wine(1, "wine name", "description", 2014, 85, 20, "pinot noir", "white", new Vineyard("vineyard", "region")));
        assertEquals(1, wineDAO.getAll().size());
        wineDAO.truncateWines();
        assertEquals(0, wineDAO.getAll().size());
    }

    /**
     * test adding a list including some invalid wines.
     * eg :wines with negative values
     * wineDAO should only include the valid wines from the list
     */
    @Test
    public void testBatchaddInvalidWine() {

        List<Wine> someInvalidWines = new ArrayList<>();
        someInvalidWines.add(new Wine(1, "regular valid wine", "description", 2014, 85, 20, "pinot noir", "white", new Vineyard("vineyard", "region")));
        someInvalidWines.add(new Wine(2, "", "a wine with an empty name", 2014, 85, 20, "pinot noir", "white", new Vineyard("vineyard", "region")));
        someInvalidWines.add(new Wine(3, "0 year wine", "wine from year 0", 0, 85, 20, "pinot noir", "white", new Vineyard("vineyard", "region")));
        someInvalidWines.add(new Wine(4, "20 year wine", "wine from year 20", 20, 85, 20, "pinot noir", "white", new Vineyard("vineyard", "region")));
        someInvalidWines.add(new Wine(5, "future wine", "wine from year 10000", 10000, 85, 20, "pinot noir", "white", new Vineyard("vineyard", "region")));
        someInvalidWines.add(new Wine(6, "negative price wine", "wine with price -10", 2000, 85, -10, "pinot noir", "white", new Vineyard("vineyard", "region")));
        someInvalidWines.add(new Wine(7, "negative score wine", "wine with score -1", 2000, -1, 10, "pinot noir", "white", new Vineyard("vineyard", "region")));
        someInvalidWines.add(new Wine(8, "101 score wine", "wine with score 101", 2000, 101, 10, "pinot noir", "white", new Vineyard("vineyard", "region")));
        wineDAO.batchAdd(someInvalidWines);
        assertEquals(1, wineDAO.getAll().size());
    }

    /**
     * test adding invalid wines individually using the wineDAO add method
     * should only add the wine if it has valid attributes
     */
    @Test
    public void testAddSingleInvalidWine() {
        Wine emptyWineName = new Wine(2, "", "a wine with an empty name", 2014, 85, 20, "pinot noir", "white", new Vineyard("vineyard", "region"));
        wineDAO.add(emptyWineName);
        Wine year0Wine = new Wine(3, "0 year wine", "wine from year 0", 0, 85, 20, "pinot noir", "white", new Vineyard("vineyard", "region"));
        wineDAO.add(year0Wine);
        assertEquals(0, wineDAO.getAll().size());
    }
}
