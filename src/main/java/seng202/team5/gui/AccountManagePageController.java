package seng202.team5.gui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import seng202.team5.models.User;
import seng202.team5.repository.DrinksDAO;
import seng202.team5.repository.UserDAO;
import seng202.team5.services.UserService;

import java.util.Optional;

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
    private Label usernameLabel;

    @FXML
    private Label winesExploredLabel;

    @FXML
    private BorderPane userIconField;

    @FXML
    private Label userIdLabel;

    @FXML
    private Label userLocationLabel;

    @FXML
    private Label userJoinDateLabel;

    /**
     * Initialize the account manage page.
     */
    @FXML
    private void initialize() {

        signOutButton.setTooltip(new Tooltip("Sign out of account"));
        deleteAccountButton.setTooltip(new Tooltip("Delete account"));

        // Current user
        User curUser = UserService.getInstance().getCurrentUser();

        // Create a new cutout circle to display the image in
        Circle circleCutout = new Circle(100, -100, 40);
        Image userIcon = new Image(curUser.getIcon(), false);
        circleCutout.setFill(new ImagePattern(userIcon));
        circleCutout.setStroke(Color.WHITE);
        circleCutout.setStrokeWidth(10);

        // Add the circular icon to the display box
        userIconField.setCenter(circleCutout);

        // Update favourite count text
        DrinksDAO drinksDAO = new DrinksDAO();
        int numWines = drinksDAO.getFromUser(curUser.getId()).size();
        String wineLabel = "wine";
        if (numWines != 1) {
            wineLabel = "wines";
        }
        winesExploredLabel.setText(String.format("%d %s explored", numWines, wineLabel));

        // Update user id field
        userIdLabel.setText(String.format("%08d", curUser.getId()));

        // Update user location field
        userLocationLabel.setText("---");

        // Update user join date field
        userJoinDateLabel.setText("---");

        // Make the account button change text
        usernameLabel.textProperty().bind(
                Bindings.createStringBinding(() ->
                    curUser != null
                    ? curUser.getUsername() : "Null user!",
                    UserService.getInstance().getUserProperty()
                )
        );
    }

    /**
     * signs the user out and swaps to log in page
     */
    private void signOutUserInstance() {
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
     * Delete the user's account
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
            UserDAO userDAO = new UserDAO();
            userDAO.delete(UserService.getInstance().getCurrentUser().getId());
            signOutUserInstance();
        }
    }
}
