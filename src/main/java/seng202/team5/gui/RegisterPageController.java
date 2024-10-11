package seng202.team5.gui;

import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import seng202.team5.models.User;
import seng202.team5.services.UserService;

/**
 * Controller for the login page.
 *
 * @author Martyn Gascoigne
 */
public class RegisterPageController extends FormErrorController {

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
    @FXML
    private TextField passwordVisibleField;

    @FXML
    private ImageView toggleVisibility;
    private boolean passwordVisible = false;

    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private TextField repeatPasswordVisibleField;

    @FXML
    private ImageView toggleRepeatVisibility;
    private boolean repeatPasswordVisible = false;

    private Image shownIcon;
    private Image hiddenIcon;

    /**
     * Initialize the user page.
     */
    @FXML
    private void initialize() {
        errorLabel.setText(""); //Blank error message

        loginButton.setTooltip(new Tooltip("Return to login page"));
        registerButton.setTooltip(new Tooltip("Register a new account"));

        passwordVisibleField.textProperty().bindBidirectional(
                passwordField.textProperty());
        repeatPasswordVisibleField.textProperty().bindBidirectional(
                repeatPasswordField.textProperty());

        // Create icons
        shownIcon = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/images/OpenEye.png")));
        hiddenIcon = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/images/ClosedEye.png")));
    }

    /**
     * Toggle the visibility of the first password box.
     */
    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        // If password visible, set visibility
        if (passwordVisible) {
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            registerButton.requestFocus(); // make the register button focused
            toggleVisibility.setImage(shownIcon);
        } else {
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            registerButton.requestFocus(); // make the register button focused
            toggleVisibility.setImage(hiddenIcon);
        }
    }

    /**
     * Toggle the visibility of the confirm password box.
     */
    @FXML
    private void toggleRepeatPasswordVisibility() {
        repeatPasswordVisible = !repeatPasswordVisible;

        // If password visible, set visibility
        if (repeatPasswordVisible) {
            repeatPasswordVisibleField.setVisible(true);
            repeatPasswordVisibleField.setManaged(true);
            repeatPasswordField.setVisible(false);
            repeatPasswordField.setManaged(false);
            registerButton.requestFocus(); // make the register button focused
            toggleRepeatVisibility.setImage(shownIcon);
        } else {
            repeatPasswordField.setVisible(true);
            repeatPasswordField.setManaged(true);
            repeatPasswordVisibleField.setVisible(false);
            repeatPasswordVisibleField.setManaged(false);
            registerButton.requestFocus(); // make the register button focused
            toggleRepeatVisibility.setImage(hiddenIcon);
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
    private void attemptRegister() {
        resetFieldError(usernameField);
        resetFieldError(passwordField);
        resetFieldError(repeatPasswordField);
        resetFieldError(passwordVisibleField);
        resetFieldError(repeatPasswordVisibleField);
        TextField currentPassField = passwordVisible ? passwordVisibleField
                : passwordField;
        TextField currentRepeatField = repeatPasswordVisible ? repeatPasswordVisibleField
                : repeatPasswordField;

        String username = usernameField.getText();

        UserService userService = UserService.getInstance();
        String message = userService.checkName(username);
        if (message != null) {
            fieldError(usernameField, errorLabel, message);
            return;
        }

        String password = passwordField.getText();
        // Password validation
        String passMessage = userService.checkPassword(password);
        if (passMessage != null) {
            fieldError(currentPassField, errorLabel, passMessage);
            return;
        }

        String repeatedPassword = repeatPasswordField.getText();

        if (!password.equals(repeatedPassword)) {
            fieldError(currentPassField, errorLabel, "Passwords don't match!");
            fieldError(currentRepeatField);
            return;
        }

        User user = userService.registerUser(username, password);

        if (user != null) {
            userService.setCurrentUser(user);
            usernameField.setText("");
            passwordField.setText("");

            // Go to the homepage
            swapPage("/fxml/AccountManagePage.fxml");
        } else {
            // Show error
            fieldError(usernameField, errorLabel, "Account with that username already exists!");
        }
    }

    /**
     * Attempt to register account if the user presses enter.
     *
     * @param event KeyEvent that triggered the method, pressing of a key
     */
    @FXML
    private void enterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            attemptRegister();
        }
    }

    /**
     * Go back to login page.
     */
    @FXML
    private void goToLogin() {
        swapPage("/fxml/LoginPage.fxml");
    }
}
