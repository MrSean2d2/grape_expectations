package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
    @FXML
    private void loadHomePage() throws Exception {
        loadPage("/fxml/HomePage.fxml");
        homeButton.getStyleClass().add("active");
    }

    @FXML
    private void loadDataListPage() throws Exception {
        loadPage("/fxml/DataListPage.fxml");
        dataListButton.getStyleClass().add("active");
    }

    @FXML
    private void loadMapPage() throws Exception {
        loadPage("/fxml/MapPage.fxml");
        mapButton.getStyleClass().add("active");
    }
    @FXML
    private void loadAccountPage() throws Exception {
        loadPage("/fxml/AccountPage.fxml");
        accountButton.getStyleClass().add("active");
    }

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
