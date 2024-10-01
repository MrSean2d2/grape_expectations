package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Vineyard;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.VineyardService;

/**
 * Tests for VineyardService.
 *
 * @author Sean Reitsma
 */
public class VineyardServiceTest {
    private static VineyardService vineyardService;
    private static DatabaseService databaseService;

    /**
     * Initial setup.
     */
    @BeforeAll
    public static void setUp() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test_database.db");
        vineyardService = VineyardService.getInstance();
    }

    /**
     * Reset database for each test.
     */
    @BeforeEach
    public void resetDb() {
        databaseService.resetDb();
    }

    /**
     * Tests that only one instance of VineyardService is created.
     */
    @Test
    public void testSingletonInstance() {
        VineyardService vineyardService1 = VineyardService.getInstance();
        VineyardService vineyardService2 = VineyardService.getInstance();
        assertSame(vineyardService1, vineyardService2);
    }

    /**
     * Tests that passing a blank vineyard name throws an exception.
     */
    @Test
    public void testRetrieveVineyardBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> vineyardService.retreiveVineyard("", ""));
    }

    /**
     * Tests retrieving a vineyard which does not yet exist.
     */
    @Test
    public void testRetrieveVineyardNotExists() {
        Vineyard vineyard = vineyardService.retreiveVineyard("Test Vineyard",
                "Test Region");
        assertNotNull(vineyard);
    }

    /**
     * Tests that a vineyard which is previously not in the database gets added
     * and that the id of the returned vineyard is correct.
     */
    @Test
    public void testRetrieveVineyardAddsToDb() {
        Vineyard vineyard = vineyardService.retreiveVineyard("Test Vineyard",
                "Test Region");
        VineyardDAO vineyardDAO = new VineyardDAO();
        assertEquals(vineyard, vineyardDAO.getOne(vineyard.getId()));
    }

    /**
     * Tests retrieving a vineyard which is already in the database.
     */
    @Test
    public void testRetrieveExistingVineyard() {
        Vineyard vineyard = new Vineyard("Test Vineyard", "Test Region");
        VineyardDAO vineyardDAO = new VineyardDAO();
        vineyard.setId(vineyardDAO.add(vineyard));
        Vineyard retrieved = vineyardService.retreiveVineyard("Test Vineyard",
                "Test Region");
        assertEquals(vineyard, retrieved);
    }
}
