package seng202.team5.gui;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
import org.apache.commons.lang3.NotImplementedException;
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
            } else {
                homeIcon.setImage(new Image(
                        Objects.requireNonNull(
                                getClass().getResourceAsStream("/images/Home.png"))));
            }
        });

        scrollPane.setOnMousePressed((event) -> { // remove weird focus...
            pageContainer.requestFocus();
        });

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
        if (UserService.getInstance().getCurrentUser() != null) {
            throw new NotImplementedException("page not implemented");
        } else {
            loadPage("/fxml/HomePage.fxml");
        }
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
        loadPage("/fxml/MapPage.fxml");
        mapButton.getStyleClass().add("active");
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

        // Task to show loading screen after a delay
        Runnable showLoadingTask = () -> {
            try {
                // Load the loading page
                FXMLLoader baseLoader = new FXMLLoader(
                        getClass().getResource("/fxml/LoadingSpinner.fxml"));
                Node loader = baseLoader.load();
                javafx.application.Platform.runLater(
                        () -> pageContainer.getChildren().setAll(loader));
            } catch (IOException e) {
                log.error(e);
            }
        };

        // Create a scheduled executor service for managing the delay
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Make the loading screen show up if the task is still loading after 0.1 seconds
        var loadingDelay = scheduler.schedule(showLoadingTask, 100, TimeUnit.MILLISECONDS);
        // Update the scene
        createScene.setOnSucceeded(e -> {
            loadingDelay.cancel(false);
            scheduler.shutdown();

            javafx.application.Platform.runLater(
                    () -> pageContainer.getChildren().setAll(createScene.getValue()));
        });

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

            TranslateTransition popUp = new TranslateTransition(Duration.millis(150), notification);
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
