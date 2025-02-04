package seng202.team5.unittests.respository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.DatabaseService;

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
     * Set up service classes for tests.
     *
     * @throws InstanceAlreadyExistsException if a singleton instance of
     *                                        {@link DatabaseService} already exists.
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
     * Resets the database before each test.
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
     * Tests the getAll() method. Assertion of number of entries, and first entry is correct.
     */
    @Test
    public void testGetAll() {
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine1 =
                new Wine("Test Wine 1", "Test Wine is a nice wine", 2024, 99, 7.99, "Pinot Noir",
                        "Red", testVineyard);
        Wine testWine2 =
                new Wine("Test Wine 2", "Test Wine is a nice wine", 2013, 43, 5.99, "Pinot gris",
                        "White", testVineyard);
        Wine testWine3 =
                new Wine("Test Wine 3", "Test Wine is a nice wine", 2007, 45, 3.99, "Pinot grigio",
                        "Red", testVineyard);
        wineDAO.add(testWine1);
        wineDAO.add(testWine2);
        wineDAO.add(testWine3);

        assertEquals(3, wineDAO.getAll().size());
        assertEquals("Test Wine 1", wineDAO.getAll().getFirst().getName());

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
     * Test that trying to retrieve a wine which is not in the database returns
     * null.
     */
    @Test
    public void testGetOneNoWines() {
        Wine wine = wineDAO.getOne(1);
        assertNull(wine);
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
     * Test that the {@link NotFoundException} is thrown when the wine can't be
     * found.
     */
    @Test
    public void testGetFromNameNoWines() {
        assertThrows(NotFoundException.class, () -> wineDAO.getWineFromName("A nice wine"));
    }

    /**
     * Test that {@link NotFoundException} is thrown when the wine can't be found.
     */
    @Test
    public void testGetFromNameNonExistentWine() {
        Vineyard vineyard = new Vineyard("Test", "Test");
        Wine testWine = new Wine("Test Wine", "Test Wine is a nice wine", 2024,
                99, 7.99, "Pinot Noir", "Red", vineyard);
        wineDAO.add(testWine);
        assertThrows(NotFoundException.class, () -> wineDAO.getWineFromName("Not test wine"));
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
                testVineyard2);
        Wine testWine3 = new Wine("testWine3", "tasty", 2023, 85, 15.99, "testVariety", "Red",
                testVineyard3);

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
        int toDeleteId = wineDAO.add(testWine);

        wineDAO.delete(toDeleteId);

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
        int toUpdateId = wineDAO.add(testWine);
        testWine.setId(toUpdateId);

        assertEquals("Test Wine", wineDAO.getOne(toUpdateId).getName());
        assertEquals("Red", wineDAO.getOne(toUpdateId).getWineColour());
        assertEquals(toUpdateId, wineDAO.getOne(toUpdateId).getId());

        testWine.setName("Evil Wine");
        testWine.setColour("Blue");

        wineDAO.update(testWine);

        assertEquals("Evil Wine", wineDAO.getOne(toUpdateId).getName());
        assertEquals("Blue", wineDAO.getOne(toUpdateId).getWineColour());
    }

    /**
     * Tests the clearing of database.
     */

    @Test
    public void testTruncateDatabase() {
        wineDAO.add(new Wine(1, "wine name", "description", 2014, 85, 20, "pinot noir", "white",
                new Vineyard("vineyard", "region")));
        assertEquals(1, wineDAO.getAll().size());
        wineDAO.truncateWines();
        assertEquals(0, wineDAO.getAll().size());
    }

    /**
     * Tests the executeQuery function for various search / filters.
     */
    @org.junit.jupiter.api.Nested
    class TestNest {

        /**
         * Set up test wine entries and base sql statement.
         */
        String sql;

        @BeforeEach
        public void init() {
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
                    + "wine.variety, wine.price, wine.colour, vineyard.name AS vineyardName, "
                    + "vineyard.region FROM WINE, VINEYARD WHERE vineyard.id = wine.vineyard"
                    + " AND (wine.name LIKE ? OR wine.description LIKE ?) ";
        }

        /**
         * Tests the executeSearchFilter method for no filters applied
         */
        @Test
        public void testExecuteSearchFilter() {
            List<Wine> result = wineDAO.executeSearchFilter(sql, "");
            Assertions.assertEquals(5, result.size());
        }

        /**
         * Tests executeSearchFilter method for single search term in wine description
         */
        @Test
        public void testSearchFilter() {
            List<Wine> result = wineDAO.executeSearchFilter(sql, "YUMMY");
            Assertions.assertEquals(1, result.size());
            Assertions.assertTrue(result.getFirst().getDescription().contains("YUMMY"));
        }

        /**
         * Test executeSearchFilter method for single year filter
         */
        @Test
        public void testYearFilter() {
            sql += " AND wine.year = 2005;";
            List<Wine> result = wineDAO.executeSearchFilter(sql, "");
            for (Wine wine : result) {
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
            for (Wine wine : result) {
                Assertions.assertEquals(2005, wine.getYear());
                Assertions.assertEquals(88, wine.getRating());
                Assertions.assertTrue(wine.getName().contains("delish"));
            }
            Assertions.assertEquals(2, result.size());
        }
    }


    /**
     * test adding a list including some invalid wines.
     * eg :wines with negative values
     * wineDAO should only include the valid wines from the list
     */
    @Test
    public void testBatchaddInvalidWine() {

        List<Wine> someInvalidWines = new ArrayList<>();
        someInvalidWines.add(
                new Wine(1, "regular valid wine", "description", 2014, 85, 20, "pinot noir",
                        "white", new Vineyard("vineyard", "region")));
        someInvalidWines.add(
                new Wine(2, "", "a wine with an empty name", 2014, 85, 20, "pinot noir", "white",
                        new Vineyard("vineyard", "region")));
        someInvalidWines.add(
                new Wine(3, "0 year wine", "wine from year 0", 0, 85, 20, "pinot noir", "white",
                        new Vineyard("vineyard", "region")));
        someInvalidWines.add(
                new Wine(4, "20 year wine", "wine from year 20", 20, 85, 20, "pinot noir", "white",
                        new Vineyard("vineyard", "region")));
        someInvalidWines.add(
                new Wine(5, "future wine", "wine from year 10000", 10000, 85, 20, "pinot noir",
                        "white", new Vineyard("vineyard", "region")));
        someInvalidWines.add(
                new Wine(6, "negative price wine", "wine with price -10", 2000, 85, -10,
                        "pinot noir", "white", new Vineyard("vineyard", "region")));
        someInvalidWines.add(
                new Wine(7, "negative score wine", "wine with score -1", 2000, -1, 10, "pinot noir",
                        "white", new Vineyard("vineyard", "region")));
        someInvalidWines.add(
                new Wine(8, "101 score wine", "wine with score 101", 2000, 101, 10, "pinot noir",
                        "white", new Vineyard("vineyard", "region")));
        wineDAO.batchAdd(someInvalidWines);
        assertEquals(1, wineDAO.getAll().size());
    }

    /**
     * test adding invalid wines individually using the wineDAO add method
     * should only add the wine if it has valid attributes
     */
    @Test
    public void testAddSingleInvalidWine() {
        Wine emptyWineName =
                new Wine(2, "", "a wine with an empty name", 2014, 85, 20, "pinot noir", "white",
                        new Vineyard("vineyard", "region"));
        wineDAO.add(emptyWineName);
        Wine year0Wine =
                new Wine(3, "0 year wine", "wine from year 0", 0, 85, 20, "pinot noir", "white",
                        new Vineyard("vineyard", "region"));
        wineDAO.add(year0Wine);
        assertEquals(0, wineDAO.getAll().size());
    }

    /**
     * Various related tests for the sql string generated by the queryBuilder method
     */
    @Nested
    class TestQueryBuilder {

        /**
         * Test searching
         */
        @Test
        public void testSearch() {
            String expectedSql = "SELECT DISTINCT wine.id, wine.name, "
                    + "wine.description, wine.year, wine.rating, "
                    + "wine.variety, wine.price, wine.colour, "
                    + "vineyard.name AS vineyardName, vineyard.region "
                    + "FROM WINE, VINEYARD "
                    + "WHERE vineyard.id = wine.vineyard "
                    + "AND (wine.name LIKE ? OR wine.description LIKE ?);";
            assertEquals(expectedSql, wineDAO.queryBuilder("Yummy", "0", "0",
                    "0", "0", 0, 800, -1));
        }

        /**
         * Test filtering by variety
         */
        @Test
        public void testFilterVariety() {
            String expectedSql = "SELECT DISTINCT wine.id, wine.name, "
                    + "wine.description, wine.year, wine.rating, "
                    + "wine.variety, wine.price, wine.colour, "
                    + "vineyard.name AS vineyardName, vineyard.region "
                    + "FROM WINE, VINEYARD "
                    + "WHERE vineyard.id = wine.vineyard "
                    + "AND wine.variety = 'testVariety';";
            assertEquals(expectedSql, wineDAO.queryBuilder(null, "testVariety", "0",
                    "0", "0", 0, 800, -1));
        }

        /**
         * Test filtering by region
         */
        @Test
        public void testFilterRegion() {
            String expectedSql = "SELECT DISTINCT wine.id, wine.name, "
                    + "wine.description, wine.year, wine.rating, "
                    + "wine.variety, wine.price, wine.colour, "
                    + "vineyard.name AS vineyardName, vineyard.region "
                    + "FROM WINE, VINEYARD "
                    + "WHERE vineyard.id = wine.vineyard "
                    + "AND vineyard.region = 'testRegion';";
            assertEquals(expectedSql, wineDAO.queryBuilder(null, "0", "0",
                    "testRegion", "0", 0, 800, -1));
        }

        /**
         * Test filtering by year
         */
        @Test
        public void testFilterYear() {
            String expectedSql = "SELECT DISTINCT wine.id, wine.name, "
                    + "wine.description, wine.year, wine.rating, "
                    + "wine.variety, wine.price, wine.colour, "
                    + "vineyard.name AS vineyardName, vineyard.region "
                    + "FROM WINE, VINEYARD "
                    + "WHERE vineyard.id = wine.vineyard AND wine.year = 1990;";
            assertEquals(expectedSql, wineDAO.queryBuilder(null, "0", "0",
                    "0", "1990", 0, 800, -1));
        }

        /**
         * Test filtering by min price
         */
        @Test
        public void testFilterMinPrice() {
            String expectedSql = "SELECT DISTINCT wine.id, wine.name, wine.description, wine.year, "
                    + "wine.rating, wine.variety, wine.price, "
                    + "wine.colour, vineyard.name AS vineyardName, "
                    + "vineyard.region FROM WINE, VINEYARD "
                    + "WHERE vineyard.id = wine.vineyard "
                    + "AND wine.price >= 5.0;";
            assertEquals(expectedSql, wineDAO.queryBuilder(null, "0", "0",
                    "0", "0", 5, 800, -1));
        }

        /**
         * Test filtering by max price
         */
        @Test
        public void testFilterMaxPrice() {
            String expectedSql = "SELECT DISTINCT wine.id, wine.name, "
                    + "wine.description, wine.year, wine.rating, "
                    + "wine.variety, wine.price, wine.colour, "
                    + "vineyard.name AS vineyardName, vineyard.region "
                    + "FROM WINE, VINEYARD WHERE vineyard.id = wine.vineyard "
                    + "AND wine.price <= 30.0;";
            assertEquals(expectedSql, wineDAO.queryBuilder(null, "0", "0",
                    "0", "0", 0, 30, -1));
        }

        /**
         * Test filtering by a combination of min and max price
         */
        @Test
        public void testFilterMinMaxPrice() {
            String expectedSql = "SELECT DISTINCT wine.id, wine.name, "
                    + "wine.description, wine.year, wine.rating, "
                    + "wine.variety, wine.price, wine.colour, "
                    + "vineyard.name AS vineyardName, vineyard.region "
                    + "FROM WINE, VINEYARD WHERE vineyard.id = wine.vineyard "
                    + "AND wine.price <= 30.0 "
                    + "AND wine.price >= 5.0;";
            assertEquals(expectedSql, wineDAO.queryBuilder(null, "0", "0",
                    "0", "0", 5, 30, -1));
        }

        /**
         * Test filtering by min rating
         */
        @Test
        public void testFilterMinRating() {
            String expectedSql = "SELECT DISTINCT wine.id, wine.name, "
                    + "wine.description, wine.year, wine.rating, "
                    + "wine.variety, wine.price, wine.colour, vineyard.name "
                    + "AS vineyardName, vineyard.region "
                    + "FROM WINE, VINEYARD "
                    + "WHERE vineyard.id = wine.vineyard "
                    + "AND wine.rating >= 80.0;";
            assertEquals(expectedSql, wineDAO.queryBuilder(null, "0", "0",
                    "0", "0", 0, 800, 80));
        }

        /**
         * Test combination of searching and filtering by variety
         */
        @Test
        public void testFilterSearch() {
            String expectedSql = "SELECT DISTINCT wine.id, wine.name, "
                    + "wine.description, wine.year, wine.rating, "
                    + "wine.variety, wine.price, wine.colour, "
                    + "vineyard.name AS vineyardName, vineyard.region "
                    + "FROM WINE, VINEYARD WHERE vineyard.id = wine.vineyard "
                    + "AND (wine.name LIKE ? OR wine.description LIKE ?) "
                    + "AND wine.variety = 'Test Variety';";
            assertEquals(expectedSql, wineDAO.queryBuilder("Yummy", "Test Variety", "0",
                    "0", "0", 0, 800, -1));
        }
    }

    /**
     * Test getting the distinct variety list
     */
    @Test
    public void testGetVariety() {
        Vineyard vineyard = new Vineyard("Vineyard", "Region");
        Wine wine1 = new Wine("Test", "Nice", 1999, 89,
                10, "Variety 1", "Red", vineyard);
        Wine wine2 = new Wine("Test 1", "Nice", 1999, 89, 10,
                "Variety 2", "Red", vineyard);
        Wine wine3 = new Wine("Test 2", "Nice", 1999, 89, 10,
                "Variety 3", "Red", vineyard);
        Wine wine4 = new Wine("Test 3", "Nice", 1999, 89, 10,
                "Variety 1", "Red", vineyard);
        List<Wine> wines = List.of(wine1, wine2, wine3, wine4);
        wineDAO.batchAdd(wines);
        List<String> varieties = wineDAO.getVariety();
        assertEquals(3, varieties.size());
        assertEquals("Variety 1", varieties.getFirst());
    }

    @Test
    public void testGetYear() {
        Vineyard vineyard = new Vineyard("Vineyard", "Region");
        Wine wine1 = new Wine("Test", "Nice", 1999, 89,
                10, "Variety 1", "Red", vineyard);
        Wine wine2 = new Wine("Test 1", "Nice", 2000, 89, 10,
                "Variety 2", "Red", vineyard);
        Wine wine3 = new Wine("Test 2", "Nice", 2001, 89, 10,
                "Variety 3", "Red", vineyard);
        Wine wine4 = new Wine("Test 3", "Nice", 1999, 89, 10,
                "Variety 1", "Red", vineyard);
        List<Wine> wines = List.of(wine1, wine2, wine3, wine4);
        wineDAO.batchAdd(wines);
        List<String> years = wineDAO.getYear();
        assertEquals(3, years.size());
        assertEquals("1999", years.getFirst());
    }

    @Test
    public void testGetMinRating() {
        Vineyard vineyard = new Vineyard("Vineyard", "Region");
        Wine wine1 = new Wine("Test", "Nice", 1999, 80,
                10, "Variety 1", "Red", vineyard);
        Wine wine2 = new Wine("Test 1", "Nice", 2000, 89, 10,
                "Variety 2", "Red", vineyard);
        Wine wine3 = new Wine("Test 2", "Nice", 2001, 100, 10,
                "Variety 3", "Red", vineyard);
        Wine wine4 = new Wine("Test 3", "Nice", 1999, 93, 10,
                "Variety 1", "Red", vineyard);
        List<Wine> wines = List.of(wine1, wine2, wine3, wine4);
        wineDAO.batchAdd(wines);
        assertEquals(80, wineDAO.getMinRating());
    }

    @Test
    public void testGetMaxRating() {
        Vineyard vineyard = new Vineyard("Vineyard", "Region");
        Wine wine1 = new Wine("Test", "Nice", 1999, 80,
                10, "Variety 1", "Red", vineyard);
        Wine wine2 = new Wine("Test 1", "Nice", 2000, 89, 10,
                "Variety 2", "Red", vineyard);
        Wine wine3 = new Wine("Test 2", "Nice", 2001, 100, 10,
                "Variety 3", "Red", vineyard);
        Wine wine4 = new Wine("Test 3", "Nice", 1999, 93, 10,
                "Variety 1", "Red", vineyard);
        List<Wine> wines = List.of(wine1, wine2, wine3, wine4);
        wineDAO.batchAdd(wines);
        assertEquals(100, wineDAO.getMaxRating());
    }

    /**
     * Test max and min price. This is grouped in a nested test because they both
     * have the same setup.
     */
    @Nested
    class TestPrice {
        /**
         * Setup some wines with a range of prices.
         */
        @BeforeEach
        public void init() {
            Vineyard vineyard = new Vineyard("Vineyard", "Region");
            Wine wine1 = new Wine("Test", "Nice", 1999, 89,
                    10, "Variety 1", "Red", vineyard);
            Wine wine2 = new Wine("Test 1", "Nice", 1999, 89, 10,
                    "Variety 2", "Red", vineyard);
            Wine wine3 = new Wine("Test 2", "Nice", 1999, 89, 20,
                    "Variety 3", "Red", vineyard);
            Wine wine4 = new Wine("Test 3", "Nice", 1999, 89, 100,
                    "Variety 1", "Red", vineyard);
            List<Wine> wines = List.of(wine1, wine2, wine3, wine4);
            wineDAO.batchAdd(wines);
        }

        /**
         * Verify the minimum price.
         */
        @Test
        public void testGetMinPrice() {
            assertEquals(10, wineDAO.getMinPrice());
        }

        /**
         * Verify the max price.
         */
        @Test
        public void testGetMaxPrice() {
            assertEquals(100, wineDAO.getMaxPrice());
        }
    }


}
