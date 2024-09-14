package seng202.team5.unittests.respository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.DatabaseService;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.RegionService;
import seng202.team5.services.WineVarietyService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WineDAOTest {
    private static final Logger log = LogManager.getLogger(WineDAOTest.class);
    private static WineDAO wineDAO;
    private static DatabaseService databaseService;

    /**
     * Set up service classes for tests.
     * @throws InstanceAlreadyExistsException
     */

    @BeforeAll
    public static void setUp() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test.db");
        WineVarietyService wineVarietyService = new WineVarietyService();
        RegionService regionService = new RegionService();
        VineyardDAO vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);
    }

    /**
     * Resets the database before each test.
     */
    @BeforeEach
    public void resetDb() {
        databaseService.resetDb();
    }

    /**
     * Tests the database is empty on creation.
     */
    @Test
    public void testEmptyOnCreation() {
        assertEquals(0, wineDAO.getAll().size());
    }

    /**
     * Tests the getAll() method. Asssertion of number of entries, and first entry is correct.
     */
    @Test
    public void testGetAll() {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine1 = new Wine("Test Wine 1", "Test Wine is a nice wine", 2024, 99, 7.99, "Pinot Noir", "Red", testVineyard);
        Wine testWine2 = new Wine("Test Wine 2", "Test Wine is a nice wine", 2013, 43, 5.99, "Pinot gris", "White", testVineyard);
        Wine testWine3 = new Wine("Test Wine 3", "Test Wine is a nice wine", 2007, 45, 3.99, "Pinot grigio", "Red", testVineyard);
        wineDAO.add(testWine1);
        wineDAO.add(testWine2);
        wineDAO.add(testWine3);

        assertEquals(3,wineDAO.getAll().size());
        assertEquals("Test Wine 1", wineDAO.getAll().get(0).getName());

    }

    /**
     * Test the getOne() method. Assertion of the same object retrieved as is input.
     */
    @Test
    public void testGetOne() {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine = new Wine("Test Wine", "Test Wine is a nice wine", 2024, 99, 7.99, "Pinot Noir", "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));
        Wine wine = wineDAO.getOne(1);
        assertNotNull(wine);

        assertEquals(testWine, wine);
    }

    /**
     * Test the add method of a single wine entry.
     */

    @Test
    public void testAdd() {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine = new Wine("Test Wine", "Test Wine is a nice wine", 2024, 99, 7.99, "Pinot Noir", "Red", testVineyard);
        testWine.setId(wineDAO.add(testWine));
        Wine wine = wineDAO.getAll().getFirst();
        assertNotNull(wine);

        assertEquals(testWine, wine);
    }

    /**
     * Tests the batch add of adding multiple entries at once.
     */
    @Test
    public void testBatchAdd() {
        Vineyard testVineyard1 = new Vineyard("tv1","testreg1");

        Wine testWine1 = new Wine("testWine", "tasty", 2023, 85, 15.99, "testVariety","Red", testVineyard1);
        Wine testWine2 = new Wine("testWine2", "tasty", 2023, 85, 15.99, "testVariety","Red", testVineyard1);
        Wine testWine3 = new Wine("testWine3", "tasty", 2023, 85, 15.99, "testVariety","Red", testVineyard1);

        List<Wine> testWineList = new ArrayList<Wine>(){{add(testWine1); add(testWine2); add(testWine3);}};

        wineDAO.batchAdd(testWineList);
        assertEquals(3,wineDAO.getAll().size());
    }

    /**
     * Tests delete method
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

    @org.junit.jupiter.api.Nested
    class testNest {

        String sql;
        @BeforeEach
        public void init(){
            Vineyard testVineyard1 = new Vineyard("Test Vineyard", "Test Region");
            Vineyard testVineyard2 = new Vineyard("Test Vineyard", "Test Region");

            Wine testWine1 = new Wine("Test Wine 1 delish", "YUMMY", 2005,
                    88, 32, "testVariety", "Red", testVineyard1);
            Wine testWine2 = new Wine("Test Wine 2 delish", "OKAY", 2005,
                    88, 98, "testVariety", "White", testVineyard2);
            Wine testWine3 = new Wine("Test Wine 3", "YUCK", 2021,
                    96, 54, "testVariety", "Red", testVineyard1);
            Wine testWine4 = new Wine("Test Wine 4 delish", "GROSS", 2005,
                    65, 34, "testVariety", "White", testVineyard2);
            Wine testWine5 = new Wine("Test Wine 5", "PERFECT", 2008,
                    88, 25, "testVariety", "Red", testVineyard1);
            wineDAO.add(testWine1);
            wineDAO.add(testWine2);
            wineDAO.add(testWine3);
            wineDAO.add(testWine4);
            wineDAO.add(testWine5);
            sql = "SELECT DISTINCT wine.id, wine.name, wine.description, wine.year, wine.rating, "
                    + "wine.variety, wine.price, wine.colour, vineyard.name AS vineyardName, vineyard.region "
                    + "FROM WINE, VINEYARD WHERE vineyard.id = wine.vineyard"
                    + " AND (wine.name LIKE ? OR wine.description LIKE ?) ";
        }
        /**
         * Tests the executeSearchFilter method for no filters applied
         */
        @Test
        public void testExecuteSearchFilter(){
            List<Wine> result = wineDAO.executeSearchFilter(sql, "");
            Assertions.assertEquals(5, result.size());
        }

        /**
         * Tests executeSearchFilter method for single search term in wine description
         */
        @Test
        public void testSearchFilter(){
            List<Wine> result = wineDAO.executeSearchFilter(sql, "YUMMY");
            System.out.println(result.get(0).getDescription());
            Assertions.assertEquals(1, result.size());
            Assertions.assertTrue(result.get(0).getDescription().contains("YUMMY"));
        }

        /**
         * Test executeSearchFilter method for single year filter
         */
        @Test
        public void testYearFilter() {
            sql += " AND wine.year = 2005;";
            List<Wine> result = wineDAO.executeSearchFilter(sql, "");
            for (Wine wine: result) {
                Assertions.assertEquals("2005", String.valueOf(wine.getYear()));
            }
            Assertions.assertEquals(3, result.size());
        }


        /**
         * Tests executeSearchFilter method for search and multiple filter values
         */
        @Test
        public void testSearchAndFilter() {
            sql += " AND wine.year = 2005 ";
            sql += " AND wine.rating = 88;";

            List<Wine> result = wineDAO.executeSearchFilter(sql, "delish");
            for (Wine wine: result) {
                Assertions.assertEquals(2005, wine.getYear());
                Assertions.assertEquals(88, wine.getRating());
                Assertions.assertTrue(wine.getName().contains("delish"));
            }
            Assertions.assertEquals(2, result.size());
        }

    }


}
