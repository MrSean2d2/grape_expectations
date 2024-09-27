package seng202.team5.gui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import seng202.team5.models.User;
import seng202.team5.services.UserService;

/**
 * Controller for the login page.
 *
 * @author Martyn Gascoigne
 */
public class RegisterPageController extends PageController {

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

        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        repeatPasswordVisibleField.textProperty()
                .bindBidirectional(repeatPasswordField.textProperty());

        // Create icons
        shownIcon = new Image(getClass().getResourceAsStream("/images/OpenEye.png"));
        hiddenIcon = new Image(getClass().getResourceAsStream("/images/ClosedEye.png"));
    }

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
        usernameField.getStyleClass().remove("field_error");
        passwordField.getStyleClass().remove("field_error");
        repeatPasswordField.getStyleClass().remove("field_error");

        String username = usernameField.getText();

        if (username.isEmpty()) {
            errorLabel.setText("Username cannot be empty!");
            usernameField.getStyleClass().add("field_error");
            return;
        }

        if (username.length() < 4 || username.length() > 20) {
            errorLabel.setText("Username must be between 4 and 20 characters!");
            usernameField.getStyleClass().add("field_error");
            return;
        }

        String password = passwordField.getText();

        if (password.isEmpty()) {
            errorLabel.setText("Password cannot be empty!");
            passwordField.getStyleClass().add("field_error");
            return;
        }

        // Password validation

        Pattern letter = Pattern.compile("[a-zA-z]");
        Pattern digit = Pattern.compile("[0-9]");

        Matcher hasLetter = letter.matcher(password);
        Matcher hasDigit = digit.matcher(password);

        if (password.length() < 8) {
            errorLabel.setText("Password must contain at least 8 characters!");
            passwordField.getStyleClass().add("field_error");
            return;
        }

        if (!hasLetter.find()) {
            errorLabel.setText("Password must contain alphanumeric characters!");
            passwordField.getStyleClass().add("field_error");
            return;
        }

        if (!hasDigit.find()) {
            errorLabel.setText("Password must contain a numeric character!");
            passwordField.getStyleClass().add("field_error");
            return;
        }

        Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
        Matcher hasSpecial = special.matcher(password);

        if (!hasSpecial.find()) {
            errorLabel.setText("Password must contain a special character!");
            passwordField.getStyleClass().add("field_error");
            return;
        }

        String repeatedPassword = repeatPasswordField.getText();

        if (!password.equals(repeatedPassword)) {
            errorLabel.setText("Passwords don't match!");
            passwordField.getStyleClass().add("field_error");
            repeatPasswordField.getStyleClass().add("field_error");
            return;
        }

        UserService userManager = UserService.getInstance();
        User user = userManager.registerUser(username, password);

        if (user != null) {
            userManager.setCurrentUser(user);
            usernameField.setText("");
            passwordField.setText("");

            // Go to the homepage
            swapPage("/fxml/AccountManagePage.fxml");
        } else {
            // Show error
            errorLabel.setText("Account with that username already exists!");
            usernameField.getStyleClass().add("field_error");
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
