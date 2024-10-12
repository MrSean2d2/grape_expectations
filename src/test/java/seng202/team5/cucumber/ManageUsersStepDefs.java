package seng202.team5.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.exceptions.PasswordIncorrectException;
import seng202.team5.models.Role;
import seng202.team5.models.User;
import seng202.team5.repository.UserDAO;
import seng202.team5.services.UserService;

public class ManageUsersStepDefs {
    private UserService userService;
    private User currentUser;
    private UserDAO userDAO;
    private String errorMessage;

    @Given("a user {string} with password {string} has registered")
    public void userRegistered(String username, String password) throws NotFoundException {
        userDAO = new UserDAO();
        userService = UserService.getInstance();
        currentUser = userService.registerUser(username, password);
        if (currentUser == null) {
            currentUser = userDAO.getFromUserName(username);
        }
    }

    @When("the admin grants user admin rights")
    public void grantAdminRights() {
        currentUser.setRole(Role.ADMIN);
        userDAO.update(currentUser);
    }

    @Then("user now has admin role")
    public void userAdminRole() throws NotFoundException {
        Assertions.assertTrue(userDAO.getFromUserName("tester").getIsAdmin());
    }

    @When("the admin deletes {string} user")
    public void deleteUser(String username) throws NotFoundException {
        userDAO = new UserDAO();
        currentUser = userDAO.getFromUserName(username);
        userService.deleteUser(currentUser);
    }

    @Then("user {string} with password {string} can no longer sign in")
    public void noLongerSignIn(String username, String password) throws PasswordIncorrectException {
        try {
            userService.signinUser(username, password);
        } catch (NotFoundException e) {
            errorMessage = e.getMessage();
        }
        Assertions.assertEquals("No user with " + username + " found", errorMessage);
    }
}
