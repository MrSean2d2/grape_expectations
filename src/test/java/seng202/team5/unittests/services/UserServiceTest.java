package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.models.User;
import seng202.team5.services.UserService;

public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = UserService.getInstance();
        userService.signOut();
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
}