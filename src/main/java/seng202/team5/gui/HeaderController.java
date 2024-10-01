package seng202.team5.gui;

import java.io.IOException;
import java.util.Objects;
import javafx.animation.TranslateTransition;
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
    private ImageView homeIcon;

    @FXML
    private Button dataListButton;

    @FXML
    private Button mapButton;

    @FXML
    private Button accountButton;

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
    public void init(Stage stage) throws Exception {
        loadHomePage();

        scrollPane.setOnMousePressed(Event::consume);
        pageContainer.setOnMousePressed(Event::consume);

        UserService.getInstance().getUserProperty().addListener((observable, oldUser, newUser) -> {
            if (newUser != null) {
                homeIcon.setImage(new Image(
                        Objects.requireNonNull(
                                getClass().getResourceAsStream("/images/Dashboard.png"))));
                homeButton.setTooltip(new Tooltip("Dashboard"));
            } else {
                homeIcon.setImage(new Image(
                        Objects.requireNonNull(
                                getClass().getResourceAsStream("/images/Home.png"))));
                homeButton.setTooltip(new Tooltip("Home page"));
            }
        });

        scrollPane.setOnMousePressed((event) -> { // remove weird focus...
            pageContainer.requestFocus();
        });

        logoButton.setTooltip(new Tooltip("Home page"));
        dataListButton.setTooltip(new Tooltip("Data list page"));
        mapButton.setTooltip(new Tooltip("Map page"));
        accountButton.setTooltip(new Tooltip("Account page"));
    }

    /**
     * Load the home page.
     */
    @FXML
    private void loadHomePage() {
        if (UserService.getInstance().getCurrentUser() != null) {
            loadPage("/fxml/DashboardPage.fxml");
        } else {
            loadPage("/fxml/HomePage.fxml");
        }
    }

    /**
     * Load the data list page.
     */
    @FXML
    private void loadDataListPage() {
        loadPage("/fxml/DataListPage.fxml");
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

        homeButton.getStyleClass().remove("active");
        dataListButton.getStyleClass().remove("active");
        mapButton.getStyleClass().remove("active");
        accountButton.getStyleClass().remove("active");

        mapButton.getStyleClass().add("active");
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
        dataListButton.getStyleClass().remove("active");
        mapButton.getStyleClass().remove("active");
        accountButton.getStyleClass().remove("active");

        // Load the loading page :)
        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/LoadingSpinner.fxml"));
        try {
            Node loader = baseLoader.load();
            pageContainer.getChildren().setAll(loader);
        } catch (IOException e) {
            log.error(e);
        }

        loadedPage = fxml.substring(fxml.lastIndexOf("/") + 1).trim();

        // Add active status to buttons
        switch (loadedPage) {
            case "HomePage.fxml":
            case "DashboardPage.fxml":
                homeButton.getStyleClass().add("active");
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
        createScene.setOnSucceeded(e -> pageContainer.getChildren().setAll(createScene.getValue()));

        // Begin loading
        new Thread(createScene).start();
    }

    /**
     * Add a notification to the top page.
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
