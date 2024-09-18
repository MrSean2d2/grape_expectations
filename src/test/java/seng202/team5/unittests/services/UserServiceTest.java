package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.User;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.UserService;

public class UserServiceTest {
    private UserService userService;
    private static DatabaseService databaseService;

    /**
     * set up the background info for each test
     * @throws InstanceAlreadyExistsException if the instance already exists
     */
    @BeforeEach
    public void setUp() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test_database.db");
        userService = UserService.getInstance();
        userService.signOut();


    }

    /**
     * Reset the database before each test.
     */
    @BeforeEach
    void resetDB() {
        databaseService.resetDb();
    }

    /**
     * Test sign out of account.
     */
    @Test
    public void signOutTest() {
        User user = new User(1, "user", "password", "user", 0);
        userService.setCurrentUser(user);
        assertNotNull(userService.getCurrentUser());
        userService.signOut();
        assertNull(userService.getCurrentUser());
    }

    /**
     * Test setting the current user to the passed in user.
     */
    @Test
    public void setCurrentUserTest() {
        User user = new User(1, "user", "password", "user", 0);

        // Set the current user and check if not null and the username == "user"
        userService.setCurrentUser(user);
        assertNotNull(userService.getCurrentUser());
        assertEquals("user", userService.getCurrentUser().getUsername());
    }

    /**
     * Hash password test.
     */
    @Test
    public void hashPasswordTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = userService.generateSalt();
        String pass1String = "password";
        String pass2String = "password";
        String pass3String = "notPassword";
        String hashedPassword = userService.hashPassword(pass1String, salt);

        // Check if password 1 and 2 are equal, and that 1 and 3 are not
        assertTrue(userService.verifyPassword(pass2String, hashedPassword));
        assertFalse(userService.verifyPassword(pass3String, hashedPassword));
    }

    /**
     * Test sucessful register of user.
     */
    @Test
    public void registerUserTest() {
        User testUser = userService.registerUser("testUser3", "password");
        assertEquals("testUser3", testUser.getUsername());
    }


    /**
     * Test username and password is empty.
     */
    @Test
    public void registerNullUserTest() {
        User testUser = userService.registerUser("", "");
        assertNull(testUser);
    }

    /**
     * Register non-unique username test.
     */
    @Test
    public void registerNotUniqueUserTest() {
        userService.registerUser("testUser", "pass");
        User testUser1 = userService.registerUser("testUser", "pass2");
        assertNull(testUser1);
    }

    /**
     * Test register user has unique id.
     */
    @Test
    public void registerDifferentUsersTest() {
        User testUser1 = userService.registerUser("testUser1", "pass1");
        User testUser2 = userService.registerUser("testUser2", "pass2");
        assertFalse(testUser1.getId() == testUser2.getId());
    }

    /**
     * Test user sign in with correct credentials.
     */
    @Test
    public void correctSignInTest() {
        userService.registerUser("testUser1","pass");

        User signedInUser = userService.signinUser("testUser1", "pass");

        assertEquals(signedInUser.getUsername(), "testUser1");
    }

    /**
     * Test user sign in with incorrect credentials.
     */
    @Test
    public void incorrectSignInTest() {
        userService.registerUser("testUser","pass");

        User signedInUser = userService.signinUser("testUser", "wrongPass");

        assertNull(signedInUser);
    }
}