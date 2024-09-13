package seng202.team5.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import seng202.team5.models.Wine;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.WineService;

import java.util.ArrayList;
import java.util.List;

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
    private MenuButton varietyMenuButton;

    @FXML
    private TextField searchTextField;
    private WineDAO wineDAO;
    private VineyardDAO vineyardDAO;

    private void setUpFilterButtons() {
        varietyMenuButton.getItems().removeFirst();
        varietyMenuButton.getItems().removeFirst();
        //for (String varietyName : ) get list of varieties from DAO
            //varietyMenuButton.getItems().add(new MenuItem(varietyName));
    }

    /**
     * Initializes the data List by calling {@link seng202.team5.services.WineService}
     * to populate the list of wines.
     */
    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        //favouriteColumn.setCellValueFactory(new PropertyValueFactory<>("favourite"));

        setUpFilterButtons();

        ObservableList<Wine> wines = FXCollections.observableArrayList(WineService.getInstance()
                .getWineList());


        // Add data to TableView
        wineTable.setItems(wines);

        wineTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(wineTable.getSelectionModel().getSelectedItem().getName());
                if (mouseEvent.getClickCount() == 2) {
                    System.out.println("double clicked");
                    MainLayoutController mainLayoutController = new MainLayoutController();
                    try {
                        System.out.println("trying to open Detailed View");
                        Wine selectedWine = wineTable.getSelectionModel().getSelectedItem();
                        if (selectedWine != null) {
                            System.out.println("selectedWine != null");
                            WineService.getInstance().setSelectedWine(selectedWine);
                            mainLayoutController.loadDetailedViewPage();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);
    }

    /**
     * Gets text from search bar and searches list when search button is clicked.
     */
    @FXML
    public void searchClicked() {
        String searching = searchTextField.getText();
        System.out.println(searching);
        List<Wine> searchResults = wineDAO.getSearchedWines(searching);
        ObservableList<Wine> observableSearchResults = FXCollections.observableArrayList(searchResults);
        wineTable.getItems().clear();
        wineTable.setItems(observableSearchResults);
    }

    /**
     * When enter key is pressed, functionality of searchClicked is executed.
     *
     * @param event KeyEvent that triggered the method, pressing of a key
     */
    @FXML
    public void enterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchClicked();
        }
    }
}
