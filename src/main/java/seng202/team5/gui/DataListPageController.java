package seng202.team5.gui;

import java.util.List;
import java.util.Objects;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;
import seng202.team5.models.Wine;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.WineService;



/**
 * Controller for the Data List Page.
 */
public class DataListPageController extends PageController {
    @FXML
    public ComboBox<String> yearComboBox;
    @FXML
    public ComboBox<String> regionComboBox;
    @FXML
    public ComboBox<String> varietyComboBox;

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
    private Button searchButton;

    @FXML
    private Button resetSearchFilterButton;

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
    private static final Logger log = LogManager.getLogger(DataListPageController.class);

    /**
     * Initializes the data List by calling {@link seng202.team5.services.WineService}
     * to populate the list of wines.
     */
    @FXML
    private void initialize() {
        vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);

        favToggleButton.setDisable(true);
        favToggleButton.setText("Coming Soon");

        varietyComboBox.setTooltip(new Tooltip("Filter by variety"));
        regionComboBox.setTooltip(new Tooltip("Filter by region"));
        yearComboBox.setTooltip(new Tooltip("Filter by year"));
        priceRangeSlider.setTooltip(new Tooltip("Select a price range"));
        ratingSlider.setTooltip(new Tooltip("Select a minimum price"));
        searchButton.setTooltip(new Tooltip("Enter search query"));
        resetSearchFilterButton.setTooltip(new Tooltip("Reset search query"));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        ObservableList<Wine> wines = FXCollections.observableArrayList(WineService.getInstance()
                .getWineList());

        // Add data to TableView
        wineTable.setItems(wines);

        wineTable.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                try {
                    Wine selectedWine = wineTable.getSelectionModel().getSelectedItem();
                    if (selectedWine != null) {
                        WineService.getInstance().setSelectedWine(selectedWine);
                        openDetailedViewPage(getHeaderController());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Sets up default filter buttons
        setDefaults();
        setUpFilterButtons();

        // Initialises listeners on sliders
        initializeSliderListeners();
        initializeSliderValueListeners();
    }

    /**
     * Initialize listeners to change the slider values in real time to reflect
     * the current selection.
     */
    private void initializeSliderValueListeners() {
        // sets value of price/rating labels in real time
        priceRangeSlider.highValueProperty().addListener(
                (ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
                    Float value = Float.valueOf(String.format("%.1f", newVal.floatValue()));
                    maxPriceLabel.setText(String.valueOf(value));
                });
        priceRangeSlider.lowValueProperty().addListener(
                (ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
                    Float value = Float.valueOf(String.format("%.1f", newVal.floatValue()));
                    minPriceLabel.setText(String.valueOf(value));
                });
        ratingSlider.valueProperty().addListener(
                (ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
                    Float value = Float.valueOf(String.format("%.1f", newVal.floatValue()));
                    ratingSliderValue.setText(String.valueOf(value));
                });
    }

    /**
     * Adds listeners to price and rating slider filters, to handle action of such filters.
     */
    private void initializeSliderListeners() {
        priceRangeSlider.highValueProperty().addListener(
                (ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
                maxPriceFilter = Float.parseFloat(String.format("%.1f", newVal.floatValue()));
                applySearchFilters();
            });

        priceRangeSlider.lowValueProperty().addListener(
                (ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
                minPriceFilter = Float.parseFloat(String.format("%.1f", newVal.floatValue()));
                applySearchFilters();
            });

        ratingSlider.valueProperty().addListener(
                (ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
                minRatingFilter = Float.parseFloat(String.format("%.1f", newVal.floatValue()));
                applySearchFilters();
            });
    }

    /**
     * Initialises items in combo boxes.
     */
    private void setUpFilterButtons() {
        List<String> yearOptions = wineDAO.getYear();
        ObservableList<String> observableYearList =
                FXCollections.observableArrayList(yearOptions);
        observableYearList.addFirst("Year");
        yearComboBox.setItems(observableYearList);

        List<String> regionOptions = vineyardDAO.getRegions();
        ObservableList<String> observableRegionsList =
                FXCollections.observableArrayList(regionOptions);
        observableRegionsList.addFirst("Region");
        regionComboBox.setItems(observableRegionsList);

        List<String> varietyOptions = wineDAO.getVariety();
        ObservableList<String> observableVarietyList =
                FXCollections.observableArrayList(varietyOptions);
        observableVarietyList.addFirst("Variety");
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
    private void searchClicked() {
        String searching = searchTextField.getText();
        applySearchFilters();
        addNotification("Applied Search", "#d5e958");
    }

    /**
     * Apply search and filters and updates table.
     */
    public void applySearchFilters() {
        String sql = wineDAO.queryBuilder(searchTextField.getText(), varietyFilter, regionFilter,
                yearFilter, minPriceFilter, maxPriceFilter, minRatingFilter,
                maxRatingFilter, favouriteFilter);
        List<Wine> queryResults = wineDAO.executeSearchFilter(sql, searchTextField.getText());
        ObservableList<Wine> observableQueryResults =
                FXCollections.observableArrayList(queryResults);
        wineTable.setItems(observableQueryResults);
    }

    /**
     * When enter key is pressed, functionality of searchClicked is executed.
     *
     * @param event KeyEvent that triggered the method, pressing of a key
     */
    @FXML
    private void enterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchClicked();
        }
    }

    /**
     * Handles action of Variety filter selected.
     */
    public void onVarietyComboBoxClicked() {
        String selectedVariety = String.valueOf(varietyComboBox.getValue());
        if (!(Objects.equals(selectedVariety, "Variety") || selectedVariety == null)) {
            varietyFilter = selectedVariety;
        }
        applySearchFilters();
    }

    /**
     * Handles action of Region filter selected.
     */
    public void onRegionComboBoxClicked() {
        String selectedRegion = String.valueOf(regionComboBox.getValue());
        if (!(Objects.equals(selectedRegion, "Region") || selectedRegion == null)) {
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
        if (!(Objects.equals(selectedYear, "Year") || selectedYear == null)) {
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
        if (isFavourited) {
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
        varietyComboBox.setValue(varietyComboBox.getItems().getFirst());
        regionComboBox.setValue(regionComboBox.getItems().getFirst());
        yearComboBox.setValue(yearComboBox.getItems().getFirst());

        favToggleButton.setSelected(false);
    }

    /**
     * Opens detailed wine view page for the wine that was double-clicked.
     *
     */
    private void openDetailedViewPage(HeaderController headerController) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/DetailedViewPage.fxml"));
            Parent root = loader.load();

            // Set the header controller reference to the new page controller
            PageController pageController = loader.getController();
            if (pageController != null) {
                pageController.setHeaderController(headerController);
            }

            Stage stage = new Stage();
            stage.setTitle("Wine Details");
            Scene scene = new Scene(root);

            String styleSheetUrl = MainWindow.styleSheet;
            scene.getStylesheets().add(styleSheetUrl);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            log.error(e);
        }
    }

}
