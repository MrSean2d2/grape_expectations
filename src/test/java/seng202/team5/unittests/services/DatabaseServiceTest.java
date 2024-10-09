package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import seng202.team5.services.DatabaseService;

/**
 * Tests for DatabaseService.
 *
 * @author Sean Reitsma
 */
public class DatabaseServiceTest {

    /**
     * Test that the DatabaseService instance can be retrieved and is always the
     * same instance.
     */
    @Test
    public void testDatabaseServiceInstance() {
        DatabaseService databaseService = DatabaseService.getInstance();
        assertNotNull(databaseService);
        assertEquals(databaseService, DatabaseService.getInstance());
    }

    /**
     * Test for successful database connection.
     *
     * @throws SQLException if there is an SQL error
     */
    @Test
    void testDatabaseConnection() throws SQLException {
        DatabaseService databaseService = DatabaseService.getInstance();
        Connection connection = databaseService.connect();
        assertNotNull(connection);
        assertNotNull(connection.getMetaData());
        connection.close();
    }
}
