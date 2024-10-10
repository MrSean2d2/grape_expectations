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
    private User loggedInUser;
    @Given("the user {string} with password {string} already has an account")
    public void userPasswordAccount(String username, String password) {
        userService = UserService.getInstance();
        userService.registerUser(username, password);
    }
    @Given("is not yet logged in")
    public void userLoggedOut() {
        userService.signOut();
    }
    @When("username {string} with incorrect password {string} is used to log in")
    public void correctUsernameIncorrectPassword(String username, String password) throws NotFoundException, PasswordIncorrectException {
        try {
            userService.signinUser(username, password);
        } catch (PasswordIncorrectException e) {
            errorMessage = e.getMessage();
        }
    }
    @Then("error message tells the user password is correct")
    public void passwordIncorrect() {
        Assertions.assertEquals("Password incorrect", errorMessage);

    }
    @When("username {string} with password {string} is used to log in")
    public void usernamePasswordLogIn(String username, String password) throws NotFoundException, PasswordIncorrectException {
        loggedInUser = userService.signinUser(username, password);
    }
    @Then("the user is logged in and taken to account page")
    public void LoggedIn() {
        Assertions.assertEquals("tester", loggedInUser.getUsername());
    }
    @When("incorrect username {string} with password {string} is used to log in")
    public void incorrectUsernameCorrectPassword(String username, String password) throws NotFoundException, PasswordIncorrectException {
        try {
            userService.signinUser(username, password);
        } catch (NotFoundException e) {
            errorMessage = e.getMessage();
        }
    }
    @Then("error message tells the user username is wrong")
    public void usernameIncorrect() {
        Assertions.assertEquals("No user with test found", errorMessage);
    }
}
