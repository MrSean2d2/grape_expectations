package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


/**
 * Controller for the Wrapper.fxml page (page header).
 *
 * @author team 5
 */
public class MainLayoutController {

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
     * Load the home page.
     *
     * @throws Exception if there is an error loading the page
     */
    @FXML
    private void loadHomePage() throws Exception {
        loadPage("/fxml/newHomePage.fxml");
        homeButton.getStyleClass().add("active");
    }


    /**
     * Load the data list page.
     *
     * @throws Exception if there is an error loading the page
     */
    @FXML
    private void loadDataListPage() throws Exception {
        loadPage("/fxml/DataListPage.fxml");
        dataListButton.getStyleClass().add("active");
    }


    /**
     * Load the map page.
     *
     * @throws Exception if there is an error loading the page
     */
    @FXML
    private void loadMapPage() throws Exception {
        loadPage("/fxml/MapPage.fxml");
        mapButton.getStyleClass().add("active");
    }


    /**
     * Load the account page.
     *
     * @throws Exception if there is an error loading the page
     */
    @FXML
    private void loadAccountPage() throws Exception {
        loadPage("/fxml/AccountPage.fxml");
        accountButton.getStyleClass().add("active");
    }


    /**
     * Loads the detailed wine view page.
     * Detailed view is accessed by double-clicking on a wine in
     * the wine table in view data page -
     * WARNING!:
     * currently not working because detailed view page does not have a pagecontroller
     * so does not get called
     *
     * @throws Exception if there is an error loading the page
     */
    public void loadDetailedViewPage() throws Exception {
        loadPage("/fxml/DetailedViewPage.fxml");
    }


    /**
     * Load a page with a path given as an argument.
     *
     * @param fxml path to fxml file
     * @throws Exception if there is an error loading the page
     */
    private void loadPage(String fxml) throws Exception {
        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource(fxml));
        Node page = baseLoader.load();
        pageContainer.getChildren().setAll(page);

        homeButton.getStyleClass().remove("active");
        dataListButton.getStyleClass().remove("active");
        mapButton.getStyleClass().remove("active");
        accountButton.getStyleClass().remove("active");
    }


    /**
     * Initialize the window.
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) throws Exception {
        loadHomePage();
    }
}
