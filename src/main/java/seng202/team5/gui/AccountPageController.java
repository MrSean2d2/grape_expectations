package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import seng202.team5.models.User;
import seng202.team5.services.UserService;

/**
 * Controller for the account page.
 *
 * @author Martyn Gascoigne
 */
public class AccountPageController extends PageController {

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
        registerButton.setTooltip(new Tooltip("Register a new account"));
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
        User user = userManager.signinUser(username, password);

        if (user != null) {
            userManager.setCurrentUser(user);
            usernameField.setText("");
            passwordField.setText("");

            // Go to the homepage
            swapPage("/fxml/newHomePage.fxml");
        } else {
            errorLabel.setText("Username or password is incorrect!");
            usernameField.getStyleClass().add("field_error");
            passwordField.getStyleClass().add("field_error");
        }
    }

    /**
     * Attempt to register with the provided details
     * if the register is successful,
     * the app will go to the home page
     * otherwise (i.e. username isn't unique),
     * the app will remain on the login page.
     */
    @FXML
    public void attemptRegister() {
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
        User user = userManager.registerUser(username, password);


        if (user != null) {
            userManager.setCurrentUser(user);
            usernameField.setText("");
            passwordField.setText("");

            // Go to the homepage
            swapPage("/fxml/newHomePage.fxml");
        } else {
            // Show error
            errorLabel.setText("Account with that username already exists!");
            usernameField.getStyleClass().add("field_error");
        }
    }
}
