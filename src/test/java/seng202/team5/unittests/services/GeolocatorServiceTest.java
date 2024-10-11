package seng202.team5.unittests.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Vineyard;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.GeolocatorService;
import seng202.team5.services.UserService;

public class GeolocatorServiceTest {
    private static DatabaseService databaseService;
    private GeolocatorService geolocatorService;
    
    @BeforeAll
    public static void setUp() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test_database.db");
    }

    /**
     * Reset the database before each test.
     */
    @BeforeEach
    void resetDb() {
        databaseService.resetDb();
        geolocatorService = GeolocatorService.getInstance();
    }

    @Test
    public void testQueryAddress() {
        Vineyard vineyard = new Vineyard("Stoneleigh", "Marlborough");
        geolocatorService.queryAddress(vineyard);
        Assertions.assertEquals(-41.47447475, vineyard.getLat());
    }
}
