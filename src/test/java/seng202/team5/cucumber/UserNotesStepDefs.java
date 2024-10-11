package seng202.team5.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team5.models.Review;
import seng202.team5.models.User;
import seng202.team5.services.UserService;
import seng202.team5.services.WineService;

import java.awt.*;

public class UserNotesStepDefs {
    private UserService userService = UserService.getInstance();
    private WineService wineService = WineService.getInstance();
    private Review review;
    private User user;
    private String actualNotes;
    @Given("the user is logged in")
    public void theUserIsLoggedIn() {
        user = userService.registerUser("testUser","password8!");
        userService.setCurrentUser(user);

        //int wineID = wineService.get
    }

    @When("the user enters {string} into the notes section")
    public void theUserEntersIntoTheNotesSection(String arg0) {
        
    }

    @And("the user is on the detailed view of {string} wine")
    public void theUserIsOnTheDetailedViewOfWine(String arg0) {
        
    }

    @Then("the note {string} is saved to the user review of the {string} entry")
    public void theNoteIsSavedToTheUserReviewOfTheEntry(String arg0, String arg1) {
    }

    @Given("a user is reviewing a wine with id {int}")
    public void aUserIsReviewingAWineWithId(int wineID) {
        int userID = 1;
        review = new Review(wineID, userID);
    }

    @When("the user adds notes {string} to the review")
    public void theUserAddsNotesToTheReview(String notes) {
        review.setNotes(notes);
        actualNotes = review.getNotes();
    }
    @Then("the review should successfully save the notes")
    public void theReviewShouldSuccessfullySaveTheNotes() {
        Assertions.assertNotNull(actualNotes);
    }
    @And("the notes in the review should be {string}")
    public void theNotesInTheReviewShouldBe(String expectedNotes) {
        Assertions.assertEquals(expectedNotes, actualNotes);
    }


}
