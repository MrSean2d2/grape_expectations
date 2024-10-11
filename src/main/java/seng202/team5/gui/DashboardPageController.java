package seng202.team5.gui;

import static seng202.team5.services.ColourLookupService.getTagLabelColour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.models.AssignedTag;
import seng202.team5.models.Tag;
import seng202.team5.repository.AssignedTagsDAO;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.DashboardService;
import seng202.team5.services.TagService;
import seng202.team5.services.UserService;


/**
 * Controller for the Dashboard Page.
 */
public class DashboardPageController extends PageController {

    private static final Logger log = LogManager.getLogger(DashboardPageController.class);

    @FXML
    public Label notEnoughRatingsMessageLabel;
    @FXML
    public RadioButton varietyPieChartButton;
    @FXML
    public RadioButton regionPieChartButton;
    @FXML
    public RadioButton colourPieChartButton;
    @FXML
    public RadioButton yearPieChartButton;
    @FXML
    public RadioButton tagPieChartRadioButton;
    @FXML
    public GridPane radioButtonContainer;
    @FXML
    public Label topColourLabel;
    @FXML
    public Label noTagMessageLabel;
    @FXML
    private PieChart pieChart;

    @FXML
    private Label topVarietyLabel;

    @FXML
    private Label topRegionLabel;

    @FXML
    private Label topYearLabel;

    @FXML
    private VBox userListPane;

    @FXML
    private Label titleLabel;
    private DashboardService dashboardService;
    private TagsDAO tagsDAO;
    private int userId;

    /**
     * Initialises the dashboard page.
     * Fetches user reviews, processes data and updates the pie chart
     */
    @FXML
    private void initialize() {
        userId = UserService.getInstance().getCurrentUser().getId();

        if (userId != 0) {
            titleLabel.setText("Hello, "
                    + UserService.getInstance().getCurrentUser().getUsername()
                    + "!");
        }

        dashboardService = new DashboardService(
                userId,
                new VineyardDAO(),
                new WineDAO(new VineyardDAO()),
                new ReviewDAO());

        // Show error message if the user needs to rate more wines
        int numWinesReviewed = dashboardService.getUserReviews().size();
        if (numWinesReviewed < 5) {
            pieChart.setVisible(false);
            notEnoughRatingsMessageLabel.setText("Rate "
                    + (5 - numWinesReviewed)
                    + " more wine(s) to view Pie Chart Stats!");
            notEnoughRatingsMessageLabel.setVisible(true);
            setPieChartButtons(false);
        } else {
            pieChart.setVisible(true);
            notEnoughRatingsMessageLabel.setVisible(false);
            setPieChartButtons(true);
        }
        updateTopLabels();

        initialiseRadioButtons();

        // Add tables
        tagsDAO = new TagsDAO();

        initialiseTagList();
    }


    /**
     * set radio buttons and container of radio buttons to hide or show depending on if
     * a pie chart can be made (enough wines have been reviewed)
     * @param visibility boolean whether buttons should be shown
     */
    public void setPieChartButtons(boolean visibility) {
        colourPieChartButton.setVisible(visibility);
        regionPieChartButton.setVisible(visibility);
        tagPieChartRadioButton.setVisible(visibility);
        varietyPieChartButton.setVisible(visibility);
        yearPieChartButton.setVisible(visibility);
        radioButtonContainer.setVisible(visibility);
    }

    /**
     * Populates the user data list.
     */
    private void initialiseTagList() {
        // Clear the user list pane in preparation to add the reviewed items
        userListPane.getChildren().clear();

        AssignedTagsDAO assignedTagsDAO = new AssignedTagsDAO();
        List<Tag> tags = tagsDAO.getFromUser(userId);

        // Add default reviewed options
        createNewTagList("My Reviewed Wines",
                dashboardService.getUserReviews().size(),
                -1, 0, false);

        // Add all user tag options
        for (Tag tag : tags) {
            List<AssignedTag> numWines = assignedTagsDAO.getTagsFromUser(
                    userId,
                    tag.getTagId());

            boolean canEdit = (tag.getUserId() == userId);
            createNewTagList(tag.getName(),
                    numWines.size(),
                    tag.getColour(),
                    tag.getTagId(),
                    canEdit);
        }
    }

    /**
     * Populates the pie chart combo box.
     */
    private void initialiseRadioButtons() {
        List<RadioButton> radioButtonList = new ArrayList<>();
        radioButtonList.add(colourPieChartButton);
        radioButtonList.add(varietyPieChartButton);
        radioButtonList.add(regionPieChartButton);
        radioButtonList.add(yearPieChartButton);
        radioButtonList.add(tagPieChartRadioButton);

        ToggleGroup tg = new ToggleGroup();

        for (RadioButton button : radioButtonList) {
            button.getStyleClass().remove("radio-button");
            button.getStyleClass().add("button");
            button.setToggleGroup(tg);
        }

        // set up toggle of radio buttons
        tg.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            RadioButton rb = (RadioButton) tg.getSelectedToggle();
            if (rb != null) {
                String pieChartType = rb.getText();
                updatePieChartData(pieChartType);
            }
        });
        // Initialise pie chart with variety default
        varietyPieChartButton.setSelected(true);
    }

    /**
     * Populates the top labels for user preferences.
     */
    private void updateTopLabels() {
        // get max
        List<Map.Entry<String, Integer>> topVariety = dashboardService.getTopVariety();
        List<Map.Entry<String, Integer>> topRegion = dashboardService.getTopRegion();
        List<Map.Entry<Integer, Integer>> topYear = dashboardService.getTopYear();
        List<Map.Entry<String, Integer>> topColour = dashboardService.getTopColour();

        if (!topVariety.isEmpty()) {
            topVarietyLabel.setText(topVariety.getFirst().getKey());
        }

        if (!topRegion.isEmpty()) {
            topRegionLabel.setText(topRegion.getFirst().getKey());
        }

        if (!topYear.isEmpty()) {
            topYearLabel.setText(String.valueOf(topYear.getFirst().getKey()));
        }
        if (!topColour.isEmpty()) {
            topColourLabel.setText(String.valueOf(topColour.getFirst().getKey()));
        }
    }

    /**
     * Create a new list of tags on the dashboard page.
     *
     * @param name the name to display
     * @param numWines the number of wines to display
     * @param colour the background colour of the chip
     * @param id id of the tag list
     * @param canEdit if the tag list can be edited by the user
     */
    public void createNewTagList(String name, int numWines, int colour, int id, boolean canEdit) {
        Label nameTag = new Label(name);
        nameTag.setStyle(nameTag.getStyle() + "-fx-font-weight: 700;");
        nameTag.setPadding(new Insets(5));

        // Show the number of wine(s)
        Label optionTag = new Label(numWines + " Wine" + (numWines != 1 ? "s" : ""));
        optionTag.setAlignment(Pos.CENTER_RIGHT);
        optionTag.setMaxWidth(Double.MAX_VALUE);
        optionTag.setPadding(new Insets(5));
        HBox.setHgrow(optionTag, Priority.ALWAYS);

        // Edit button
        HBox tagContainer = new HBox(nameTag, optionTag);
        tagContainer.setAlignment(Pos.CENTER_LEFT);
        tagContainer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(tagContainer, Priority.ALWAYS);
        tagContainer.getStyleClass().addAll("tag", "max-width");

        // Wrap in a bigger box to allow for the edit button
        HBox biggerContainer = new HBox(tagContainer);
        biggerContainer.getStyleClass().add("max-width");
        biggerContainer.setAlignment(Pos.CENTER_LEFT);

        // Add an edit button if the user has the ability to
        if (canEdit) {
            Label editButton = new Label("Edit");
            editButton.setAlignment(Pos.CENTER_RIGHT);
            editButton.setMaxHeight(Double.MAX_VALUE);
            HBox.setMargin(editButton, new Insets(0, 0, 0, 10));
            editButton.getStyleClass().add("button");
            editButton.setCursor(Cursor.HAND);

            // Click event - if the user can edit it
            editButton.setOnMouseClicked(event -> {
                try {
                    // Set the selected tag to the tag represented by this list
                    Tag selectedTag = tagsDAO.getOne(id);
                    TagService.getInstance().setSelectedTag(selectedTag);

                    TagService.getInstance().showEditTagPopup(userListPane.getScene().getWindow(),
                            getHeaderController());
                    // Clear and refresh
                    initialiseTagList();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // Append the edit button to the end of the container
            biggerContainer.getChildren().add(editButton);
        }

        // If the tag has a colour, add it to the container
        if (colour != -1) {
            tagContainer.getStyleClass().add(getTagLabelColour(colour));
        }

        // More general styling stuff
        tagContainer.setStyle(tagContainer.getStyle() + "-fx-background-radius: 5;");

        // Set click event to pass tag name to DataListPage
        tagContainer.setOnMouseClicked(event -> {
            try {
                HeaderController headerController = getHeaderController();
                if (id == 0) {
                    // Case for when top button is clicked
                    headerController.loadDataListPageWithTag("All Reviews");
                } else {
                    // Pass tag name or ID to DataListPage
                    headerController.loadDataListPageWithTag(name);
                }

            } catch (Exception e) {
                log.error(e);
            }
        });

        userListPane.getChildren().add(biggerContainer);
    }


    /**
     * Add a new tag.
     */
    @FXML
    private void addNewTag() {
        // Try to create a new tag
        try {
            // Set the selected tag to the tag represented by this list
            TagService.getInstance().setSelectedTag(null);
            TagService.getInstance().showEditTagPopup(userListPane.getScene().getWindow(),
                    getHeaderController());
            // Clear and refresh
            initialiseTagList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the pie chart data based on selected category.
     *
     * @param category selected for pie chart
     */
    public void updatePieChartData(String category) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        List<PieChart.Data> dataList = new ArrayList<>();
        noTagMessageLabel.setVisible(false);
        int numWinesReviewed = dashboardService.getUserReviews().size();
        if (numWinesReviewed < 5) {
            pieChart.setVisible(false);
            notEnoughRatingsMessageLabel.setVisible(true);
        }

        switch (category) {
            case "Variety":
                List<Map.Entry<String, Integer>> topVariety = dashboardService.getTopVariety();
                for (Map.Entry<String, Integer> entryVariety : topVariety) {
                    if (entryVariety.getValue() > 0) {
                        dataList.add(new PieChart.Data(
                                entryVariety.getKey(),
                                entryVariety.getValue()));
                    }
                }
                break;
            case "Region":
                List<Map.Entry<String, Integer>> topRegion = dashboardService.getTopRegion();
                for (Map.Entry<String, Integer> entryRegion : topRegion) {
                    if (entryRegion.getValue() > 0) {
                        dataList.add(new PieChart.Data(
                                entryRegion.getKey(),
                                entryRegion.getValue()));
                    }
                }
                break;
            case "Year":
                List<Map.Entry<Integer, Integer>> topYear = dashboardService.getTopYear();
                for (Map.Entry<Integer, Integer> entryYear : topYear) {
                    if (entryYear.getValue() > 0) {
                        dataList.add(new PieChart.Data(
                                String.valueOf(entryYear.getKey()),
                                entryYear.getValue()));
                    }
                }
                break;
            case "Colour":
                List<Map.Entry<String, Integer>> topColour = dashboardService.getTopColour();
                for (Map.Entry<String, Integer> entryColour : topColour) {
                    if (entryColour.getValue() > 0) {
                        dataList.add(new PieChart.Data(
                                String.valueOf(entryColour.getKey()),
                                entryColour.getValue()));
                    }
                }
                break;
            case "Tags":
                List<Map.Entry<String, Integer>> topTags = dashboardService.getTopTags();
                if (topTags.isEmpty()) {
                    noTagMessageLabel.setVisible(true);
                    notEnoughRatingsMessageLabel.setVisible(false);
                } else {
                    noTagMessageLabel.setVisible(false);
                    for (Map.Entry<String, Integer> entryTag : topTags) {
                        if (entryTag.getValue() > 0) {
                            dataList.add(new PieChart.Data(
                                    String.valueOf(entryTag.getKey()), entryTag.getValue()));
                        }
                    }
                }

                break;
            default:
                // Don't add any data!!!
                break;
        }

        // Set the pie chart data and parameters
        pieChartData.addAll(dataList);
        pieChart.setData(pieChartData);
        pieChart.setTitle("Favourite " + category);
        pieChart.setClockwise(true);
        pieChart.setStartAngle(180);
        pieChart.setLabelsVisible(true);

        for (final PieChart.Data data : pieChart.getData()) {
            double total = pieChart.getData().stream().mapToDouble(
                    PieChart.Data::getPieValue).sum();
            pieChart.getData().forEach(pieData -> {
                String percentage = String.format("%.2f%%",
                        ((pieData.getPieValue() / total) * 100));
                Tooltip tooltip = new Tooltip(percentage);
                Tooltip.install(pieData.getNode(), tooltip);
            });
            data.getNode().setOnMouseClicked(event -> {
                String filterTerm = data.getName();
                DashboardService.getInstance().setSelectedPieSliceSearch(category, filterTerm);
                swapPage("/fxml/DataListPage.fxml");
            });
        }
    }
}


