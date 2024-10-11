package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.exceptions.PasswordIncorrectException;
import seng202.team5.models.Role;
import seng202.team5.models.User;
import seng202.team5.repository.UserDAO;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.UserService;

public class UserServiceTest {
    private UserService userService;
    private static DatabaseService databaseService;

    /**
     * set up the background info for each test
     *
     * @throws InstanceAlreadyExistsException if the instance already exists
     */
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
        UserService.removeInstance();
        userService = UserService.getInstance();
        userService.signOut();
    }

    /**
     * Test sign out of account.
     */
    @Test
    public void signOutTest() {
        User user = new User(1, "user", "password", Role.USER, 0);
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
        User user = new User(1, "user", "password", Role.USER, 0);

        // Set the current user and check if not null and the username == "user"
        userService.setCurrentUser(user);
        assertNotNull(userService.getCurrentUser());
        assertEquals("user", userService.getCurrentUser().getUsername());
    }

    /**
     * Hash password test.
     */
    @Test
    public void hashPasswordTest() throws NoSuchAlgorithmException,
            InvalidKeySpecException, PasswordIncorrectException {
        byte[] salt = UserService.generateSalt();
        String pass1String = "password";
        String pass2String = "password";
        String pass3String = "notPassword";
        String hashedPassword = UserService.hashPassword(pass1String, salt);

        // Check if password 1 and 2 are equal, and that 1 and 3 are not
        assertTrue(UserService.verifyPassword(pass2String, hashedPassword));

        assertThrows(PasswordIncorrectException.class, () ->
                UserService.verifyPassword(pass3String, hashedPassword));
    }

    /**
     * Test successful register of user.
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

    @Test
    public void registerBlankNameTest() {
        User user = userService.registerUser("", "password1!");
        assertNull(user);
    }

    @Test
    public void registerBlankPasswordTest() {
        User user = userService.registerUser("user", "");
        assertNull(user);
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
        assertNotEquals(testUser1.getId(), testUser2.getId());
    }

    /**
     * Test user sign in with correct credentials.
     */
    @Test
    public void correctSignInTest() throws NotFoundException, PasswordIncorrectException {
        userService.registerUser("testUser1", "pass");

        User signedInUser = userService.signinUser("testUser1", "pass");

        assertEquals(signedInUser.getUsername(), "testUser1");
    }

    /**
     * Test user sign in with incorrect credentials.
     */
    @Test
    public void incorrectSignInTest() {
        userService.registerUser("testUser", "pass");

        assertThrows(PasswordIncorrectException.class,
                () -> userService.signinUser("testUser", "wrongPass"));
    }

    /**
     * Test updating the user's password.
     *
     * @throws NoSuchAlgorithmException if there is an error registering the user
     * @throws InvalidKeySpecException if there is an error registering the user
     */
    @Test
    public void updatePasswordTest() throws NoSuchAlgorithmException,
            InvalidKeySpecException, PasswordIncorrectException {
        User user = userService.registerUser("test", "oldPassword");
        String newPassword = "newPassword";
        userService.updateUserPassword(user, newPassword);
        assertTrue(UserService.verifyPassword(newPassword, user.getPassword()));
    }

    /**
     * Test that an admin account is automatically created.
     */
    @Test
    public void createAdminOnStartTest() throws NotFoundException, PasswordIncorrectException {
        User admin = userService.signinUser("admin", "admin");
        assertNotNull(admin);
        assertEquals(Role.ADMIN, admin.getRole());
    }

    /**
     * Test checkName with a username which is too short.
     */
    @Test
    public void testShortUsername() {
        assertEquals("Username must be between 4 and 20 characters!", userService.checkName("a"));
    }

    /**
     * Test checkName with a username which is too long.
     */
    @Test
    public void testLongUsername() {
        assertEquals("Username must be between 4 and 20 characters!",
                userService.checkName("averylongusernamewithmorethantwentycharacters"));
    }

    /**
     * Test a correct username which is exactly 20 characters long.
     */
    @Test
    public void testCorrectUsernameUpperBound() {
        assertNull(userService.checkName("aaaaaaaaaaaaaaaaaaaa"));
    }

    /**
     * Test a correct username which is exactly 4 characters long.
     */
    @Test
    public void testCorrectUsernameLowerBound() {
        assertNull(userService.checkName("aaaa"));
    }

    /**
     * Test checkName with an empty username.
     */
    @Test
    public void testEmptyUsername() {
        assertEquals("Username cannot be empty!", userService.checkName(""));
    }

    /**
     * Test checkPassword with an empty password.
     */
    @Test
    public void testEmptyPassword() {
        assertEquals("Password cannot be empty!", userService.checkPassword(""));
    }

    /**
     * Test checkPassword with a password of less than 8 characters.
     */
    @Test
    public void testShortPassword() {
        assertEquals("Password must contain at least 8 characters!",
                userService.checkPassword("passwor"));
    }

    /**
     * Test checkPassword with a password containing no letters.
     */
    @Test
    public void testNoAlphaPassword() {
        assertEquals("Password must contain alphanumeric characters!",
                userService.checkPassword("12345678!@"));
    }

    /**
     * Test checkPassword with a password containing no numbers.
     */
    @Test
    public void testNoNumberPassword() {
        assertEquals("Password must contain a numeric character!",
                userService.checkPassword("password!@"));
    }

    /**
     * Test checkPassword with a password containing no special characters.
     */
    @Test
    public void testNoSpecialPassword() {
        assertEquals("Password must contain a special character!",
                userService.checkPassword("password12"));
    }

    /**
     * Test checkPassword with a password which is valid.
     */
    @Test
    public void testValidPassword() {
        assertNull(userService.checkPassword("password!1"));
    }

    /**
     * Verify that updateUserPassword correctly keeps the old password
     * if an unexpected {@link NoSuchAlgorithmException} is thrown by hashPassword.
     */
    @Test
    public void testUpdatePasswordNoSuchAlgorithm() {
        User user = userService.registerUser("testUser", "password");
        String oldPassword = user.getPassword();
        try (MockedStatic<UserService> userServiceMockedStatic = mockStatic(UserService.class)) {
            userServiceMockedStatic.when(() -> UserService.hashPassword(eq("newPassword"), any()))
                    .thenThrow(NoSuchAlgorithmException.class);
            userService.updateUserPassword(user, "newPassword");
            assertEquals(oldPassword, user.getPassword());
        }
    }

    /**
     * Verify that updateUserPassword correctly keeps the old password if an unexpected
     * {@link InvalidKeySpecException} is thrown by hashPassword.
     */
    @Test
    public void testUpdatePasswordInvalidKeySpec() {
        User user = userService.registerUser("testUser", "password");
        String oldPassword = user.getPassword();
        try (MockedStatic<UserService> userServiceMockedStatic = mockStatic(UserService.class)) {
            userServiceMockedStatic.when(() -> UserService.hashPassword(eq("newPassword"), any()))
                    .thenThrow(InvalidKeySpecException.class);
            userService.updateUserPassword(user, "newPassword");
            assertEquals(oldPassword, user.getPassword());
        }
    }

    /**
     * Test deleting a user.
     */
    @Test
    public void testDeleteUser() {
        User user = userService.registerUser("testUser", "password");
        userService.deleteUser(user);
        UserDAO userDAO = new UserDAO();
        assertNull(userDAO.getOne(user.getId()));
    }

    /**
     * Verify that the only admin user cannot be deleted.
     *
     * @throws NotFoundException if the default admin user doesn't exist
     * @throws PasswordIncorrectException if the default admin password is incorrect
     */
    @Test
    public void testDeleteDefaultAdmin() throws NotFoundException, PasswordIncorrectException {
        User user = userService.signinUser("admin", "admin");
        userService.deleteUser(user);
        UserDAO userDAO = new UserDAO();
        assertEquals(user, userDAO.getOne(user.getId()));
    }

    /**
     * Test deleting a second admin.
     */
    @Test
    public void testDeleteExtraAdmin() {
        User user = userService.registerUser("testUser", "password");
        user.setRole(Role.ADMIN);
        UserDAO userDAO = new UserDAO();
        userDAO.update(user);
        userService.deleteUser(user);
        assertNull(userDAO.getOne(user.getId()));
    }


}