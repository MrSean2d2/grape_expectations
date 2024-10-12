package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Tag;
import seng202.team5.models.User;
import seng202.team5.repository.TagsDAO;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.TagService;
import seng202.team5.services.UserService;

public class TagServiceTest {
    private static TagService tagService;
    private static DatabaseService databaseService;
    private User user;

    /**
     * Initialise the test database.
     * @throws InstanceAlreadyExistsException if the DataBaseService instance
     *                                        already exists
     */
    @BeforeAll
    public static void setUp() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService
                .initialiseInstanceWithUrl("jdbc:sqlite:./src/test/resources/test.db");
        databaseService.resetDb();
        tagService = TagService.getInstance();
    }

    /**
     * Set up a test user and reset the database.
     */
    @BeforeEach
    public void initUser() {
        databaseService.resetDb();
        user = UserService.getInstance().registerUser("User", "password");
    }

    /**
     * Test non-existent tag.
     */
    @Test
    public void testCheckTagDoesNotExist() {
        assertFalse(tagService.checkTagExists("A non-existent tag", user.getId()));
    }

    @Test
    public void testCheckTagExists() {
        assertTrue(tagService.checkTagExists("Favourite", user.getId()));
    }

    @Test
    public void testCustomTagExists() {
        Tag tag = new Tag(user.getId(), "Custom", 1);
        TagsDAO tagsDAO = new TagsDAO();
        tag.setTagId(tagsDAO.add(tag));
        assertTrue(tagService.checkTagExists("Custom", user.getId()));
    }

    @Test
    public void testCheckEmptyName() {
        assertEquals("Tag name can't be blank!", tagService.checkName(""));
    }

    @Test
    public void testCheckBlankName() {
        assertEquals("Tag name can't be blank!", tagService.checkName("     "));
    }

    @Test
    public void testCheckLongName() {
        assertEquals("Tag name is too long! Must be no more than 20 characters.",
                tagService.checkName("alskf;sladfj;aslkjfd;aslfjda;sljdfdlskfjdslkfjdslkfjsdljf"));
    }

    @Test
    public void testCreateTag() {
        Tag tag = tagService.createTag(user.getId(), "A cool tag", 0);
        assertNotNull(tag);
        assertEquals(tagService.getCreatedTag(), tag);
    }

    @Test
    public void testCreateTagDuplicate() {
        tagService.createTag(user.getId(), "A tag", 0);
        assertNull(tagService.createTag(user.getId(), "A tag", 0));
    }
}
