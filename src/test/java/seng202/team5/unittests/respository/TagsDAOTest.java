package seng202.team5.unittests.respository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Tag;
import seng202.team5.models.User;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.UserDAO;
import seng202.team5.services.DatabaseService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the TagsDAO.
 *
 * @author Martyn Gascoigne
 */
public class TagsDAOTest {
    private static TagsDAO tagsDAO;
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

        tagsDAO = new TagsDAO();
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
        assertEquals(0, tagsDAO.getAll().size());
    }


    /**
     * Test getting all tags.
     */
    @Test
    public void testGetAll() throws DuplicateEntryException {
        User testUser = new User("test", "pass", "user", 0);
        testUser.setId(userDAO.add(testUser));
        User testUser2 = new User("test2", "pass", "user", 0);
        testUser2.setId(userDAO.add(testUser2));

        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        Tag testTag2 = new Tag(testUser2.getId(), "Tag 2", 0);
        testTag2.setTagId(tagsDAO.add(testTag2));

        Tag testTag3 = new Tag(testUser2.getId(), "Tag 3", 0);
        testTag3.setTagId(tagsDAO.add(testTag3));

        List<Tag> tags = tagsDAO.getAll();
        assertEquals(3, tags.size());
    }


    /**
     * Test getting a single tag.
     */
    @Test
    public void testGetOne() throws DuplicateEntryException {
        User testUser = new User("test", "pass", "user", 0);
        testUser.setId(userDAO.add(testUser));
        User testUser2 = new User("test2", "pass", "user", 0);
        testUser2.setId(userDAO.add(testUser2));

        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        Tag testTag2 = new Tag(testUser2.getId(), "Tag 2", 0);
        testTag2.setTagId(tagsDAO.add(testTag2));

        Tag testTag3 = new Tag(testUser2.getId(), "Tag 3", 0);
        testTag3.setTagId(tagsDAO.add(testTag3));

        Tag tag = tagsDAO.getOne(1);
        assertEquals("Tag 1", tag.getName());
    }


    /**
     * Test getting from user.
     */
    @Test
    public void testGetFromUser() throws DuplicateEntryException {
        User testUser = new User("test", "pass", "user", 0);
        testUser.setId(userDAO.add(testUser));
        User testUser2 = new User("test2", "pass", "user", 0);
        testUser2.setId(userDAO.add(testUser2));

        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        Tag testTag2 = new Tag(testUser2.getId(), "Tag 2", 0);
        testTag2.setTagId(tagsDAO.add(testTag2));

        Tag testTag3 = new Tag(testUser2.getId(), "Tag 3", 0);
        testTag3.setTagId(tagsDAO.add(testTag3));

        List<Tag> userTags = tagsDAO.getFromUser(testUser2.getId());
        assertEquals(2, userTags.size());
    }


    /**
     * Test deleting a tag.
     */
    @Test
    public void testDeleteTag() throws DuplicateEntryException {
        User testUser = new User("test", "pass", "user", 0);
        testUser.setId(userDAO.add(testUser));

        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        List<Tag> tags = tagsDAO.getAll();
        assertEquals(1, tags.size());

        tagsDAO.delete(1);

        tags = tagsDAO.getAll();
        assertEquals(0, tags.size());
    }


    /**
     * Test deleting all of a user's tags.
     */
    @Test
    public void testDeleteTagFromUser() throws DuplicateEntryException {
        User testUser = new User("test", "pass", "user", 0);
        testUser.setId(userDAO.add(testUser));

        User testUser2 = new User("test2", "pass", "user", 0);
        testUser2.setId(userDAO.add(testUser2));

        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));

        Tag testTag2 = new Tag(testUser.getId(), "Tag 2", 0);
        testTag2.setTagId(tagsDAO.add(testTag2));

        Tag testTag3 = new Tag(testUser.getId(), "Tag 3", 0);
        testTag3.setTagId(tagsDAO.add(testTag3));

        Tag testTag4 = new Tag(testUser2.getId(), "Tag 4", 0);
        testTag4.setTagId(tagsDAO.add(testTag4));

        List<Tag> tags = tagsDAO.getAll();
        assertEquals(4, tags.size());

        tagsDAO.deleteFromUser(1);

        tags = tagsDAO.getAll();
        assertEquals(1, tags.size());
    }

    /**
     * Test updating a tag.
     */
    @Test
    public void testUpdateTag() throws DuplicateEntryException {
        User testUser = new User("test", "pass", "user", 0);
        testUser.setId(userDAO.add(testUser));

        Tag testTag = new Tag(testUser.getId(), "Tag 1", 0);
        testTag.setTagId(tagsDAO.add(testTag));


        assertEquals("Tag 1", testTag.getName());
        testTag.setName("Tag 10");
        tagsDAO.update(testTag);
        assertEquals("Tag 10", testTag.getName());
    }
}
