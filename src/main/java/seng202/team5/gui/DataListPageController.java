package seng202.team5.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import seng202.team5.models.Wine;

import java.io.IOException;

/**
 * Controller for the Data List Page.
 */
public class DataListPageController {

    @FXML
    private TableView<Wine> wineTable;

    @FXML
    private TableColumn<Wine, String> nameColumn;

    @FXML
    private TableColumn<Wine, Double> priceColumn;

    @FXML
    private TableColumn<Wine, Integer> yearColumn;

    @FXML
    private TableColumn<Wine, Double> ratingColumn;

    @FXML
    private TableColumn<Wine, Boolean> favouriteColumn;

    @FXML
    private Button varietyButton;
    @FXML
    private Button regionButton;
    @FXML
    private Button yearButton;
    @FXML
    private Button scoreButton;
    @FXML
    private Button priceButton;

    static private PopOver popOver;
    static private VBox popOverRoot;

    /**
     * Initializes the data List by calling {@link seng202.team5.services.WineService}
     * to populate the list of wines.
     */
    @FXML
    public void initialize() {
        popOverRoot = new VBox();
        popOver = new PopOver(popOverRoot);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        //favouriteColumn.setCellValueFactory(new PropertyValueFactory<>("favourite"));

        ObservableList<Wine> wines = FXCollections.observableArrayList(
                MainWindow.appEnvironment.wineService.getWineList());

        // Add data to TableView
        wineTable.setItems(wines);
        wineTable.setOnMouseClicked(mouseEvent -> {
            System.out.println(wineTable.getSelectionModel().getSelectedItem().getName());
            if (mouseEvent.getClickCount() == 2) {
                System.out.println("double clicked");
            }
        });
    }

    @FXML
    public void togglePopupScore() {
        createNewPopup("/fxml/HoverPopup.fxml");
        popOver.show(scoreButton);
    }

    @FXML
    public void togglePopupYear() {
        createNewPopup("/fxml/HoverYear.fxml");
        popOver.show(yearButton);
    }

    @FXML
    public void togglePopupRegion() {
        createNewPopup("/fxml/HoverRegion.fxml");
        popOver.show(regionButton);
    }

    @FXML
    public void togglePopupPrice() {
        createNewPopup("/fxml/HoverPrice.fxml");
        popOver.show(priceButton);
    }

    @FXML
    public void togglePopupFavourite() { }

    @FXML
    public void togglePopupVariety() {
        createNewPopup("/fxml/HoverVariety.fxml");
        popOver.show(varietyButton);
    }


    /**
     * Create a new popover instance from the specified fxml file
     * @param fxml the fxml file to search for
     * @return new popover object
     */
    public void createNewPopup(String fxml){
        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource(fxml));
            Node page = baseLoader.load();
            popOverRoot.getChildren().setAll(page);
            popOver.setContentNode(popOverRoot);

            popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
