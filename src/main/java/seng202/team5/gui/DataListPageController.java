package seng202.team5.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import seng202.team5.models.Wine;
import seng202.team5.services.WineService;

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

        ObservableList<Wine> wines = FXCollections.observableArrayList(
                MainWindow.appEnvironment.wineService.getWineList());

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
    }
}
