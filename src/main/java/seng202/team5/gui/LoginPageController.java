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
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.exceptions.PasswordIncorrectException;
import seng202.team5.models.User;
import seng202.team5.services.OpenWindowsService;
import seng202.team5.services.UserService;

import javax.tools.Tool;

/**
 * Controller for the login page.
 *
 * @author Martyn Gascoigne
 */
public class LoginPageController extends PasswordVisibilityController {

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

    private Image shownIcon;
    private Image hiddenIcon;

    /**
     * Initialize the user page.
     */
    @FXML
    private void initialize() {
        errorLabel.setText(""); //Blank error message

        loginButton.setTooltip(new Tooltip("Log in to account"));
        registerButton.setTooltip(new Tooltip("Go to register page"));


        passwordVisibleField.textProperty().bindBidirectional(
                passwordField.textProperty());

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
        setVisible(passwordVisible, passwordField, passwordVisibleField);
        registerButton.requestFocus();
        toggleVisibility.setImage(passwordVisible ? shownIcon : hiddenIcon);
        if (passwordVisible) {
            Tooltip.install(toggleVisibility, new Tooltip("Hide Password"));
        } else {
            Tooltip.install(toggleVisibility, new Tooltip("View Password"));
        }
    }

    /**
     * Attempt to log in with the provided details.
     */
    @FXML
    public void attemptLogin() {
        resetFieldError(usernameField);
        resetFieldError(passwordField);
        TextField currentPassField = passwordVisible ? passwordVisibleField
                : passwordField;

        String username = usernameField.getText();

        if (username.isEmpty()) {
            // Show error
            fieldError(usernameField, errorLabel, "Username cannot be empty!");
            return;
        }

        String password = passwordField.getText();

        if (password.isEmpty()) {
            // Show error
            fieldError(currentPassField, errorLabel, "Password cannot be empty!");
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

                // Close any open popup windows
                OpenWindowsService.getInstance().closeAllWindows();

                // Go to the homepage
                addNotification(String.format("User %s logged in", user.getUsername()), "#d5e958");
                if (user.getIsAdmin() && userManager.isAdminDefaultPassword()) {
                    userManager.setSelectedUser(user);
                    loadEditPassword();
                    userManager.changedAdminPassword();
                }
                swapPage("/fxml/AccountManagePage.fxml");
            }
        } catch (NotFoundException e) {
            fieldError(usernameField, errorLabel, "User doesn't exist!");
        } catch (PasswordIncorrectException e) {
            fieldError(currentPassField, errorLabel, "Password is incorrect!");
        }
    }

    /**
     * Load the edit password popup to force the user to edit their password.
     */
    private void loadEditPassword() {
        openEditPasswordPopup(false, loginButton.getScene().getWindow());
    }



    /**
     * Attempt to log in if the user presses enter.
     *
     * @param event KeyEvent that triggered the method, pressing of a key
     */
    @FXML
    private void enterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            attemptLogin();
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
