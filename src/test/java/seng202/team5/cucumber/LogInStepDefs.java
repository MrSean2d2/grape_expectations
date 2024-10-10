package seng202.team5.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.exceptions.PasswordIncorrectException;
import seng202.team5.models.User;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.UserService;

public class LogInStepDefs {
    private UserService userService;
    private String errorMessage;
    @Given("the user {string} with password {string} already has an account")
    public void userPasswordAccount(String username, String password) {
        // Write code here that turns the phrase above into concrete actions
        userService = UserService.getInstance();
        userService.registerUser(username, password);
    }
    @Given("is not yet logged in")
    public void userLoggedOut() {
        // Write code here that turns the phrase above into concrete actions
        userService.signOut();
    }
    @When("the user enters correct username {string},  incorrect password {string}")
    public void correctUsernameIncorrectPassword(String username, String password) throws NotFoundException, PasswordIncorrectException {
        // Write code here that turns the phrase above into concrete actions
        try {
            userService.signinUser(username, password);
        } catch (PasswordIncorrectException e) {
            errorMessage = e.getMessage();
        } catch (NotFoundException e) {
            errorMessage = e.getMessage();
        }
    }
    @Then("error message tells the user password is correct")
    public void errorMessage() {
        Assertions.assertEquals("Password incorrect", errorMessage);

    }
}
