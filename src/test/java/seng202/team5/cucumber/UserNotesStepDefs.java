package seng202.team5.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team5.models.Review;

public class UserNotesStepDefs {
    private Review review;
    private String actualNotes;

    @Given("a user is reviewing a wine with id {int}")
    public void userIsReviewingaWineWithId(int wineID) {
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
