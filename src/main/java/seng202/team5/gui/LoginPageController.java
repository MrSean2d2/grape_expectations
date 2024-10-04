package seng202.team5.gui;

import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.exceptions.PasswordIncorrectException;
import seng202.team5.models.User;
import seng202.team5.services.ColourLookupService;
import seng202.team5.services.UserService;

/**
 * Controller for the login page.
 *
 * @author Martyn Gascoigne
 */
public class LoginPageController extends PageController implements HasFormErrors {

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
     * Attempt to log in with the provided details.
     */
    @FXML
    public void attemptLogin() {
        resetFieldError(usernameField);
        resetFieldError(passwordField);

        String username = usernameField.getText();

        if (username.isEmpty()) {
            // Show error
            fieldError(usernameField, "Username cannot be empty!");
            return;
        }

        String password = passwordField.getText();

        if (password.isEmpty()) {
            // Show error
            fieldError(passwordField, "Password cannot be empty!");
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
                addNotification(String.format("User %s logged in", user.getUsername()), "#d5e958");
                if (user.getIsAdmin() && userManager.isAdminDefaultPassword()) {
                    userManager.setSelectedUser(user);
                    loadEditPassword();
                    userManager.changedAdminPassword();
                }
                swapPage("/fxml/AccountManagePage.fxml");
            }
        } catch (NotFoundException e) {
            fieldError(usernameField, "User doesn't exist!");
        } catch (PasswordIncorrectException e) {
            fieldError(passwordField, "Password is incorrect!");
        }
    }

    /**
     * Load the edit password popup to force the user to edit their password.
     */
    private void loadEditPassword() {
        try {
            FXMLLoader editPasswordLoader = new FXMLLoader(
                    getClass().getResource("/fxml/EditPasswordPopup.fxml"));
            Parent root = editPasswordLoader.load();
            EditPasswordPopupController controller = editPasswordLoader.getController();
            controller.setClosable(false);
            Stage stage = new Stage();
            stage.setTitle("Edit password");
            Scene scene = new Scene(root);
            String styleSheetUrl = MainWindow.styleSheet;
            scene.getStylesheets().add(styleSheetUrl);
            stage.setMinWidth(controller.minWidth);
            stage.setMinHeight(controller.minHeight);
            stage.setOnCloseRequest(controller::onCloseRequest);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(loginButton.getScene().getWindow());
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    /**
     * Show a field error for the specified field, with an error message.
     *
     * @param field   the field containing the error
     * @param message the error message
     */
    @Override
    public void fieldError(TextField field, String message) {
        fieldError(field);
        errorLabel.setVisible(true);
        errorLabel.setText(message);
    }

    /**
     * Show a field error for the specified field, without an error message.
     *
     * @param field the field containing the error
     */
    @Override
    public void fieldError(TextField field) {
        field.getStyleClass().add("field_error");
    }

    /**
     * Reset the error status of a given field.
     *
     * @param field the field to reset
     */
    @Override
    public void resetFieldError(TextField field) {
        errorLabel.setVisible(false);
        field.getStyleClass().remove("field_error");
    }
}
