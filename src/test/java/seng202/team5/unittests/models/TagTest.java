package seng202.team5.unittests.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import seng202.team5.models.AssignedTag;
import seng202.team5.models.Role;
import seng202.team5.models.Tag;
import seng202.team5.models.User;

/**
 * Tests for the tag object.
 */
public class TagTest {
    /**
     * Testing tag equals for coverage :).
     */
    @Test
    public void testEquality() {
        Tag tag1 = new Tag(1, 0, "Cool Tag", 0);
        Tag tag2 = new Tag(1, 0, "Cool Tag", 0);
        Tag tag3 = new Tag(2, 0, "Cool Tag... Not!", 0);

        // Test equality
        assertEquals(tag1, tag1);
        assertEquals(tag1, tag2);

        // Test inequality
        assertNotEquals(tag1, tag3);
        assertNotEquals(tag1, null);
    }

    /**
     * Testing assigned tag equals for coverage :).
     */
    @Test
    public void testAssignedEquality() {
        AssignedTag tag1 = new AssignedTag(1, 0, 0);
        AssignedTag tag2 = new AssignedTag(1, 0, 0);
        AssignedTag tag3 = new AssignedTag(2, 0, 0);
        User notTag = new User(0, "Test User", "password", Role.USER, 0);

        // Test equality
        assertEquals(tag1, tag1);
        assertEquals(tag1, tag2);
        assertEquals(tag1, tag2);

        // Test inequality
        assertNotEquals(tag1, tag3);
        assertNotEquals(tag1, null);
        assertNotEquals(tag1, notTag);
    }

    /**
     * Testing assigned tag equals for coverage :).
     */
    @Test
    public void testAssignedConstructor() {
        AssignedTag tag1 = new AssignedTag(1, 0, 0);

        // Test equality
        assertEquals(tag1.getTagId(), 1);
        assertEquals(tag1.getUserId(), 0);
        assertEquals(tag1.getWineId(), 0);
    }

    /**
     * Testing tag toString for coverage :).
     */
    @Test
    public void testToString() {
        Tag tag1 = new Tag(1, 0, "Cool Tag", 0);
        assertEquals(tag1.toString(), "Cool Tag");
    }
}
