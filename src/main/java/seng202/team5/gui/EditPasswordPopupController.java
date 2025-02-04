package seng202.team5.gui;

import java.util.Objects;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
public class EditPasswordPopupController extends FormErrorController {

    @FXML
    public Button editPassButton;

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
    private boolean closable = true;

    /**
     * Initialize the window.
     *
     * @param stage the stage of the window
     * @param closable whether this window should be closeable or not
     */
    public void init(Stage stage, boolean closable) {
        stage.setOnCloseRequest(this::onCloseRequest);
        int minWidth = 385;
        stage.setMinWidth(minWidth);
        int minHeight = 230;
        stage.setMinHeight(minHeight);
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

        errorLabel.setVisible(false);
        editPassButton.setTooltip(new Tooltip("Submit updated password"));

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
    private void onCloseRequest(WindowEvent windowEvent) {
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
            errorLabel.setVisible(true);
            fieldError(passwordField, errorLabel, message);
            fieldError(passwordVisibleField, errorLabel, message);
        } else if (!password.equals(repeatPassword)) {
            fieldError(passwordField);
            fieldError(passwordVisibleField);
            errorLabel.setVisible(true);
            fieldError(repeatPasswordField, errorLabel, "Passwords do not match!");
            fieldError(repeatPasswordVisibleField, errorLabel, "Passwords do not match!");
        } else {
            errorLabel.setVisible(false);
            User user = userService.getSelectedUser();
            userService.updateUserPassword(user, password);
            UserDAO userDAO = new UserDAO();
            userDAO.update(user);
            close();
        }
    }

    /**
     * Handles event of enter pressed.
     *
     * @param event the KeyEvent
     */
    @FXML
    private void enterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            submit();
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
        updateToolTip(toggleVisibility, passwordVisible);
    }

    /**
     * Toggle the repeated password visibility.
     */
    @FXML
    private void toggleRepeatPasswordVisibility() {
        repeatPasswordVisible = !repeatPasswordVisible;
        setVisible(repeatPasswordVisible, repeatPasswordField, repeatPasswordVisibleField);
        toggleRepeatVisibility.setImage(repeatPasswordVisible ? shownIcon : hiddenIcon);
        updateToolTip(toggleRepeatVisibility, passwordVisible);
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
     * Set the tooltip of the toggle of password visibility.
     *
     * @param toggle the imageview to change on toggle
     * @param isVisible if the user wants to hide or view password
     */
    @FXML
    private void updateToolTip(ImageView toggle, boolean isVisible) {
        String toolTipText = isVisible ? "Hide password" : "View password";
        Tooltip.install(toggle, new Tooltip(toolTipText));
    }
}
