package seng202.team5.gui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seng202.team5.services.UserService;


/**
 * Controller for the Header.fxml page (page header)
 * @author team 5
 */
public class HeaderController {

    @FXML
    private StackPane pageContainer;

    @FXML
    private Button homeButton;

    @FXML
    private Button dataListButton;

    @FXML
    private Button mapButton;

    @FXML
    private Button accountButton;

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) throws Exception {
        loadHomePage();

        // Make the account button change text
//        accountButton.textProperty().bind(
//                Bindings.createStringBinding(() ->
//                                UserService.getInstance().getCurrentUser() != null ?
//                                        "User: " + UserService.getInstance().getCurrentUser().getUsername() : "No user logged in",
//                        UserService.getInstance().getUserProperty()
//                )
//        );
        accountButton.textProperty().bind(
                Bindings.createStringBinding(() ->
                                UserService.getInstance().getCurrentUser() != null ?
                                        "Account" : "Sign In",
                        UserService.getInstance().getUserProperty()
                )
        );
    }

    /**
     * Load the home page
     *
     * @throws Exception
     */
    @FXML
    private void loadHomePage() throws Exception {
        loadPage("/fxml/newHomePage.fxml");
        homeButton.getStyleClass().add("active");
    }

    /**
     * Load the data list page
     *
     * @throws Exception
     */
    @FXML
    private void loadDataListPage() throws Exception {
        loadPage("/fxml/DataListPage.fxml");
        dataListButton.getStyleClass().add("active");
    }


    /**
     * Load the map page
     *
     * @throws Exception
     */
    @FXML
    private void loadMapPage() throws Exception {
        loadPage("/fxml/MapPage.fxml");
        mapButton.getStyleClass().add("active");
    }


    /**
     * Load the account page
     *
     * @throws Exception
     */
    @FXML
    private void loadAccountPage() throws Exception {
        // Load the "login" page if a user is currently not signed in
        // Otherwise, load the "account" page

        // Signed-in user is NOT NULL, so we load the manage page
        if(UserService.getInstance().getCurrentUser() != null) {
            loadPage("/fxml/AccountManagePage.fxml");
        } else {
            loadPage("/fxml/LoginPage.fxml");
        }
        accountButton.getStyleClass().add("active");
    }


    /**
     * loads the detailed wine view page,
     * detailed view is accessed by double-clicking on a wine in
     * the wine table in view data page -
     * WARNING!:
     * currently not working because detailed view page does not have a pagecontroller
     * so does not get called
     * @throws Exception
     */
    public void loadDetailedViewPage() throws Exception {
        loadPage("/fxml/DetailedViewPage.fxml");
    }


    /**
     * Load a page with a path given as an argument
     *
     * @param fxml path to fxml file
     * @throws Exception
     */
    public void loadPage(String fxml) throws Exception {
        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource(fxml));
        Node page = baseLoader.load();

        // Set the header controller reference to the new page controller
        PageController pageController = baseLoader.getController();
        if(pageController != null) {
            pageController.setHeaderController(this);
        }
        pageContainer.getChildren().setAll(page);

        // Reset the active
        homeButton.getStyleClass().remove("active");
        dataListButton.getStyleClass().remove("active");
        mapButton.getStyleClass().remove("active");
        accountButton.getStyleClass().remove("active");
    }
}
