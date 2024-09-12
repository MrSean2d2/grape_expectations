package seng202.team5.unittests.respository;

import kotlin.NotImplementedError;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Vineyard;
import seng202.team5.repository.DatabaseService;
import seng202.team5.repository.VineyardDAO;

public class VineyardDAOTest {
    static VineyardDAO vineyardDAO;
    static DatabaseService databaseService;

    @BeforeAll
    static void setup() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl("jdbc:sqlite:./src/test/resources/test_database.db");
        vineyardDAO = new VineyardDAO();
    }

    @BeforeEach
    void resetDB() {
        databaseService.resetDb();
    }

    @Test
    public void testVineyardsEmptyOnCreation() {
        Assertions.assertEquals(0, vineyardDAO.getAll().size());
    }

    @Test
    public void testAddVineyardOK() {
        Vineyard toAdd = new Vineyard("test1", "region1");
        vineyardDAO.add(toAdd);
        Assertions.assertEquals(1, vineyardDAO.getAll().size());
        Vineyard vineyard = vineyardDAO.getAll().get(0);
        Assertions.assertEquals(toAdd.getName(), vineyard.getName());
    }
    @Test
    public void testDeleteVineyard() {
        int insertId = vineyardDAO.add(new Vineyard("test", "region1"));
        int totalVineyardsBefore = vineyardDAO.getAll().size();
        vineyardDAO.delete(insertId);
        Assertions.assertEquals(totalVineyardsBefore - 1, vineyardDAO.getAll().size());
    }

    @Test
    public void testGetVineyardByID() {
        Vineyard toAdd = new Vineyard("test", "region1");
        int id = vineyardDAO.add(toAdd);
        Vineyard vineyard = vineyardDAO.getOne(id);
        Assertions.assertEquals(toAdd.getName(), vineyard.getName());
    }

    @Test
    public void testUpdateVineyard() {
        Vineyard vineyard = new Vineyard("test", "region1");
        vineyardDAO.add(vineyard);
        Assertions.assertThrows(NotImplementedException.class, () -> vineyardDAO.update(vineyard));
    }
}
