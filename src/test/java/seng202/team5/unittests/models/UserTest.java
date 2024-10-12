package seng202.team5.unittests.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import seng202.team5.models.Role;
import seng202.team5.models.User;

/**
 * Tests for the user object.
 */
public class UserTest {
    /**
     * Sanity test for User equality.
     */
    @Test
    public void testEquality() {
        User user1 = new User(0, "Test User", "password", Role.USER, 0);
        User user2 = new User(0, "Test User", "password", Role.USER, 0);
        User user3 = new User(1, "Test User 2", "password", Role.USER, 0);

        // Test equality
        assertEquals(user1, user1);
        assertEquals(user1, user2);

        // Test inequality
        assertNotEquals(user1, user3);
        assertNotEquals(user1, null);
    }
}
