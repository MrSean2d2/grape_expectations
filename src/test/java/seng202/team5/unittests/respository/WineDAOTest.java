package seng202.team5.unittests.respository;

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
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.RegionService;
import seng202.team5.services.WineVarietyService;

import static org.junit.jupiter.api.Assertions.*;

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
        VineyardDAO vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);
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
        Vineyard testVineyard = new Vineyard("Test vineyard", "Test region");
        Wine testWine = new Wine("Test Wine", "Test Wine is a nice wine", 2024, 99, 7.99, "Pinot Noir", "Red", testVineyard);
        wineDAO.add(testWine);
        Wine wine = wineDAO.getOne(1);
        assertNotNull(wine);
        assertEquals(testWine, wine);
    }

    @Test
    public void testAdd() {
        Vineyard testVineyard = new Vineyard("testVineyard", "testRegion");

        Wine testWine = new Wine(1, "testWine", "tasty",
                    2023, 85, 15.99, "testVariety", "Red", testVineyard);

        wineDAO.add(testWine);
        assertEquals(1, wineDAO.getAll().size());
        Wine retrievedWine = wineDAO.getOne(1);

        assertNotNull(retrievedWine);
        assertEquals(1, retrievedWine.getId());
        assertEquals(testWine, retrievedWine);
    }

    @Test
    public void testAddDefaultId() {
        Vineyard testVineyard = new Vineyard("Test Vineyard", "Test Region");

        Wine testWine = new Wine("Test Wine", "A very nice wine", 2024,
                87, 200, "testVariety", "Red", testVineyard);
        int addedId = wineDAO.add(testWine);
        // Should be only 1 item in the database so the id should be 1
        assertEquals(1, addedId);
        Wine retrievedWine = wineDAO.getOne(addedId);
        assertNotNull(retrievedWine);
        assertEquals(1, retrievedWine.getId());
        assertEquals(testWine, retrievedWine);
    }
    @Test
    public void testDelete() {
        Vineyard testVineyard = new Vineyard("Test Vineyard", "Test Region");
        Wine testWine = new Wine("Test Wine", "A very nice wine", 2024,
                87, 200, "testVariety", "Red", testVineyard);
        int toDeleteID = wineDAO.add(testWine);

        wineDAO.delete(toDeleteID);

        assertEquals(0, wineDAO.getAll().size());

    }
}
