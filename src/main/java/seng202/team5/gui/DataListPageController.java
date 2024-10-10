package seng202.team5.gui;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.UserService;
import seng202.team5.services.VineyardService;
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
    public TextField ratingSliderValue;
    @FXML
    public RangeSlider priceRangeSlider;
    @FXML
    public TextField maxPriceValue;
    @FXML
    public TextField minPriceValue;

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
    private Button addWineButton;

    @FXML
    private TextField searchTextField;
    @FXML
    private Label tableResults;
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
        maxPriceValue.setTooltip(new Tooltip("Set a maximum price"));
        minPriceValue.setTooltip(new Tooltip("Set a minimum price"));
        ratingSlider.setTooltip(new Tooltip("Select a minimum rating"));
        ratingSliderValue.setTooltip(new Tooltip("Set a minimum rating"));
        searchButton.setTooltip(new Tooltip("Enter search query"));
        resetSearchFilterButton.setTooltip(new Tooltip("Reset search query"));


        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        ObservableList<Wine> wines = WineService.getInstance().getWineList();

        // Add data to TableView
        wineTable.setItems(wines);

        wineTable.setPlaceholder(new Label("No matching wines found"));
        tableResults.setText(wineTable.getItems().size() + " results");

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

        Vineyard selectedVineyard = VineyardService.getInstance().getSelectedVineyard();
        if (selectedVineyard != null) {
            searchTextField.setText(selectedVineyard.getName());
            regionFilter = selectedVineyard.getRegion();
            VineyardService.getInstance().setSelectedVineyard(null);
            applySearchFilters();
        }
        initAdminAction();
        wineTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void initAdminAction() {
        if (UserService.getInstance().getCurrentUser() != null
                && UserService.getInstance().getCurrentUser().getIsAdmin()) {
            addWineButton.setVisible(true);
            addWineButton.setOnAction(this::addWine);
        }
    }

    private void addWine(ActionEvent event) {
        WineService.getInstance().setSelectedWine(null);
        try {
            FXMLLoader addWineLoader = new FXMLLoader(getClass().getResource(
                    "/fxml/EditWinePopup.fxml"));
            Parent root = addWineLoader.load();
            EditWinePopupController controller = addWineLoader.getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Add new wine");
            controller.init(stage);
            controller.setHeaderController(getHeaderController());
            String styleSheetUrl = MainWindow.styleSheet;
            scene.getStylesheets().add(styleSheetUrl);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();
            onResetSearchFilterButtonClicked();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize listeners to change the slider values in real time to reflect
     * the current selection.
     */
    private void initializeSliderValueListeners() {
        // sets value of price/rating labels in real time
        priceRangeSlider.highValueProperty().addListener(
                (ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
                    int value = Integer.parseInt(String.format("%.0f", newVal.floatValue()));
                    maxPriceValue.setText(String.valueOf(value));
                });

        minPriceValue.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    double newMinPrice = Double.parseDouble(minPriceValue.getText());
                    if (newMinPrice >= priceRangeSlider.getMin()) {
                        priceRangeSlider.setLowValue(newMinPrice);
                        minPriceFilter = newMinPrice;
                        applySearchFilters();
                    } else {
                        addNotification("Please pick a minimum price greater than "
                                + (int) priceRangeSlider.getMin(), "#d5e958");
                    }
                } catch (NumberFormatException e) {
                    addNotification("Invalid Number", "#d5e958");
                }
            }
        });

        priceRangeSlider.lowValueProperty().addListener(
                (ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
                    int value = Integer.parseInt(String.format("%.0f", newVal.floatValue()));
                    minPriceValue.setText(String.valueOf(value));
                });

        maxPriceValue.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    double newMaxPrice = Double.parseDouble(maxPriceValue.getText());
                    if (newMaxPrice <= priceRangeSlider.getMax()) {
                        priceRangeSlider.setHighValue(newMaxPrice);
                        maxPriceFilter = newMaxPrice;
                        applySearchFilters();
                    } else {
                        addNotification("Please pick a maximum price less than "
                                + (int) priceRangeSlider.getMin(), "#d5e958");
                    }
                } catch (NumberFormatException e) {
                    addNotification("Invalid Number", "#d5e958");
                }
            }
        });

        ratingSlider.valueProperty().addListener(
                (ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
                    int value = Integer.parseInt(String.format("%.0f", newVal.floatValue()));
                    ratingSliderValue.setText(String.valueOf(value));
                });

        ratingSliderValue.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    double newRating = Double.parseDouble(ratingSliderValue.getText());
                    if (newRating <= ratingSlider.getMax()) {
                        ratingSlider.setValue(newRating);
                        minRatingFilter = newRating;
                        applySearchFilters();
                    } else {
                        addNotification("Please pick a minimum rating between "
                                + (int) priceRangeSlider.getMin() + " and "
                                + (int) priceRangeSlider.getMax(), "#d5e958");
                    }
                } catch (NumberFormatException e) {
                    addNotification("Invalid Number", "#d5e958");
                }
            }
        });
    }

    /**
     * Adds listeners to price and rating slider filters, to handle action of such filters.
     */
    private void initializeSliderListeners() {
        priceRangeSlider.setOnMouseReleased(event -> {
            maxPriceFilter = Float.parseFloat(String.format(
                    "%.1f", priceRangeSlider.getHighValue()));
            minPriceFilter = Float.parseFloat(String.format(
                    "%.1f", priceRangeSlider.getLowValue()));
            applySearchFilters();
        });

        ratingSlider.setOnMouseReleased(event -> {
            minRatingFilter = Float.parseFloat(String.format("%.1f", ratingSlider.getValue()));
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
        int minRating = (int) (10 * (Math.floor((double) wineDAO.getMinRating() / 10)));
        ratingSlider.setMin(minRating);

        int maxRating = (int) (10 * (Math.ceil((double) wineDAO.getMaxRating() / 10)));
        ratingSlider.setMax(maxRating);

        int minPrice = (int) (5 * (Math.floor((double) wineDAO.getMinPrice() / 5)));
        priceRangeSlider.setMin(minPrice);

        int maxPrice = (int) (5 * (Math.ceil((double) wineDAO.getMaxPrice() / 5)));
        priceRangeSlider.setMax(maxPrice);

        // Set the initial values
        priceRangeSlider.setLowValue(minPrice);
        priceRangeSlider.setHighValue(maxPrice);

        minPriceValue.setText(String.valueOf(minPrice));
        maxPriceValue.setText(String.valueOf(maxPrice));
        ratingSliderValue.setText(String.valueOf(minRating));

        // Defaults
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
        applySearchFilters();
        addNotification("Applied Search", "#d5e958");
    }

    /**
     * Apply search and filters and updates table.
     */
    public void applySearchFilters() {
        WineService.getInstance().searchWines(searchTextField.getText(), varietyFilter,
                regionFilter, yearFilter, minPriceFilter, maxPriceFilter, minRatingFilter,
                maxRatingFilter, favouriteFilter);

        ObservableList<Wine> observableQueryResults = WineService.getInstance().getWineList();
        wineTable.setItems(observableQueryResults);
        tableResults.setText(wineTable.getItems().size() + " results");
        addNotification("Applied Filter", "#d5e958");
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
        ratingSlider.setValue(ratingSlider.getMin());
        searchTextField.clear();
        wineTable.getItems().clear();
        setDefaults();
        ObservableList<Wine> observableWines = WineService.getInstance().getWineList();
        wineTable.setItems(observableWines);
        tableResults.setText(wineTable.getItems().size() + " results");
        varietyComboBox.setValue(varietyComboBox.getItems().getFirst());
        regionComboBox.setValue(regionComboBox.getItems().getFirst());
        yearComboBox.setValue(yearComboBox.getItems().getFirst());

        favToggleButton.setSelected(false);
        addNotification("Search and filters reset", "#d5e958");
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
            stage.showAndWait();


        } catch (Exception e) {
            log.error(e);
        }
    }

}
