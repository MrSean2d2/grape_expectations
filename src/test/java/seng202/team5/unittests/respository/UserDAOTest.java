package seng202.team5.unittests.respository;

import static org.junit.jupiter.api.Assertions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.User;
import seng202.team5.repository.DatabaseService;
import seng202.team5.repository.UserDAO;


/**
 * Unit tests for the UserDAO.
 *
 * @author Martyn Gascoigne
 */
public class UserDAOTest {
    private static final Logger log = LogManager.getLogger(UserDAOTest.class);
    private static UserDAO userDAO;
    private static DatabaseService databaseService;


    /**
     * Set up the testing scenario.
     */
    @BeforeAll
    public static void setUp() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test.db");
        userDAO = new UserDAO();
    }


    /**
     * Reset the database before each test
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
        assertEquals(0, userDAO.getAll().size());
    }


    /**
     * Test the DAO getting one instance from the user table.
     */
    @Test
    public void testGetOne() throws DuplicateEntryException {
        userDAO.add(new User("test", "password", "user", 0));

        User user = userDAO.getOne(1);
        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("user", user.getRole());
        assertEquals(0, user.getIconNumber());
    }


    /**
     * Test the DAO getting all instances from the user table.
     */
    @Test
    public void testGetAll() throws DuplicateEntryException {
        userDAO.add(new User("user1", "password", "user", 0));
        userDAO.add(new User("user2", "password", "user", 0));

        assertEquals(2, userDAO.getAll().size());

        userDAO.add(new User("user3", "password", "user", 0));

        assertEquals(3, userDAO.getAll().size());
    }


    /**
     * Test the DAO adding one instance to the user table.
     */
    @Test
    public void testAdd() throws DuplicateEntryException {
        assertEquals(0, userDAO.getAll().size());

        userDAO.add(new User("user1", "password", "user", 0));

        assertEquals(1, userDAO.getAll().size());

        userDAO.add(new User("user2", "password", "user", 0));

        assertEquals(2, userDAO.getAll().size());
    }


    /**
     * Test the DAO deleting one instance from the user table.
     */
    @Test
    public void testDelete() throws DuplicateEntryException {
        assertEquals(0, userDAO.getAll().size());

        userDAO.add(new User("user1", "password", "user", 0));

        assertEquals(1, userDAO.getAll().size());

        userDAO.delete(1);

        assertEquals(0, userDAO.getAll().size());
    }


    /**
     * Test the DAO updating a user.
     */
    @Test
    public void testUpdate() throws DuplicateEntryException {
        User user = new User("user1", "password", "user", 0);
        int userId = userDAO.add(user);
        user.setId(userId);

        assertEquals("user1", userDAO.getOne(1).getUsername());
        assertEquals("user", userDAO.getOne(1).getRole());

        user.setUsername("user2");
        user.setRole("admin");

        userDAO.update(user);

        assertEquals("user2", userDAO.getOne(1).getUsername());
        assertEquals("admin", userDAO.getOne(1).getRole());
    }

}
