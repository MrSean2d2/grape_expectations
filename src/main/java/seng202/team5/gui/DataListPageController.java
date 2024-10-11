package seng202.team5.gui;

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
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;
import seng202.team5.models.Tag;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.DashboardService;
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
    public ComboBox<String> colourComboBox;

    @FXML
    private ComboBox<String> tagComboBox;

    @FXML
    public Slider ratingSlider;
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
    private TagsDAO tagsDAO;
    private String yearFilter;
    private String varietyFilter;
    private String colourFilter;
    private String regionFilter;
    private double minPriceFilter;
    private double maxPriceFilter;
    private double minRatingFilter;
    private double maxRatingFilter;

    private static final Logger log = LogManager.getLogger(DataListPageController.class);
    private boolean addedWine = false;

    /**
     * Initializes the data List by calling {@link seng202.team5.services.WineService}
     * to populate the list of wines.
     */
    @FXML
    private void initialize() {
        vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);
        tagsDAO = new TagsDAO();

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
        setUpTagFilter();

        // Initialises listeners on sliders
        initializeSliderListeners();
        initializeSliderValueListeners();

        // Initialises with search term after vineyard selected from map
        Vineyard selectedVineyard = VineyardService.getInstance().getSelectedVineyard();
        if (selectedVineyard != null) {
            searchTextField.setText(selectedVineyard.getName());
            regionFilter = selectedVineyard.getRegion();
            VineyardService.getInstance().setSelectedVineyard(null);
            applySearchFilters();
        }
        initAdminAction();
        wineTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Initialises with filters after pie slice selected from dashboard
        List<String> selectedPieFilterTerm =
                DashboardService.getInstance().getSelectedPieSliceSearch();

        boolean valid = true;
        for (String term : selectedPieFilterTerm) {
            if (term == null) {
                valid = false;
                break;
            }
        }

        // Check if the term is valid
        if (valid) {
            String category = selectedPieFilterTerm.get(0);
            String filterTerm = selectedPieFilterTerm.get(1);
            tagComboBox.setValue("All Reviews");
            switch (category) {
                case "Variety":
                    varietyFilter = filterTerm;
                    varietyComboBox.setValue(filterTerm);
                    break;
                case "Region":
                    regionFilter = filterTerm;
                    regionComboBox.setValue(filterTerm);
                    break;
                case "Year":
                    yearFilter = filterTerm;
                    yearComboBox.setValue(filterTerm);
                    break;
                case "Colour":
                    colourFilter = filterTerm;
                    if (!filterTerm.equals("Unknown")) {
                        colourComboBox.setValue(filterTerm);
                    }
                    break;
                case "Tag":
                    tagComboBox.setValue(filterTerm);
                    break;
                default:
                    // Any other invalid case, break
                    break;
            }
            DashboardService.getInstance().setSelectedPieSliceSearch(null, null);
        }

        applySearchFilters();
    }

    /**
     * Shows add wine button if user is an admin.
     */
    private void initAdminAction() {
        if (UserService.getInstance().getCurrentUser() != null
                && UserService.getInstance().getCurrentUser().getIsAdmin()) {
            addWineButton.setVisible(true);
            addWineButton.setOnAction(this::addWine);
        }
    }

    /**
     * Opens popup to add new wine.
     *
     * @param event button click event
     */
    private void addWine(ActionEvent event) {
        WineService.getInstance().setSelectedWine(null);
        openPopup("/fxml/EditWinePopup.fxml", "Add new wine", addWineButton.getScene().getWindow());
        addedWine = true;
        applySearchFilters();
        setDefaults();
    }

    /**
     * Initialize listeners to change the slider and slider values in real time to reflect
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
                                + (int) priceRangeSlider.getMin()
                                + " and "
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

        setDefaultVarietyBox();
    }

    /**
     * set default options of variety combobox to all varieties.
     */
    public void setDefaultVarietyBox() {
        List<String> varietyOptions = wineDAO.getVariety();
        setVarietyOptions(varietyOptions);
    }

    /**
     * takes list of variety options and sets variety combo box
     * to have them as options in drop-down.
     *
     * @param varietyOptions list of varieties
     */
    public void setVarietyOptions(List<String> varietyOptions) {
        ObservableList<String> observableVarietyList =
                FXCollections.observableArrayList(varietyOptions);
        observableVarietyList.addFirst("Variety");
        varietyComboBox.setItems(observableVarietyList);
    }

    /**
     * Populates the tag combo box with the user specific tags
     * and initialises the listener for when the box is used.
     */
    private void setUpTagFilter() {
        if (UserService.getInstance().getCurrentUser() != null) {
            // User is logged in, set up the tag combo box
            int userId = UserService.getInstance().getCurrentUser().getId();
            List<Tag> tags = tagsDAO.getFromUser(userId);

            List<String> tagOptions = tags.stream()
                    .map(Tag::getName)
                    .toList();

            ObservableList<String> observableTagList =
                    FXCollections.observableArrayList();
            observableTagList.add("Tag");
            observableTagList.add("All Reviews");
            observableTagList.addAll(tagOptions);
            tagComboBox.setItems(observableTagList);
            tagComboBox.setDisable(false);
        } else {
            tagComboBox.setDisable(true);
        }

        // Handle the tag selection event to filter wines
        tagComboBox.getSelectionModel().selectedItemProperty().addListener((options,
                                                                            oldValue, newValue) -> {
            if (newValue != null) {
                applySearchFilters();
            }
        });
    }

    /**
     * Sets the ComboBox selection to the tag from the dashboard.
     *
     * @param tagFilter The tag to be selected in the ComboBox.
     */
    public void setComboBoxTagSelection(String tagFilter) {
        tagComboBox.getSelectionModel().select(tagFilter);
    }

    /**
     * Set the contents of the variety combo box.
     */
    public void setVarietyComboBox() {
        List<String> varietyOptions;

        // variety filter still at default value
        if (colourFilter.equals("0") || colourFilter.equals("Colour")) {
            varietyOptions = wineDAO.getVariety();
        } else {
            varietyOptions = wineDAO.getVarietyFromColour(colourFilter);
            if (!varietyOptions.contains(varietyFilter)) {
                varietyFilter = "0";
                varietyComboBox.setValue("Variety");
            }
        }
        setVarietyOptions(varietyOptions);
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
        ratingSlider.setValue(minRating);

        minPriceValue.setText(String.valueOf(minPrice));
        maxPriceValue.setText(String.valueOf(maxPrice));
        ratingSliderValue.setText(String.valueOf(minRating));

        // Defaults
        if (!addedWine) {
            this.yearFilter = "0";
            this.varietyFilter = "0";
            this.colourFilter = "0";
            this.regionFilter = "0";
            this.minPriceFilter = 0.0;
            this.maxPriceFilter = 800.0;
            this.minRatingFilter = 0.0;
            this.maxRatingFilter = 100.0;
        }
        addedWine = false;
    }

    /**
     * Applies search when search button is clicked.
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
        String selectedTag = tagComboBox.getValue();
        WineService.getInstance().searchWines(searchTextField.getText(),
                varietyFilter, colourFilter, regionFilter,
                yearFilter, minPriceFilter, maxPriceFilter,
                minRatingFilter, maxRatingFilter, selectedTag);

        // If a tag is selected, apply tag filtering as well
        ObservableList<Wine> observableQueryResults = WineService.getInstance().getWineList();
        wineTable.setItems(observableQueryResults);
        tableResults.setText(wineTable.getItems().size() + " results");
        if (!addedWine) {
            addNotification("Applied Filter", "#d5e958");
        }
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
        if (Objects.equals(selectedVariety, "Variety")) {
            varietyFilter = "0";
        } else if (selectedVariety != null) {
            varietyFilter = selectedVariety;
        }
        applySearchFilters();
    }

    /**
     * Handles action of Colour filter selected.
     */
    public void onColourComboBoxClicked() {
        String selectedColour = String.valueOf(colourComboBox.getValue());
        if (!(Objects.equals(selectedColour, "Colour"))) {
            colourFilter = selectedColour;
            setVarietyComboBox();
            applySearchFilters();
        } else {
            colourFilter = "0";
            setVarietyComboBox();
            applySearchFilters();
        }
    }

    /**
     * Handles action of Region filter selected.
     */
    public void onRegionComboBoxClicked() {
        String selectedRegion = String.valueOf(regionComboBox.getValue());
        if (Objects.equals(selectedRegion, "Region")) {
            regionFilter = "0";
        } else if (selectedRegion != null) {
            regionFilter = selectedRegion;
        }
        applySearchFilters();
    }

    /**
     * Handles action of Year filter selected.
     */
    public void onYearComboBoxClicked() {
        String selectedYear = String.valueOf(yearComboBox.getValue());
        if (Objects.equals(selectedYear, "Year")) {
            yearFilter = "0";
        } else if (selectedYear != null) {
            yearFilter = selectedYear;
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
        colourComboBox.setValue(colourComboBox.getItems().getFirst());
        regionComboBox.setValue(regionComboBox.getItems().getFirst());
        yearComboBox.setValue(yearComboBox.getItems().getFirst());
        if (!tagComboBox.isDisabled()) {
            tagComboBox.setValue(tagComboBox.getItems().getFirst());
        }

        setDefaultVarietyBox();
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
            DetailedViewPageController pageController = loader.getController();
            if (pageController != null) {
                pageController.setHeaderController(headerController);
            }

            Stage stage = new Stage();
            stage.setTitle("Wine Details");
            Scene scene = new Scene(root);

            String styleSheetUrl = MainWindow.styleSheet;
            scene.getStylesheets().add(styleSheetUrl);
            if (pageController != null) {
                pageController.init(stage);
            }

            stage.setScene(scene);
            stage.showAndWait();

            stage.setOnHidden(event -> {
                applySearchFilters();
                setUpTagFilter();
            });
        } catch (Exception e) {
            log.error(e);
        }
    }
}
