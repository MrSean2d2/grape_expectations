package seng202.team5.gui;

import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import seng202.team5.models.User;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.services.OpenWindowsService;
import seng202.team5.services.UserService;

/**
 * Controller for the account page.
 *
 * @author Martyn Gascoigne
 */
public class AccountManagePageController extends PageController {
    @FXML
    private Button signOutButton;

    @FXML
    private Button deleteAccountButton;

    @FXML
    private Button adminButton;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label winesExploredLabel;

    @FXML
    private BorderPane userIconField;

    @FXML
    private Label userIdLabel;

    /**
     * Initialize the account manage page.
     */
    @FXML
    private void initialize() {

        signOutButton.setTooltip(new Tooltip("Sign out of account"));
        deleteAccountButton.setTooltip(new Tooltip("Delete account"));

        // Current user
        User curUser = UserService.getInstance().getCurrentUser();
        if (curUser.getIsAdmin()) {
            adminButton.setDisable(false);

            adminButton.setManaged(true);
            adminButton.setVisible(true);
            adminButton.setTooltip(new Tooltip("Access Admin Page"));
        } else {
            changePasswordButton.setDisable(false);
            changePasswordButton.setVisible(true);
            changePasswordButton.setTooltip(new Tooltip("Change Password"));
            adminButton.setVisible(false);
            adminButton.setManaged(false);
        }

        // Create a new cutout circle to display the image in
        Circle circleCutout = new Circle(100, -100, 40);
        Image userIcon = new Image(curUser.getIcon(), false);
        circleCutout.setFill(new ImagePattern(userIcon));
        circleCutout.setStroke(Color.WHITE);
        circleCutout.setStrokeWidth(10);

        // Add the circular icon to the display box
        userIconField.setCenter(circleCutout);

        // Update favourite count text
        ReviewDAO reviewDAO = new ReviewDAO();
        int numWines = reviewDAO.getFromUser(curUser.getId(), false).size();
        String wineLabel = "wine";
        if (numWines != 1) {
            wineLabel = "wines";
        }
        winesExploredLabel.setText(String.format("%d %s explored", numWines, wineLabel));

        // Update user id field
        userIdLabel.setText(String.format("%08d", curUser.getId()));

        // Make the account button change text
        usernameLabel.textProperty().bind(curUser.usernameProperty());
    }

    /**
     * signs the user out and swaps to log in page.
     */
    private void signOutUserInstance() {
        OpenWindowsService.getInstance().closeAllWindows();

        UserService.getInstance().signOut();
        swapPage("/fxml/LoginPage.fxml");
    }

    /**
     * Sign the user out of their account.
     */
    @FXML
    private void signOut() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sign Out?");
        alert.setHeaderText("Are you sure you want to sign out of your account?");
        alert.setContentText("We'll really miss you!");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Sign out of user account
            signOutUserInstance();
        }
    }

    /**
     * Delete the user's account.
     */
    @FXML
    private void deleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account?");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("All of your reviews will be automatically wiped!");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete the user's account
            UserService.getInstance().deleteUser(UserService.getInstance().getCurrentUser());
            signOutUserInstance();
        }
    }

    /**
     *  Opens the edit password pop up when user wants to change password.
     */
    @FXML
    private void changePassword() {
        UserService userService = UserService.getInstance();
        User curUser = userService.getCurrentUser();
        userService.setSelectedUser(curUser);
        openEditPasswordPopup(true, changePasswordButton.getScene().getWindow());
    }

    /**
     * Loads the admin page.
     */
    @FXML
    private void loadAdminPage() {
        swapPage("/fxml/AdminPage.fxml");
    }
}
