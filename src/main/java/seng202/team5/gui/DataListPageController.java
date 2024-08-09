package seng202.team5.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import seng202.team5.Wine;

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
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        favouriteColumn.setCellValueFactory(new PropertyValueFactory<>("favourite"));

        // Populate table with test data

        ObservableList<Wine> wines = FXCollections.observableArrayList(
                new Wine("Chardonnay", "", 2020, 4, 15.99,  true),
                new Wine("Merlot", "", 2018, 4, 10.49, false),
                new Wine("Cabernet Sauvignon", "",  2021, 5, 20.99,true)
        );

        // Add data to TableView
        wineTable.setItems(wines);

    }
}
