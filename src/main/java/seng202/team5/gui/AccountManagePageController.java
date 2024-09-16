package seng202.team5.gui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
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
    private Label usernameLabel;

    @FXML
    private Label winesExploredLabel;

    @FXML
    private BorderPane userIconField;

    /**
     * Initialize the account manage page.
     */
    @FXML
    public void initialize() {
        // Create a new cutout circle to display the image in
        Circle circleCutout = new Circle(100, -100, 40);
        Image userIcon = new Image(UserService.getInstance().getCurrentUser().getIcon(), false);
        circleCutout.setFill(new ImagePattern(userIcon));
        circleCutout.setStroke(Color.WHITE);
        circleCutout.setStrokeWidth(10);

        // Add the circular icon to the display box
        userIconField.setCenter(circleCutout);

        // Make the account button change text
        usernameLabel.textProperty().bind(
                Bindings.createStringBinding(() ->
                    UserService.getInstance().getCurrentUser() != null
                    ?
                    UserService.getInstance().getCurrentUser().getUsername()
                    :
                    "Null user!",
                    UserService.getInstance().getUserProperty()
                )
        );
    }

    /**
     * Sign the user out of their account.
     */
    @FXML
    private void signOut() {
        UserService.getInstance().signOut();
        swapPage("/fxml/newHomePage.fxml");
    }
}
