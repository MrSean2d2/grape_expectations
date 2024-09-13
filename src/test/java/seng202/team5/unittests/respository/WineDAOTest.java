package seng202.team5.unittests.respository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        testWine.setId(wineDAO.add(testWine));
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
        List<Wine> wines = wineDAO.getAll();
        assertEquals(1, wines.size());
        Wine retrievedWine = wines.getFirst();

        assertNotNull(retrievedWine);
        assertEquals(1, retrievedWine.getId());
        assertEquals(testWine, retrievedWine);
    }

    @Test
    public void testBatchAdd() {
        Vineyard testVineyard1 = new Vineyard("tv1","testreg1");
        Vineyard testVineyard2 = new Vineyard("tv2","testreg2");
        Vineyard testVineyard3 = new Vineyard("tv3","testreg3");

        Wine testWine1 = new Wine("testWine", "tasty", 2023, 85, 15.99, "testVariety","Red", testVineyard1);
        Wine testWine2 = new Wine("testWine2", "tasty", 2023, 85, 15.99, "testVariety","Red", testVineyard1);
        Wine testWine3 = new Wine("testWine3", "tasty", 2023, 85, 15.99, "testVariety","Red", testVineyard1);

        List<Wine> testWineList = new ArrayList<Wine>(){{add(testWine1); add(testWine2); add(testWine3);}};

        wineDAO.batchAdd(testWineList);
        assertEquals(3,wineDAO.getAll().size());
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
