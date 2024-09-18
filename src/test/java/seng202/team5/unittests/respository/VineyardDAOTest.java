package seng202.team5.unittests.respository;

import java.util.List;
import kotlin.NotImplementedError;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.DatabaseService;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

/**
 * Unit tests for the VineyardDAO
 */
public class VineyardDAOTest {
    static VineyardDAO vineyardDAO;
    static DatabaseService databaseService;
    private static WineDAO wineDAO;


    /**
     * Set up the testing scenario
     */
    @BeforeAll
    static void setup() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl("jdbc:sqlite:./src/test/resources/test_database.db");
        vineyardDAO = new VineyardDAO();
    }


    /**
     * Reset the database before each test.
     */
    @BeforeEach
    void resetDB() {
        databaseService.resetDb();
    }


    /**
     * Test that the database is empty on creation.
     */
    @Test
    public void testEmptyOnCreation() {
        Assertions.assertEquals(0, vineyardDAO.getAll().size());
    }


    /**
     * Test adding a Vineyard.
     */
    @Test
    public void testAddVineyardOK() {
        Vineyard toAdd = new Vineyard("test1", "region1");
        vineyardDAO.add(toAdd);
        Assertions.assertEquals(1, vineyardDAO.getAll().size());
        Vineyard vineyard = vineyardDAO.getAll().getFirst();
        Assertions.assertEquals(toAdd.getName(), vineyard.getName());
    }


    /**
     * Test deleting a vineyard.
     */
    @Test
    public void testDeleteVineyard() {
        int insertId = vineyardDAO.add(new Vineyard("test", "region1"));
        int totalVineyardsBefore = vineyardDAO.getAll().size();
        vineyardDAO.delete(insertId);
        Assertions.assertEquals(totalVineyardsBefore - 1, vineyardDAO.getAll().size());
    }


    /**
     * Test getting a vineyard from its id.
     */
    @Test
    public void testGetVineyardByID() {
        Vineyard toAdd = new Vineyard("test", "region1");
        int id = vineyardDAO.add(toAdd);
        Vineyard vineyard = vineyardDAO.getOne(id);
        Assertions.assertEquals(toAdd.getName(), vineyard.getName());
    }


    /**
     * Test updating a vineyard.
     */
    @Test
    public void testUpdateVineyard() {
        Vineyard vineyard = new Vineyard("test", "region1");
        vineyardDAO.add(vineyard);
        Assertions.assertThrows(NotImplementedException.class, () -> vineyardDAO.update(vineyard));
    }

    /**
     * test getting a region of a vineyard
     */
    @Test
    public void testGetRegions() {
        Vineyard vineyard = new Vineyard("Test", "A region");
        vineyardDAO.add(vineyard);
        List<String> regions = vineyardDAO.getRegions();
        Assertions.assertEquals(1, regions.size());
        Assertions.assertEquals("A region", regions.getFirst());
    }

    /**
     * test that regions with same name are distinct
     */
    @Test
    public void testGetRegionsDistinct() {
        Vineyard vineyard1 = new Vineyard("Vineyard 1", "A region");
        Vineyard vineyard2 = new Vineyard("Vineyard 2", "A region");
        vineyardDAO.add(vineyard1);
        vineyardDAO.add(vineyard2);
        List<String> regions = vineyardDAO.getRegions();
        Assertions.assertEquals(1, regions.size());
    }

    /**
     * test that vineyards are not added from an invalid wine with a valid vineyard
     */
    @Test
    public void testVineyardAddedFromInvalidWine() {
        Vineyard vineyard1 = new Vineyard("Vineyard 1", "A region");
        Wine wine = new Wine("", "invalid wine with valid vineyard, shouldn't be added", 0, -1, -1, "",
                "white",  vineyard1);
        wineDAO = new WineDAO(vineyardDAO);
        wineDAO.add(wine);
        Assertions.assertEquals(0, wineDAO.getAll().size());
        Assertions.assertEquals(0, vineyardDAO.getAll().size());
    }

    /**
     * test that a vineyard is added to the database from a valid wine
     */
    @Test
    public void testVineyardAddedFromWine() {
        Vineyard vineyard1 = new Vineyard("Vineyard 1", "A region");
        Wine wine = new Wine("wine", "valid wine with valid vineyard, shouldn't be added", 2000, 86, 20, "variety",
                "white",  vineyard1);
        wineDAO = new WineDAO(vineyardDAO);
        wineDAO.add(wine);
        Assertions.assertEquals(1, wineDAO.getAll().size());
        Assertions.assertEquals(1, vineyardDAO.getAll().size());
    }
}
