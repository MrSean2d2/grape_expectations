package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import seng202.team5.models.User;
import seng202.team5.services.UserService;

/**
 * Controller for the account page
 * @author Martyn Gascoigne
 */
public class AccountPageController {
    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    /**
     * Initialize
     */
    @FXML
    public void initialize() {

    }

    /**
     * Attempt to log in with the provided details
     */
    @FXML
    public void attemptLogin() {
        String username = usernameField.getText();
    }

    /**
     * Attempt to register with the provided details
     */
    @FXML
    public void attemptRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.length() < 1) {
            usernameField.setStyle("-fx-text-box-border: red");
            passwordField.setStyle("-fx-text-box-border: red");
            return;
        }

        UserService userManager = new UserService();
        User user = userManager.registerUser(username, password);

        if (user != null) {
            //switchToMain(user);
            usernameField.setText("");
            passwordField.setText("");
        } else {
            usernameField.setStyle("-fx-text-box-border: red");
            passwordField.setStyle("-fx-text-box-border: red");
        }
    }
}
