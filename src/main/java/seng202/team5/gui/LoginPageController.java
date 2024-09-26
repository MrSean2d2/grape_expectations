package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.exceptions.PasswordIncorrectException;
import seng202.team5.models.User;
import seng202.team5.services.UserService;

/**
 * Controller for the login page.
 *
 * @author Martyn Gascoigne
 */
public class LoginPageController extends PageController {

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameField;

    @FXML
    private Label errorLabel;

    @FXML
    private PasswordField passwordField;

    /**
     * Initialize the user page.
     */
    @FXML
    private void initialize() {
        errorLabel.setText(""); //Blank error message

        loginButton.setTooltip(new Tooltip("Log in to account"));
        registerButton.setTooltip(new Tooltip("Go to register page"));
    }

    /**
     * Attempt to log in with the provided details.
     */
    @FXML
    public void attemptLogin() {
        usernameField.getStyleClass().remove("field_error");
        passwordField.getStyleClass().remove("field_error");

        String username = usernameField.getText();

        if (username.isEmpty()) {
            // Show error
            errorLabel.setText("Username cannot be empty!");
            usernameField.getStyleClass().add("field_error");
            return;
        }

        String password = passwordField.getText();

        if (password.isEmpty()) {
            // Show error
            errorLabel.setText("Password cannot be empty!");
            passwordField.getStyleClass().add("field_error");
            return;
        }

        UserService userManager = UserService.getInstance();
        User user;
        try {
            user = userManager.signinUser(username, password);

            if (user != null) {
                userManager.setCurrentUser(user);
                usernameField.setText("");
                passwordField.setText("");

                // Go to the homepage
                swapPage("/fxml/AccountManagePage.fxml");
            }
        } catch (NotFoundException e) {
            errorLabel.setText("User doesn't exist!");
            usernameField.getStyleClass().add("field_error");
        } catch (PasswordIncorrectException e) {
            errorLabel.setText("Password is incorrect!");
            passwordField.getStyleClass().add("field_error");
        }
    }

    /**
     * Visit the register page.
     * Transition to the register page
     */
    @FXML
    public void goToRegister() {
        // Transition
        swapPage("/fxml/RegisterPage.fxml");
    }
}
