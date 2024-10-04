package seng202.team5.gui;

import java.util.Objects;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import seng202.team5.models.User;
import seng202.team5.repository.UserDAO;
import seng202.team5.services.UserService;

/**
 * A controller for the password editing popup.
 *
 * @author Sean Reitsma
 */
public class EditPasswordPopupController implements HasFormErrors {

    @FXML
    private Label errorLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordVisibleField;

    @FXML
    private PasswordField repeatPasswordField;

    @FXML
    private TextField repeatPasswordVisibleField;

    @FXML
    private ImageView toggleRepeatVisibility;

    @FXML
    private ImageView toggleVisibility;

    private boolean passwordVisible;
    private boolean repeatPasswordVisible;

    private Image shownIcon;
    private Image hiddenIcon;
    public final int minWidth = 385;
    public final int minHeight = 230;
    private boolean closable = true;

    /**
     * Sets whether this window should be allowed to close when the user hits
     * the window manager close button.
     *
     * @param closable true if the window is allowed to close (default)
     */
    public void setClosable(boolean closable) {
        this.closable = closable;
    }

    /**
     * Initialize the window.
     */
    @FXML
    private void initialize() {
        passwordVisible = false;
        repeatPasswordVisible = false;
        passwordVisibleField.textProperty().bindBidirectional(
                passwordField.textProperty());
        repeatPasswordVisibleField.textProperty().bindBidirectional(
                repeatPasswordField.textProperty());

        shownIcon = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/images/OpenEye.png")));
        hiddenIcon = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/images/ClosedEye.png")));
    }

    /**
     * Window close handler. If the window is set as non-closable it will consume
     * the event and show an error message requesting the user to change their
     * password.
     *
     * @param windowEvent the close event
     */
    public void onCloseRequest(WindowEvent windowEvent) {
        if (closable) {
            close();
        } else {
            windowEvent.consume();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save password?");
            alert.setHeaderText("You need to change your password to continue");
            alert.setContentText("Submit password and exit? Press cancel to keep"
                    + " changing your password.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                submit();
            }
        }
    }

    /**
     * Submit the form and update the password. Validating and showing errors if
     * applicable.
     */
    @FXML
    private void submit() {
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();

        UserService userService = UserService.getInstance();
        String message = userService.checkPassword(password);
        if (message != null) {
            fieldError(passwordField, message);
        } else if (!password.equals(repeatPassword)) {
            fieldError(passwordField);
            fieldError(repeatPasswordField, "Passwords do not match!");
        } else {
            User user = userService.getSelectedUser();
            userService.updateUserPassword(user, password);
            UserDAO userDAO = new UserDAO();
            userDAO.update(user);
            close();
        }
    }

    /**
     * Close the window.
     */
    private void close() {
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.close();
    }

    /**
     * Toggle the password visibility.
     */
    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        setVisible(passwordVisible, passwordField, passwordVisibleField);
        toggleVisibility.setImage(passwordVisible ? shownIcon : hiddenIcon);
    }

    /**
     * Toggle the repeated password visibility.
     */
    @FXML
    private void toggleRepeatPasswordVisibility() {
        repeatPasswordVisible = !repeatPasswordVisible;
        setVisible(repeatPasswordVisible, repeatPasswordField, repeatPasswordVisibleField);
        toggleRepeatVisibility.setImage(repeatPasswordVisible ? shownIcon : hiddenIcon);
    }

    /**
     * Set the visibility status of the text field passed, and inversely set the
     * visibility of the password field passed.
     *
     * @param visible true to set textField visible and passField not visible
     * @param passField the password field
     * @param textField the plain text field
     */
    private void setVisible(boolean visible, PasswordField passField,
                            TextField textField) {
        textField.setVisible(visible);
        textField.setManaged(visible);
        passField.setVisible(!visible);
        passField.setManaged(!visible);
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
        field.getStyleClass().remove("field_error");
    }
}
