package seng202.team5.unittests.respository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Role;
import seng202.team5.models.Tag;
import seng202.team5.models.User;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.UserDAO;
import seng202.team5.services.DatabaseService;


/**
 * Unit tests for the TagsDAO.
 *
 * @author Martyn Gascoigne
 */
public class TagsDAOTest {
    private static TagsDAO tagsDAO;
    private static UserDAO userDAO;
    private static DatabaseService databaseService;

    public static int defaultTagCount;

    private static User testUser;
    private static User testUser2;


    /**
     * Set up the testing scenario.
     */
    @BeforeAll
    public static void setUp() throws InstanceAlreadyExistsException, DuplicateEntryException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test.db");

        databaseService.resetDb();

        tagsDAO = new TagsDAO();
        userDAO = new UserDAO();

        testUser = new User("test", "pass", Role.USER, 0);
        testUser.setId(userDAO.add(testUser));
        testUser2 = new User("test2", "pass", Role.USER, 0);
        testUser2.setId(userDAO.add(testUser2));

    }


    /**
     * Reset the database before each test
     */
    @BeforeEach
    public void resetDb() {
        databaseService.resetDb();
        defaultTagCount = tagsDAO.getAll().size();
    }


    /**
     * Test that the database is empty on creation.
     * Because of the default tags that are added in the SQLite definition file,
     * the actual "empty" state of the database contains {defaultTagCount} default tags.
     */
    @Test
    public void testEmptyOnCreation() {
        assertEquals(defaultTagCount, tagsDAO.getAll().size());
    }


    /**
     * Test getting all tags.
     */
    @Test
    public void testGetAll() {
        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        Tag testTag2 = new Tag(testUser2.getId(), "Tag 2", 0);
        testTag2.setTagId(tagsDAO.add(testTag2));

        Tag testTag3 = new Tag(testUser2.getId(), "Tag 3", 0);
        testTag3.setTagId(tagsDAO.add(testTag3));

        List<Tag> tags = tagsDAO.getAll();
        assertEquals(3 + defaultTagCount, tags.size());
    }


    /**
     * Test getting a single tag.
     */
    @Test
    public void testGetOne() {
        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        Tag testTag2 = new Tag(testUser2.getId(), "Tag 2", 0);
        testTag2.setTagId(tagsDAO.add(testTag2));

        Tag testTag3 = new Tag(testUser2.getId(), "Tag 3", 0);
        testTag3.setTagId(tagsDAO.add(testTag3));

        Tag tag = tagsDAO.getOne(testTag.getTagId());
        assertEquals("Tag 1", tag.getName());

        Tag tag2 = tagsDAO.getOne(testTag2.getTagId());
        assertEquals("Tag 2", tag2.getName());

        Tag tag3 = tagsDAO.getOne(testTag3.getTagId());
        assertEquals("Tag 3", tag3.getName());
    }


    /**
     * Test getting from user.
     */
    @Test
    public void testGetFromUser() {
        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        Tag testTag2 = new Tag(testUser2.getId(), "Tag 2", 0);
        testTag2.setTagId(tagsDAO.add(testTag2));

        Tag testTag3 = new Tag(testUser2.getId(), "Tag 3", 0);
        testTag3.setTagId(tagsDAO.add(testTag3));

        List<Tag> userTags = tagsDAO.getFromUser(testUser2.getId());
        assertEquals(2 + defaultTagCount, userTags.size());
    }


    /**
     * Test getting from user.
     */
    @Test
    public void testGetFromNameAndUserId() {
        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        Tag foundTag = tagsDAO.getFromNameAndUserId("Tag 1", testUser.getId());
        assertEquals(testTag, foundTag);
    }


    /**
     * Test deleting a tag.
     */
    @Test
    public void testDeleteTag() {
        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        List<Tag> tags = tagsDAO.getAll();
        assertEquals(1 + defaultTagCount, tags.size());

        tagsDAO.delete(1);

        tags = tagsDAO.getAll();
        assertEquals(defaultTagCount, tags.size());
    }


    /**
     * Test deleting all of a user's tags.
     */
    @Test
    public void testDeleteTagFromUser() {
        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        Tag testTag2 = new Tag(testUser.getId(), "Tag 2", 0);
        testTag2.setTagId(tagsDAO.add(testTag2));

        Tag testTag3 = new Tag(testUser.getId(), "Tag 3", 0);
        testTag3.setTagId(tagsDAO.add(testTag3));

        Tag testTag4 = new Tag(testUser2.getId(), "Tag 4", 0);
        testTag4.setTagId(tagsDAO.add(testTag4));

        List<Tag> tags = tagsDAO.getAll();

        assertEquals(4 + defaultTagCount, tags.size());

        tagsDAO.deleteFromUser(1);

        tags = tagsDAO.getAll();
        assertEquals(1 + defaultTagCount, tags.size());
    }

    /**
     * Test updating a tag.
     */
    @Test
    public void testUpdateTag() {
        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        assertEquals("Tag 1", testTag.getName());
        testTag.setName("Tag 10");
        tagsDAO.update(testTag);
        assertEquals("Tag 10", testTag.getName());
    }
}
