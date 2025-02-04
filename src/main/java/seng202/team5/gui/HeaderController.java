package seng202.team5.gui;

import java.io.IOException;
import java.util.Objects;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.services.UserService;


/**
 * Controller for the Header.fxml page (page header).
 *
 * @author team 5
 */
public class HeaderController {

    private static final Logger log = LogManager.getLogger(HeaderController.class);
    @FXML
    private StackPane pageContainer;

    @FXML
    private Button logoButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button dataListButton;

    @FXML
    private Button mapButton;

    @FXML
    private Button accountButton;

    @FXML
    private ImageView accountIcon;

    @FXML
    private ScrollPane scrollPane;


    private final HeaderController headerController = this;
    private String loadedPage = "";

    // Used to handle loading
    Task<Node> createScene = null;

    /**
     * Initialize the window.
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        loadHomePage();
        dashboardButton.setVisible(false);
        dashboardButton.setManaged(false);

        scrollPane.setOnMousePressed(Event::consume);
        pageContainer.setOnMousePressed(Event::consume);

        UserService.getInstance().getUserProperty().addListener((observable, oldUser, newUser) -> {
            if (newUser != null) {
                dashboardButton.setVisible(true);
                dashboardButton.setManaged(true);

                accountIcon.setImage(new Image(
                        Objects.requireNonNull(
                                getClass().getResourceAsStream(newUser.getIcon()))));
            } else {
                dashboardButton.setVisible(false);
                dashboardButton.setManaged(false);

                accountIcon.setImage(new Image(
                        Objects.requireNonNull(
                                getClass().getResourceAsStream("/images/User.png"))));
            }
        });

        scrollPane.setOnMousePressed((event) -> { // remove weird focus...
            pageContainer.requestFocus();
        });

        logoButton.setTooltip(new Tooltip("Home page"));
        homeButton.setTooltip(new Tooltip("Home page"));
        dashboardButton.setTooltip(new Tooltip("Dashboard page"));
        dataListButton.setTooltip(new Tooltip("Data list page"));
        mapButton.setTooltip(new Tooltip("Map page"));
        accountButton.setTooltip(new Tooltip("Account page"));
    }

    /**
     * Load the home page.
     */
    @FXML
    private void loadHomePage() {
        loadPage("/fxml/HomePage.fxml");
    }

    /**
     * Load the dashboard page.
     */
    @FXML
    private void loadDashboardPage() {
        loadPage("/fxml/DashboardPage.fxml");
    }

    /**
     * Load the data list page.
     */
    @FXML
    private void loadDataListPage() {
        loadPage("/fxml/DataListPage.fxml");
    }

    /**
     * Loads the data list page from dashboard with tag filter.
     *
     * @param tagFilter selected tag to filter with on data list page
     * @throws Exception for load
     */
    @FXML
    void loadDataListPageWithTag(String tagFilter) throws Exception {
        // Load the loading page first
        FXMLLoader loaderSpinner = new FXMLLoader(
                getClass().getResource("/fxml/LoadingSpinner.fxml"));
        Node loader = loaderSpinner.load();

        // Display the loading spinner in the container
        pageContainer.getChildren().setAll(loader);

        // Create a Task to load the DataListPage in the background
        Task<Node> loadDataListTask = new Task<>() {
            @Override
            protected Node call() throws Exception {
                // Load the Data List page (this will run in a background thread)
                FXMLLoader baseLoader = new FXMLLoader(
                        getClass().getResource("/fxml/DataListPage.fxml"));
                Node page = baseLoader.load();

                // Get the controller for the Data List page
                DataListPageController dataListPageController = baseLoader.getController();

                // Pass the tag filter to the controller
                if (dataListPageController != null) {
                    dataListPageController.setComboBoxTagSelection(tagFilter);
                }

                return page;
            }
        };


        loadDataListTask.setOnSucceeded(event -> {
            pageContainer.getChildren().setAll(loadDataListTask.getValue());

            // Update the button styles
            dataListButton.getStyleClass().add("active");
            homeButton.getStyleClass().remove("active");
            dashboardButton.getStyleClass().remove("active");
            mapButton.getStyleClass().remove("active");
            accountButton.getStyleClass().remove("active");
            dashboardButton.getStyleClass().remove("active");

        });


        loadDataListTask.setOnFailed(event -> {
            Throwable exception = loadDataListTask.getException();

            System.err.println("Failed to load the Data List page: " + exception.getMessage());
        });

        new Thread(loadDataListTask).start();
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
        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/LoadingSpinner.fxml"));
        Node loadingScreen = baseLoader.load();
        pageContainer.getChildren().setAll(loadingScreen);
        PauseTransition pause = new PauseTransition(Duration.millis(200));

        pause.setOnFinished(event -> {
            try {
                FXMLLoader mapLoader = new FXMLLoader(getClass().getResource("/fxml/MapPage.fxml"));
                Node mapPage = mapLoader.load();
                PageController pageController = mapLoader.getController();

                if (pageController != null) {
                    pageController.setHeaderController(headerController);
                }

                pageContainer.getChildren().setAll(mapPage);

                // Update active button styles
                homeButton.getStyleClass().remove("active");
                dashboardButton.getStyleClass().remove("active");
                dataListButton.getStyleClass().remove("active");
                mapButton.getStyleClass().remove("active");
                accountButton.getStyleClass().remove("active");
                mapButton.getStyleClass().add("active");
            } catch (IOException e) {
                log.error("Failed to load MapPage.fxml: ", e);
            }
        });

        pause.play();
    }

    /**
     * Load the account page.
     */
    @FXML
    private void loadAccountPage() {
        // Load the "login" page if a user is currently not signed in
        // Otherwise, load the "account" page

        // Signed-in user is NOT NULL, so we load the manage page
        if (UserService.getInstance().getCurrentUser() != null) {
            loadPage("/fxml/AccountManagePage.fxml");
        } else {
            loadPage("/fxml/LoginPage.fxml");
        }
    }


    /**
     * Load a page with a path given as an argument.
     *
     * @param fxml path to fxml file
     */
    public void loadPage(String fxml) {
        // Cancel an in-progress task if it is currently running
        if (createScene != null && createScene.isRunning()) {
            createScene.cancel(true);
        }

        setHeaderInteractionEnabled(false);

        // Begin a new task
        createScene = new Task<>() {
            @Override
            public Node call() {
                FXMLLoader baseLoader = new FXMLLoader(getClass().getResource(fxml));

                try {
                    Node page = baseLoader.load();

                    // Set the header controller reference to the new page controller
                    PageController pageController = baseLoader.getController();
                    if (pageController != null) {
                        pageController.setHeaderController(headerController);
                    }
                    return page;
                } catch (IOException e) {
                    log.error(e);
                }

                return null;
            }
        };

        // Remove the button styles from the header
        homeButton.getStyleClass().remove("active");
        dashboardButton.getStyleClass().remove("active");
        dataListButton.getStyleClass().remove("active");
        mapButton.getStyleClass().remove("active");
        accountButton.getStyleClass().remove("active");

        // Load the loading page :)
        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/LoadingSpinner.fxml"));
        try {
            Node loader = baseLoader.load();
            Platform.runLater(() -> pageContainer.getChildren().setAll(loader));
        } catch (IOException e) {
            log.error(e);
        }

        loadedPage = fxml.substring(fxml.lastIndexOf("/") + 1).trim();

        // Add active status to buttons
        switch (loadedPage) {
            case "HomePage.fxml":
                homeButton.getStyleClass().add("active");
                break;
            case "DashboardPage.fxml":
                dashboardButton.getStyleClass().add("active");
                break;

            case "DataListPage.fxml":
                dataListButton.getStyleClass().add("active");
                break;

            case "MapPage.fxml":
                mapButton.getStyleClass().add("active");
                break;

            case "LoginPage.fxml":
            case "RegisterPage.fxml":
            case "AccountManagePage.fxml":
                accountButton.getStyleClass().add("active");
                break;
            default:
                break;
        }
        // Update the scene
        createScene.setOnSucceeded(e -> {
            pageContainer.getChildren().setAll(createScene.getValue());
            setHeaderInteractionEnabled(true); // Re-enable interactions after loading
        });


        // Begin loading
        new Thread(createScene).start();
    }

    /**
     * Disables/enabled interaction with buttons in header.
     *
     * @param enabled if buttons should be enabled or disabled
     */
    private void setHeaderInteractionEnabled(boolean enabled) {
        logoButton.setDisable(!enabled);
        homeButton.setDisable(!enabled);
        dashboardButton.setDisable(!enabled);
        dataListButton.setDisable(!enabled);
        mapButton.setDisable(!enabled);
        accountButton.setDisable(!enabled);
    }

    /**
     * Add a notification to the top page.
     *
     * @param text the text to use for the notification
     * @param col the colour to use on the notification
     */
    public void addNotification(String text, String col) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Notification.fxml"));
            Node notification = loader.load();
            pageContainer.getChildren().add(notification);
            StackPane.setAlignment(notification, Pos.BOTTOM_CENTER);

            NotificationController notificationController = loader.getController();
            notificationController.setText(text);
            notificationController.setColourBand(col);

            TranslateTransition popUp = new TranslateTransition(
                    Duration.millis(150), notification);
            popUp.setFromY(100);
            popUp.setToY(-20);

            TranslateTransition popDown = new TranslateTransition(
                    Duration.millis(150), notification);
            popDown.setFromY(-20);
            popDown.setToY(100);

            popUp.play();

            popUp.setOnFinished(e -> {
                popDown.setDelay(Duration.seconds(3));
                popDown.play();
                popDown.setOnFinished(event -> pageContainer.getChildren().remove(notification));
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
