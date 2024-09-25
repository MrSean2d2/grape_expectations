package seng202.team5.gui;

import java.io.IOException;
import java.util.Optional;

import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seng202.team5.services.UserService;


/**
 * Controller for the Header.fxml page (page header).
 *
 * @author team 5
 */
public class HeaderController {

    @FXML
    private StackPane pageContainer;

    @FXML
    private Button logoButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button dataListButton;

    @FXML
    private Button mapButton;

    @FXML
    private Button accountButton;

    @FXML
    private ScrollPane scrollPane;


    private final HeaderController headerController = this;

    // Used to handle loading
    Task<Node> createScene = null;

    /**
     * Initialize the window.
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) throws Exception {
        loadHomePage();

        scrollPane.setOnMousePressed(Event::consume);
        pageContainer.setOnMousePressed(Event::consume);

        logoButton.setTooltip(new Tooltip("Home page"));
        homeButton.setTooltip(new Tooltip("Home page"));
        dataListButton.setTooltip(new Tooltip("Data list page"));
        mapButton.setTooltip(new Tooltip("Map page"));
        accountButton.setTooltip(new Tooltip("Account page"));
    }

    /**
     * Load the home page.
     *
     * @throws Exception if loading the page fails
     */
    @FXML
    private void loadHomePage() throws Exception {
        loadPage("/fxml/newHomePage.fxml");
        homeButton.getStyleClass().add("active");
    }

    /**
     * Load the data list page.
     *
     * @throws Exception if loading the page fails
     */
    @FXML
    private void loadDataListPage() throws Exception {
        loadPage("/fxml/DataListPage.fxml");
        dataListButton.getStyleClass().add("active");
    }


    /**
     * Load the map page.
     *
     * @throws Exception if loading the page fails
     */
    @FXML
    private void loadMapPage() throws Exception {
        // Cancel an in-progress task if it is currently running
        if (createScene != null && createScene.isRunning()) {
            createScene.cancel(true);
        }

        // Uses different method for loading as WebView messes with the loader
        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/MapPage.fxml"));
        Node loader = baseLoader.load();
        PageController pageController = baseLoader.getController();

        if (pageController != null) {
            pageController.setHeaderController(headerController);
        }

        pageContainer.getChildren().setAll(loader);
        resetActiveButtons(mapButton);
    }


    /**
     * Load the account page.
     *
     * @throws Exception if loading the page fails
     */
    @FXML
    private void loadAccountPage() throws Exception {
        // Load the "login" page if a user is currently not signed in
        // Otherwise, load the "account" page

        // Signed-in user is NOT NULL, so we load the manage page
        if (UserService.getInstance().getCurrentUser() != null) {
            loadPage("/fxml/AccountManagePage.fxml");
        } else {
            loadPage("/fxml/LoginPage.fxml");
        }
        accountButton.getStyleClass().add("active");
    }


    /**
     * Load a page with a path given as an argument.
     *
     * @param fxml path to fxml file
     * @throws Exception if loading the page fails
     */
    public void loadPage(String fxml) throws Exception {
        // Cancel an in-progress task if it is currently running
        if (createScene != null && createScene.isRunning()) {
            createScene.cancel(true);
        }

        // Begin a new task
        createScene = new Task<>() {
            @Override
            public Node call() throws IOException {
                FXMLLoader baseLoader = new FXMLLoader(getClass().getResource(fxml));
                Node page = baseLoader.load();

                // Set the header controller reference to the new page controller
                PageController pageController = baseLoader.getController();

                if (pageController != null) {
                    pageController.setHeaderController(headerController);
                }

                return page;
            }
        };

        // Load the loading page :)
        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/LoadingSpinner.fxml"));
        Node loader = baseLoader.load();

        pageContainer.getChildren().setAll(loader);
        // Update the scene
        createScene.setOnSucceeded(e -> pageContainer.getChildren().setAll(createScene.getValue()));

        // Begin loading
        new Thread(createScene).start();

        // Remove the button styles from the header
        resetActiveButtons();
    }

    /**
     * Reset the header buttons.
     */
    public void resetActiveButtons() {
        homeButton.getStyleClass().remove("active");
        dataListButton.getStyleClass().remove("active");
        mapButton.getStyleClass().remove("active");
        accountButton.getStyleClass().remove("active");
    }

    /**
     * Reset the header buttons and set one to active.
     */
    public void resetActiveButtons(Button button) {
        homeButton.getStyleClass().remove("active");
        dataListButton.getStyleClass().remove("active");
        mapButton.getStyleClass().remove("active");
        accountButton.getStyleClass().remove("active");

        button.getStyleClass().add("active");
    }
}
