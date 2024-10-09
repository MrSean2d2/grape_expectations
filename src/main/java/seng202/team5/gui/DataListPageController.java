package seng202.team5.gui;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;
import seng202.team5.models.Review;
import seng202.team5.models.Tag;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.*;


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
    private ComboBox<String> tagComboBox;

    @FXML
    public Slider ratingSlider;

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
    private TagsDAO tagsDAO;
    private ReviewDAO reviewDAO;

    private String yearFilter;
    private String varietyFilter;
    private String regionFilter;
    private String tagFilter;
    private double minPriceFilter;
    private double maxPriceFilter;
    private double minRatingFilter;
    private double maxRatingFilter;

    private static final Logger log = LogManager.getLogger(DataListPageController.class);

    /**
     * Initializes the data List by calling {@link seng202.team5.services.WineService}
     * to populate the list of wines.
     */
    @FXML
    private void initialize() {
        vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);
        tagsDAO = new TagsDAO();
        reviewDAO = new ReviewDAO();

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
        System.out.println("setting filter buttons");
        setUpFilterButtons();
        setUpTagFilter();

        // Initialises listeners on sliders
        initializeSliderListeners();
        initializeSliderValueListeners();

        // Initialises with search term after vineyard selected from map
        Vineyard selectedVineyard = VineyardService.getInstance().getSelectedVineyard();
        if (selectedVineyard != null) {
            searchTextField.setText(selectedVineyard.getName());
            applySearchFilters();
            VineyardService.getInstance().setSelectedVineyard(null);
        }

        // Initialises with filters after pie slice selected from dashboard
        List<String> selectedPieFilterTerm = DashboardService.getInstance().getSelectedPieSliceSearch();
        for ( String term : selectedPieFilterTerm) {
            if(term!=null) {
                String category = selectedPieFilterTerm.get(0);
                String filterTerm = selectedPieFilterTerm.get(1);
                switch (category) {
                    case "Variety":
                        varietyComboBox.setValue(filterTerm);
                        break;
                    case "Region":
                        regionComboBox.setValue(filterTerm);
                        break;
                    case "Year":
                        yearComboBox.setValue(filterTerm);
                        break;
                }
                tagComboBox.setValue("All Tags");
                applySearchFilters();
                DashboardService.getInstance().setSelectedPieSliceSearch(null,null);
            }
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
     * Populates the tag combo box with the user specific tags, and initialises the listener for when the box is used.
     */
    private void setUpTagFilter() {
        if (UserService.getInstance().getCurrentUser() != null) {
            // User is logged in, set up the tag combo box
            int userId = UserService.getInstance().getCurrentUser().getId();
            List<Tag> tags = tagsDAO.getFromUser(userId);

            List<String> tagOptions = tags.stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList());

            ObservableList<String> observableTagList =
                    FXCollections.observableArrayList();
            observableTagList.add("Tags");
            observableTagList.add("All Tags");
            observableTagList.addAll(tagOptions);
            tagComboBox.setItems(observableTagList);
            tagComboBox.setDisable(false);
        } else {
            tagComboBox.setDisable(true);
        }

        // Handle the tag selection event to filter wines
        tagComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                filterWinesByTag(newValue);
            }
        });
    }

    /**
     * Filters wines by the selected tag.
     *
     * @param selectedTag tag selected to filter by.
     */
    void filterWinesByTag(String selectedTag) {
        int currentUserId = UserService.getInstance().getCurrentUser().getId();

        List<Integer> wineIds;

        if ("All Tags".equals(selectedTag)) {
            // Show all wines that the current user has reviewed
            List<Review> userReviews = reviewDAO.getFromUser(currentUserId);
            wineIds = userReviews.stream()
                    .map(Review::getWineId)
                    .distinct()
                    .collect(Collectors.toList());
        } else if ("Tags".equals(selectedTag)) {
            wineIds = wineDAO.getAll().stream()
                    .map(Wine::getId)
                    .collect(Collectors.toList()); // Returns all wineIDs
        } else {
            // Filter reviews that contain the selected tag
            List<Review> userReviews = reviewDAO.getFromUser(currentUserId);

            wineIds = userReviews.stream()
                    .filter(review -> {
                        List<Tag> wineTags = tagsDAO.getFromWine(review.getWineId());
                        return wineTags.stream().anyMatch(tag -> tag.getName().equals(selectedTag));
                    })
                    .map(Review::getWineId)
                    .distinct()
                    .collect(Collectors.toList());
        }

        // Fetch the wines based on the filtered wine IDs and apply other filters
        List<Wine> filteredWines = wineDAO.getAll().stream()
                .filter(wine -> wineIds.contains(wine.getId()))   // Tag filtering
                .filter(wine -> varietyFilter.equals("0") || wine.getWineVariety().equals(varietyFilter))  // Variety filter
                .filter(wine -> regionFilter.equals("0") || wine.getRegion().equals(regionFilter))  // Region filter
                .filter(wine -> yearFilter.equals("0") || String.valueOf(wine.getYear()).equals(yearFilter))  // Year filter
                .filter(wine -> wine.getPrice() >= minPriceFilter && wine.getPrice() <= maxPriceFilter)  // Price filter
                .filter(wine -> wine.getRating() >= minRatingFilter)  // Rating filter
                .collect(Collectors.toList());

        ObservableList<Wine> filteredWineList = FXCollections.observableArrayList(filteredWines);
        wineTable.setItems(filteredWineList);
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
                maxRatingFilter);

        List<Wine> queryResults = wineDAO.executeSearchFilter(sql, searchTextField.getText());

        // If a tag is selected, apply tag filtering as well
        String selectedTag = tagComboBox.getValue();
        if (!Objects.equals(selectedTag, "Tags") && selectedTag != null) {
            filterWinesByTag(selectedTag);
        } else {
            ObservableList<Wine> observableQueryResults =
                    FXCollections.observableArrayList(queryResults);
            wineTable.setItems(observableQueryResults);
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
        //TODO: come back to - string vs int
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
        tagComboBox.setValue(tagComboBox.getItems().getFirst());
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
