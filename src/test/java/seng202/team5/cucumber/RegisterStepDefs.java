package seng202.team5.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.models.User;
import seng202.team5.repository.UserDAO;
import seng202.team5.services.UserService;

public class RegisterStepDefs {
    private UserDAO userDAO;
    private UserService userService;
    private User registeredUser;
    private User sameUsernameUser;
    private String usernameCheck;
    private String passwordCheck;
    @Given("the user {string} does not already have an account")
    public void userNotExist(String username) {
        userDAO = new UserDAO();
        try {
            int userId = userDAO.getFromUserName(username).getId();
            userDAO.delete(userId);
        } catch (NotFoundException ignored) {

        }
    }
    @Given("is not logged in on the account page")
    public void userSignedOut() {
        userService = UserService.getInstance();
        userService.signOut();
    }
    @When("username {string} with password {string} is registered")
    public void registerUser(String username, String password) {
        usernameCheck = userService.checkName(username);
        passwordCheck = userService.checkPassword(password);
        registeredUser = userService.registerUser(username, password);
    }
    @Then("the user is taken to account page")
    public void userIsRegistered() {
        Assertions.assertEquals("tester", registeredUser.getUsername());
    }
    @Given("user {string} already exists")
    public void userExists(String username) {
        userService = UserService.getInstance();
        userService.registerUser(username, "passtest1!");
    }
    @When("another user attempts to register using same username {string}")
    public void sameUsernameRegister(String username) {
        sameUsernameUser = userService.registerUser(username, "passtest1!");
    }
    @Then("error message tells the user account with that username already exists")
    public void usernameAlreadyExists() {
        Assertions.assertNull(sameUsernameUser);
    }
    @Then("error message tells the user username must be between four and twenty characters")
    public void usernameBetweenFourAndTwenty() {
        Assertions.assertEquals("Username must be between 4 and 20 characters!", usernameCheck);
    }
    @Then("error message tells the user password must contain a special character")
    public void passwordContainSpecialCharacter() {
        Assertions.assertEquals("Password must contain a special character!", passwordCheck);
    }
    @Then("error message tells the user username cannot be empty")
    public void usernameEmpty() {
        Assertions.assertEquals("Username cannot be empty!", usernameCheck);
    }
    @Then("error message tells the user password cannot be empty")
    public void passwordEmpty() {
        Assertions.assertEquals("Password cannot be empty!", passwordCheck);
    }
}
