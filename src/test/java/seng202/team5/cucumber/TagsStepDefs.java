package seng202.team5.cucumber;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.exceptions.PasswordIncorrectException;
import seng202.team5.models.AssignedTag;
import seng202.team5.models.Tag;
import seng202.team5.models.User;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.AssignedTagsDAO;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.UserDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.ColourLookupService;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.TagService;
import seng202.team5.services.UserService;

public class TagsStepDefs {
    private static TagsDAO tagsDAO;
    private static UserDAO userDAO;
    private static UserService userService;
    private static WineDAO wineDAO;
    private static AssignedTagsDAO assignedTagsDAO;

    private static Wine wine;
    private static Tag tag;
    private static User testUser;
    private static Vineyard vineyard;

    @BeforeAll
    public static void setUp()
            throws InstanceAlreadyExistsException, DuplicateEntryException, NotFoundException,
            PasswordIncorrectException {
        DatabaseService databaseService;
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test.db");
        databaseService.resetDb();

        // Setup DAOs
        userDAO = new UserDAO();
        wineDAO = new WineDAO(new VineyardDAO());
        assignedTagsDAO = new AssignedTagsDAO();
        tagsDAO = new TagsDAO();

        // Test user
        userDAO = new UserDAO();
        userService = UserService.getInstance();
        testUser = userService.registerUser("tester", "passtest1!");
        if (testUser == null) {
            testUser = userDAO.getFromUserName("tester");
        }

        UserService.getInstance().signinUser("tester", "passtest1!");
        vineyard = new Vineyard("Vineyard", "Region");
    }

    @Given("the user is logged in")
    public void theUserIsLoggedIn() {
        Assertions.assertNotNull(UserService.getInstance().getCurrentUser());
    }

    @And("is viewing detailed information about {string}")
    public void isViewingDetailedInformationAbout(String arg0) {
        wine = new Wine(arg0, "description", 2000, 100,
        10, "TestVariety", "Red", vineyard);
        wine.setId(wineDAO.add(wine));
    }

    @When("the user creates new tag {string}, chooses colour {string} and submits")
    public void theUserClicksCreateNewTag(String arg0, String arg1) throws DuplicateEntryException {
        int col = ColourLookupService.getColourName(arg1.toLowerCase());
        tag = TagService.getInstance().createTag(testUser.getId(), arg0, col);

        AssignedTag assignedTag = new AssignedTag(tag.getTagId(), testUser.getId(), wine.getId());

        assignedTagsDAO.add(assignedTag);
    }

    @Then("the new {string} {string} tag is added to the list of "
            + "tags and the tag is added to the selected wine")
    public void theNewTagIsAddedToTheListOfTagsAndTheTagIsAddedToTheSelectedWine(String arg0,
                                                                                 String arg1) {
        AssignedTag firstAssigned = assignedTagsDAO.getAllAssigned(wine.getId(),
                testUser.getId()).getFirst();

        Tag foundTag = tagsDAO.getOne(firstAssigned.getTagId());
        Assertions.assertEquals(arg0.toLowerCase(),
                ColourLookupService.getTagLabelColour(foundTag.getColour()).toLowerCase());

        Assertions.assertEquals(arg1, foundTag.getName());
    }

    @And("is on the dashboard page")
    public void isOnTheDashboardPage() {
    }

    @And("has created a new tag {string}")
    public void hasCreatedNewTag(String arg0) throws DuplicateEntryException {
        if (tagsDAO.getIdFromName(arg0, testUser.getId()) == 0) {
            TagService.getInstance().createTag(testUser.getId(), arg0, 0);
        }

        int tagId = tagsDAO.getIdFromName(arg0, testUser.getId());
        tag = tagsDAO.getOne(tagId);

        AssignedTag assignedTag = new AssignedTag(tag.getTagId(), testUser.getId(), wine.getId());

        assignedTagsDAO.add(assignedTag);
    }

    @When("the user edits the tag, changes the name to {string}, colour {string} and submits")
    public void theUserChangesTheTagNameToColourAndTheUserSubmits(String arg0, String arg1) {
        tag.setName(arg0);
        int col = ColourLookupService.getColourName(arg1.toLowerCase());
        tag.setColour(col);

        tagsDAO.update(tag);
    }

    @Then("the tag is updated and is called {string} with colour {string}")
    public void theTagIsUpdatedAndIsCalledWithColour(String arg0, String arg1) {
        Assertions.assertEquals(arg0, tag.getName());
        Assertions.assertEquals(arg1.toLowerCase(),
                ColourLookupService.getTagLabelColour(tag.getColour()).toLowerCase());
    }
}
