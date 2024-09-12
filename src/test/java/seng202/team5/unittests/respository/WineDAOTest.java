package seng202.team5.unittests.respository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Region;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.models.WineType;
import seng202.team5.models.WineVariety;
import seng202.team5.repository.DatabaseService;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.RegionService;
import seng202.team5.services.WineVarietyService;

public class WineDAOTest {
    private static final Logger log = LogManager.getLogger(WineDAOTest.class);
    private static WineDAO wineDAO;
    private static DatabaseService databaseService;

    @BeforeAll
    public static void setUp() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test.db");
        WineVarietyService wineVarietyService = new WineVarietyService();
        RegionService regionService = new RegionService();
        wineDAO = new WineDAO(wineVarietyService, regionService);
    }

    @BeforeEach
    public void resetDb() {
        databaseService.resetDb();
    }

    @Test
    public void testEmptyOnCreation() {
        assertEquals(0, wineDAO.getAll().size());
    }


    @Test
    public void testGetOne() {
        String createRegion = "INSERT INTO VINEYARD(name, region) "
                + "VALUES ('Test Vineyard', 'Test Region')";
        String sql = "INSERT INTO WINE(id, name, description, year, rating, "
                + "price, vineyard, variety) VALUES (1,'Test Wine', "
                + "'Test Wine is a nice wine', 2024, 99, 7.99, 'Test Vineyard', 'Pinot Noir')";
        try (Connection conn = databaseService.connect();
                Statement statement = conn.createStatement();
                Statement statement2 = conn.createStatement()) {
            statement2.execute(createRegion);
            statement.execute(sql);
        } catch (SQLException e) {
            log.error(e);
        }
        Wine wine = wineDAO.getOne(1);
        assertNotNull(wine);
        assertEquals("Test Wine", wine.getName());
        assertEquals("Test Wine is a nice wine", wine.getDescription());
        assertEquals(2024, wine.getYear());
        assertEquals(99, wine.getRating());
        assertEquals("Test Vineyard", wine.getVineyard().getName());
        assertEquals("Pinot Noir", wine.getWineVariety().getName());
    }

    @Test
    public void testAdd() {
        Region testRegion = new Region("testRegion");
        Vineyard testVineyard = new Vineyard("testVineyard");
        WineVariety testWineVariety = new WineVariety("testVariety", WineType.RED);
        testRegion.addVineyard(testVineyard);

        Wine testWine = new Wine(1, "testWine", "tasty",
                    2023, 85, 15.99, testWineVariety, testRegion, testVineyard);

        wineDAO.add(testWine);
        Wine retrievedWine = wineDAO.getOne(1);

        assertNotNull(retrievedWine);
        assertEquals("testWine", retrievedWine.getName());
        assertEquals("tasty", retrievedWine.getDescription());
        assertEquals(2023, retrievedWine.getYear());
        assertEquals(85, retrievedWine.getRating());
        assertEquals("testVineyard", retrievedWine.getVineyard().getName());
        assertEquals("testVariety", retrievedWine.getWineVariety().getName());
    }

    @Test
    public void testAddDefaultId() {
        Region testRegion = new Region("Test Region");
        Vineyard testVineyard = new Vineyard("Test Vineyard");
        WineVariety testVariety = new WineVariety("Test Variety", WineType.WHITE);
        testRegion.addVineyard(testVineyard);

        Wine testWine = new Wine("Test Wine", "A very nice wine", 2024,
                87, 200, testVariety, testRegion, testVineyard);
        int addedId = wineDAO.add(testWine);
        // Should be only 1 item in the database so the id should be 1
        assertEquals(1, addedId);
        Wine retrievedWine = wineDAO.getOne(addedId);
        assertNotNull(retrievedWine);
        assertEquals(1, retrievedWine.getId());
        /* Group all assertions checking that the retrieved wine is the same as
        the one we added.
         */
        assertAll("Wine Equality",
                () -> assertEquals("Test Wine", retrievedWine.getName()),
                () -> assertEquals("A very nice wine", retrievedWine.getDescription()),
                () -> assertEquals(2024, retrievedWine.getYear()),
                () -> assertEquals(87, retrievedWine.getRating()),
                () -> assertEquals(200, retrievedWine.getPrice()),
                () -> assertEquals(testVariety.getName(), retrievedWine.getWineVariety().getName()),
                () -> assertEquals(testRegion.getName(), retrievedWine.getRegion().getName()),
                () -> assertEquals(testVineyard.getName(), retrievedWine.getVineyard().getName())
        );
    }
}
