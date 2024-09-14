package seng202.team5.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

import java.util.List;

/**
 * Controller for the Data List Page.
 */
public class DataListPageController {
    @FXML

    public ComboBox yearComboBox;
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
    private TextField searchTextField;
    private WineDAO wineDAO;
    private VineyardDAO vineyardDAO;

    private String yearFilter;
    private String varietyFilter;
    private String regionFilter;
    private double minPriceFilter;
    private double maxPriceFilter;
    private double minScoreFilter;
    private double maxScoreFilter;
    private boolean favouriteFilter;

    private void setUpFilterButtons() {
        //TODO: implement better way of initialising
        ObservableList<String> yearOptions = FXCollections.observableArrayList();
        yearOptions.add("Year");
        yearOptions.add("2008");
        yearOptions.add("2009");
        yearOptions.add("2010");
        yearComboBox.setItems(yearOptions);

        //for (String varietyName : ) get list of varieties from DAO
            //varietyMenuButton.getItems().add(new MenuItem(varietyName));
    }

    /**
     * Initializes the data List by calling {@link seng202.team5.services.WineService}
     * to populate the list of wines.
     */
    @FXML
    public void initialize() {
        setDefaultFilters();
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
     * Sets filters to default values.
     */
    private void setDefaultFilters() {
        this.yearFilter = "0";
        this.varietyFilter = "0";
        this.regionFilter = "0";
        this.minPriceFilter = 0.0;
        this.maxPriceFilter = 800.0;
        this.minScoreFilter = 0.0;
        this.maxScoreFilter = 100.0;
        this.favouriteFilter = false;
    }

    /**
     * Gets text from search bar and uses wineDAO to get matching wines
     * to display on table.
     */
    @FXML
    public void searchClicked() {
        String searching = searchTextField.getText();
        System.out.println(searching);
        applySearchFilters();
    }

    /**
     * Apply search and filters and updates table.
     */
    public void applySearchFilters() {
        String sql = wineDAO.queryBuilder(searchTextField.getText(), yearFilter);
        List<Wine> queryResults = wineDAO.executeSearchFilter(sql, searchTextField.getText());
        ObservableList<Wine> observableQueryResults = FXCollections.observableArrayList(queryResults);
        wineTable.setItems(observableQueryResults);
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

    /**
     * Handles action of Year filter selected.
     */
    public void onYearComboBoxClicked() {
        //TODO: come back to - string vs int
        String selectedYear = String.valueOf(yearComboBox.getValue());
        if (!(selectedYear == "Year" || selectedYear == null)) {
            yearFilter = selectedYear;
        }
        applySearchFilters();
    }

    /**
     * Resets search and filters
     */
    public void onResetSearchFilterButtonClicked() {
        searchTextField.clear();
        wineTable.getItems().clear();
        setDefaultFilters();
        ObservableList<Wine> observableWines = FXCollections.observableArrayList(wineDAO.getAll());
        wineTable.setItems(observableWines);
        yearComboBox.setValue(yearComboBox.getItems().get(0));
    }
}
