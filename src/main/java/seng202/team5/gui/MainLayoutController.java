package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


/**
 * Controller for the Wrapper.fxml page (page header)
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
        loadPage("/fxml/AccountPage.fxml");
        accountButton.getStyleClass().add("active");
    }


    /**
     * loads the detailed wine view page,
     * detailed view is accessed by double-clicking on a wine in
     * the wine table in view data page -
     * TODO: Make consistent with other loaders
     * @throws Exception
     */
    public void loadDetailedViewPage() throws Exception {
        System.out.println("loading Detailed View page:");
        loadPage("/fxml/DetailedViewPage.fxml");
    }


    /**
     * Load a page with a path given as an argument
     *
     * @param String path to fxml file
     * @throws Exception
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
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) throws Exception {
        loadHomePage();
    }
}
