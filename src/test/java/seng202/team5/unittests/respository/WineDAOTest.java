package seng202.team5.unittests.respository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Wine;
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
        String createRegion = "INSERT INTO VINEYARD(name, region) VALUES ('Test Vineyard', 'Test Region')";
        String sql = "INSERT INTO WINE(id, name, description, year, rating, price, vineyard, variety) VALUES (1,'Test Wine', 'Test Wine is a nice wine', 2024, 99, 7.99, 'Test Vineyard', 'Pinot Noir')";
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
}
