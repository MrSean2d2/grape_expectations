package seng202.team5.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.RangeSlider;
import seng202.team5.models.Wine;
import seng202.team5.services.UserService;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.WineService;
import javafx.fxml.FXMLLoader;



/**
 * Controller for the Data List Page.
 */
public class DataListPageController extends PageController{
    @FXML
    public ComboBox yearComboBox;
    @FXML
    public ComboBox regionComboBox;
    @FXML
    public ComboBox varietyComboBox;

    @FXML
    public Slider ratingSlider;
    @FXML
    public ToggleButton favToggleButton;
    @FXML
    public Label ratingSliderValue;
    @FXML
    public RangeSlider priceRangeSlider;
    @FXML
    public Label maxPriceLabel;
    @FXML
    public Label minPriceLabel;

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
    private double minRatingFilter;
    private double maxRatingFilter;
    private boolean favouriteFilter;

    /**
     * Initializes the data List by calling {@link seng202.team5.services.WineService}
     * to populate the list of wines.
     */
    @FXML
    public void initialize() {
        vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);

        favToggleButton.setDisable(true);
        favToggleButton.setText("Coming Soon");

        initializeSliderListeners();

        // sets value of price/rating labels in real time
        priceRangeSlider.highValueProperty().addListener((ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
            Float value = Float.valueOf(String.format("%.1f", newVal));
            maxPriceLabel.setText(String.valueOf(value));
        });
        priceRangeSlider.lowValueProperty().addListener((ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
            Float value = Float.valueOf(String.format("%.1f", newVal));
            minPriceLabel.setText(String.valueOf(value));
        });
        ratingSlider.valueProperty().addListener((ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
            Float value = Float.valueOf(String.format("%.1f", newVal));
            ratingSliderValue.setText(String.valueOf(value));
        });

        // initialises listeners on sliders


        setDefaults();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        //favouriteColumn.setCellValueFactory(new PropertyValueFactory<>("favourite"));

        ObservableList<Wine> wines = FXCollections.observableArrayList(WineService.getInstance()
                .getWineList());

        // Add data to TableView
        wineTable.setItems(wines);
        System.out.println(wines.size());

        wineTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    try {
                        Wine selectedWine = wineTable.getSelectionModel().getSelectedItem();
                        if (selectedWine != null) {
                            WineService.getInstance().setSelectedWine(selectedWine);
                            openDetailedViewPage(selectedWine);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });



        setUpFilterButtons();
    }

    /**
     * Adds listeners to price and rating slider filters, to handle action of such filters.
     */
    private void initializeSliderListeners() {
        priceRangeSlider.highValueProperty().addListener((ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
            Float value = Float.valueOf(String.format("%.1f", newVal));
            maxPriceFilter = value;
            applySearchFilters();
        });

        priceRangeSlider.lowValueProperty().addListener((ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
            Float value = Float.valueOf(String.format("%.1f", newVal));
            minPriceFilter = value;
            applySearchFilters();
        });

        ratingSlider.valueProperty().addListener((ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
            Float value = Float.valueOf(String.format("%.1f", newVal));
            minRatingFilter = value;
            applySearchFilters();
        });
    }

    /**
     * Initialises items in combo boxes.
     */
    private void setUpFilterButtons() {
        List<String> yearOptions = wineDAO.getYear();
        ObservableList<String> observableYearList = FXCollections.observableArrayList(yearOptions);
        observableYearList.add(0, "Year");
        yearComboBox.setItems(observableYearList);

        List<String> regionOptions = vineyardDAO.getRegions();
        ObservableList<String> observableRegionsList = FXCollections.observableArrayList(regionOptions);
        observableRegionsList.add(0, "Region");
        regionComboBox.setItems(observableRegionsList);

        List<String> varietyOptions = wineDAO.getVariety();
        ObservableList<String> observableVarietyList = FXCollections.observableArrayList(varietyOptions);
        observableVarietyList.add(0, "Variety");
        varietyComboBox.setItems(observableVarietyList);
    }

    /**
     * Sets filters, sliders, and labels to default values.
     */
    private void setDefaults() {
        priceRangeSlider.setHighValue(wineDAO.getMaxPrice());
        priceRangeSlider.setLowValue(wineDAO.getMinPrice());
        ratingSlider.setMin(wineDAO.getMinRating());
        ratingSlider.setMax(wineDAO.getMaxRating());
        priceRangeSlider.setMin(wineDAO.getMinPrice());
        priceRangeSlider.setMax(wineDAO.getMaxPrice());
        minPriceLabel.setText(String.valueOf(wineDAO.getMinPrice()));
        maxPriceLabel.setText(String.valueOf(wineDAO.getMaxPrice()));
        ratingSliderValue.setText(String.valueOf(wineDAO.getMinRating()));
        this.yearFilter = "0";
        this.varietyFilter = "0";
        this.regionFilter = "0";
        this.minPriceFilter = 0.0;
        this.maxPriceFilter = 800.0;
        this.minRatingFilter = 0.0;
        this.maxRatingFilter = 100.0;
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
        String sql = wineDAO.queryBuilder(searchTextField.getText(), varietyFilter,regionFilter,yearFilter,minPriceFilter,maxPriceFilter,minRatingFilter,maxRatingFilter, favouriteFilter);
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
     * Handles action of Variety filter selected.
     */
    public void onVarietyComboBoxClicked() {
        String selectedVariety = String.valueOf(varietyComboBox.getValue());
        if (!(selectedVariety == "Variety" || selectedVariety == null)) {
            varietyFilter = selectedVariety;
        }
        System.out.println(varietyFilter);
        applySearchFilters();
    }

    /**
     * Handles action of Region filter selected.
     */
    public void onRegionComboBoxClicked() {
        String selectedRegion = String.valueOf(regionComboBox.getValue());
        if (!(selectedRegion == "Region" || selectedRegion == null)) {
            regionFilter = selectedRegion;
        }
        applySearchFilters();
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
     * TODO: implement favourite toggle feature
     * Handles action of favourite toggle button being selected.
     */
    public void onFavToggleButtonClicked() {
        boolean isFavourited = favToggleButton.isSelected();
        if (isFavourited != false) {
            favouriteFilter = true;
        }
        applySearchFilters();
    }

    /**
     * Resets search and filters.
     */
    public void onResetSearchFilterButtonClicked() {
        ratingSlider.setValue(0.0);
        ratingSliderValue.setText("0");
        searchTextField.clear();
        wineTable.getItems().clear();
        setDefaults();
        ObservableList<Wine> observableWines = FXCollections.observableArrayList(wineDAO.getAll());
        wineTable.setItems(observableWines);
        varietyComboBox.setValue(varietyComboBox.getItems().get(0));
        regionComboBox.setValue(regionComboBox.getItems().get(0));
        yearComboBox.setValue(yearComboBox.getItems().get(0));

        favToggleButton.setSelected(false);
    }
    /**
     * Opens detailed wine view page for the wine that was double-clicked.
     *
     * @param selectedWine the currently selected wine
     */
    private void openDetailedViewPage(Wine selectedWine) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DetailedViewPage.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Wine Details");
            Scene scene = new Scene(root);

            String styleSheetURL = "/fxml/style.css";
            scene.getStylesheets().add(styleSheetURL);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

}
