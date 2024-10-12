package seng202.team5.unittests.respository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Role;
import seng202.team5.models.User;
import seng202.team5.repository.UserDAO;
import seng202.team5.services.DatabaseService;


/**
 * Unit tests for the UserDAO.
 *
 * @author Martyn Gascoigne
 */
public class UserDAOTest {
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
        userDAO.add(new User("test", "password", Role.USER, 0));

        User user = userDAO.getOne(1);
        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(Role.USER, user.getRole());
        assertEquals(0, user.getIconNumber());
    }


    /**
     * Test the DAO getting all instances from the user table.
     */
    @Test
    public void testGetAll() throws DuplicateEntryException {
        userDAO.add(new User("user1", "password", Role.USER, 0));
        userDAO.add(new User("user2", "password", Role.USER, 0));

        assertEquals(2, userDAO.getAll().size());

        userDAO.add(new User("user3", "password", Role.USER, 0));

        assertEquals(3, userDAO.getAll().size());
    }


    /**
     * Test the DAO adding one instance to the user table.
     */
    @Test
    public void testAdd() throws DuplicateEntryException {
        assertEquals(0, userDAO.getAll().size());

        userDAO.add(new User("user1", "password", Role.USER, 0));

        assertEquals(1, userDAO.getAll().size());

        userDAO.add(new User("user2", "password", Role.USER, 0));

        assertEquals(2, userDAO.getAll().size());
    }


    /**
     * Test the DAO deleting one instance from the user table.
     */
    @Test
    public void testDelete() throws DuplicateEntryException {
        assertEquals(0, userDAO.getAll().size());

        userDAO.add(new User("user1", "password", Role.USER, 0));

        assertEquals(1, userDAO.getAll().size());

        userDAO.delete(1);

        assertEquals(0, userDAO.getAll().size());
    }


    /**
     * Test the DAO updating a user.
     */
    @Test
    public void testUpdate() throws DuplicateEntryException {
        User user = new User("user1", "password", Role.USER, 0);
        int userId = userDAO.add(user);
        user.setId(userId);

        assertEquals("user1", userDAO.getOne(1).getUsername());
        assertEquals("user", userDAO.getOne(1).getRole().getRoleName());

        user.setUsername("user2");
        user.setRole(Role.ADMIN);

        userDAO.update(user);

        assertEquals("user2", userDAO.getOne(1).getUsername());
        assertEquals("admin", userDAO.getOne(1).getRole().getRoleName());
    }


    /**
     * Test that an empty database contains no admin users.
     *
     */
    @Test
    public void testNoAdmin() {
        assertEquals(0, userDAO.getAdminCount());
    }

    /**
     * Test that a database containing a regular user contains no admin users.
     *
     * @throws DuplicateEntryException if the user to be added already exists
     */
    @Test
    public void testNoAdminWithRegularUser() throws DuplicateEntryException {
        User user = new User("user1", "password", Role.USER, 0);
        int userId = userDAO.add(user);
        user.setId(userId);

        assertEquals(0, userDAO.getAdminCount());
    }

    /**
     * Test that a database containing one admin contains one admin.
     *
     * @throws DuplicateEntryException if the user to be added already exists
     */
    @Test
    public void testOneAdmin() throws DuplicateEntryException {
        User admin = new User("admin", "password", Role.ADMIN, 0);
        int adminId = userDAO.add(admin);
        admin.setId(adminId);
        assertEquals(1, userDAO.getAdminCount());
    }

    /**
     * Test searching for a username.
     *
     * @throws DuplicateEntryException if the admin user already exists
     */
    @Test
    public void testGetMatchingUserName() throws DuplicateEntryException {
        User admin = new User("admin", "admin", Role.ADMIN, 0);
        admin.setId(userDAO.add(admin));
        List<User> results = userDAO.getMatchingUserName("adm");
        assertEquals(1, results.size());
        assertEquals("admin", results.getFirst().getUsername());
    }

    /**
     * Test adding a user with a duplicate username.
     *
     * @throws DuplicateEntryException if the original user to be added is itself
     *                                 a duplicate
     */
    @Test
    public void testDuplicateUser() throws DuplicateEntryException {
        User admin = new User("admin", "admin", Role.ADMIN, 0);
        userDAO.add(admin);
        User same = new User("admin", "pasword", Role.ADMIN, 2);
        assertThrows(DuplicateEntryException.class, () -> userDAO.add(same));
    }

}
