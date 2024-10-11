package seng202.team5.gui;

import static seng202.team5.services.ColourLookupService.getTagLabelColour;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import seng202.team5.models.AssignedTag;
import seng202.team5.models.Tag;
import seng202.team5.repository.AssignedTagsDAO;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.DashboardService;
import seng202.team5.services.UserService;


/**
 * Controller for the Dashboard Page.
 */
public class DashboardPageController extends PageController {
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
     * Initialises the dashboard page
     * Fetches user reviews, processes data and updates the pie chart
     */
    @FXML
    private void initialize() {
        int userID = UserService.getInstance().getCurrentUser().getId();

        if (userID != 0) {
            titleLabel.setText("Hello, " + UserService.getInstance().getCurrentUser().getUsername() + "!");
        }

        dashboardService = new DashboardService(
                userID,
                new VineyardDAO(),
                new WineDAO(new VineyardDAO()),
                new ReviewDAO());

        // Show error message if the user needs to rate more wines
        int numWinesReviewed = dashboardService.getUserReviews().size();
        if (numWinesReviewed < 5) {
            pieChart.setVisible(false);
            notEnoughRatingsMessageLabel.setText("Rate " + (5 - numWinesReviewed) + " more wine(s) to view Pie Chart Stats!");
            notEnoughRatingsMessageLabel.setVisible(true);
        } else {
            pieChart.setVisible(true);
            notEnoughRatingsMessageLabel.setVisible(false);
        }
        updateTopLabels();

        initialiseRadioButtons();

        // Add tables
        TagsDAO tagsDAO = new TagsDAO();
        AssignedTagsDAO assignedTagsDAO = new AssignedTagsDAO();
        List<Tag> tags = tagsDAO.getFromUser(userId);

        // Add default reviewed options
        createNewTagList("My Reviewed Wines", dashboardService.getUserReviews().size(), -1);

        // Add all of the user tag options
        for (Tag tag : tags) {
            /*
            Get a list of the assigned tags
            This could be used to get the list of wines that have this tag
            as the assigned tag would have a wineID also
             */
            List<AssignedTag> numWines = assignedTagsDAO.getTagsFromUser(
                    userId,
                    tag.getTagId());

            createNewTagList(tag.getName(), numWines.size(), tag.getColour());
        }
    }

    /**
     * Populates the pie chart combo box
     */
    private void initialiseRadioButtons() {
        List<RadioButton> radioButtonList = new ArrayList<>();
        radioButtonList.add(colourPieChartButton);
        radioButtonList.add(varietyPieChartButton);
        radioButtonList.add(regionPieChartButton);
        radioButtonList.add(yearPieChartButton);
        radioButtonList.add(tagPieChartRadioButton);

        ToggleGroup tg = new ToggleGroup();

        for(RadioButton button: radioButtonList){
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
     * Populates the top labels for user preferences
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
     */
    public void createNewTagList(String name, int numWines, int colour) {
        Label nameTag = new Label(name);
        nameTag.setStyle(nameTag.getStyle() + "-fx-font-weight: 700;");
        nameTag.setPadding(new Insets(5));
        Label optionTag = new Label(numWines + " Wines");
        optionTag.setAlignment(Pos.CENTER_RIGHT);
        optionTag.setMaxWidth(Double.MAX_VALUE);
        optionTag.setPadding(new Insets(5));
        HBox.setHgrow(optionTag, Priority.ALWAYS);

        HBox tagContainer = new HBox(nameTag, optionTag);
        tagContainer.getStyleClass().addAll("tag", "max-width");

        if (colour != -1) {
            tagContainer.getStyleClass().add(getTagLabelColour(colour));
        }

        tagContainer.setStyle(tagContainer.getStyle() + "-fx-background-radius: 5;");

        // Set click event to pass tag name to DataListPage
        tagContainer.setOnMouseClicked(event -> {
            try {
                HeaderController headerController = getHeaderController();
                if (name.equals("My Reviewed Wines")) {
                    headerController.loadDataListPageWithTag("All Tags"); // Case for when top button is clicked
                } else {
                    headerController.loadDataListPageWithTag(name); // Pass tag name or ID to DataListPage
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        userListPane.getChildren().add(tagContainer);
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
                for ( Map.Entry<String, Integer> entryVariety : topVariety) {
                    if (entryVariety.getValue() > 0) {
                        dataList.add(new PieChart.Data(entryVariety.getKey(), entryVariety.getValue()));
                    }
                }
                break;
            case "Region":
                List<Map.Entry<String, Integer>> topRegion = dashboardService.getTopRegion();
                for ( Map.Entry<String, Integer> entryRegion : topRegion) {
                    if (entryRegion.getValue() > 0) {
                        dataList.add(new PieChart.Data(entryRegion.getKey(), entryRegion.getValue()));
                    }
                }
                break;
            case "Year":
                List<Map.Entry<Integer, Integer>> topYear = dashboardService.getTopYear();
                for ( Map.Entry<Integer, Integer> entryYear : topYear) {
                    if (entryYear.getValue() > 0) {
                    dataList.add(new PieChart.Data(String.valueOf(entryYear.getKey()), entryYear.getValue()));
                    }
                }
                break;
            case "Colour":
                List<Map.Entry<String, Integer>> topColour = dashboardService.getTopColour();
                for ( Map.Entry<String, Integer> entryColour : topColour) {
                    if (entryColour.getValue() > 0) {
                        dataList.add(new PieChart.Data(String.valueOf(entryColour.getKey()), entryColour.getValue()));
                    }
                }
                break;
            case "Tags":
                List<Map.Entry<String, Integer>> topTags = dashboardService.getTopTags();
                if (topTags.size() == 0) {
                    noTagMessageLabel.setVisible(true);
                    notEnoughRatingsMessageLabel.setVisible(false);
                } else {
                    noTagMessageLabel.setVisible(false);
                    for ( Map.Entry<String, Integer> entryTag : topTags) {
                        if (entryTag.getValue() > 0) {
                            dataList.add(new PieChart.Data(String.valueOf(entryTag.getKey()), entryTag.getValue()));
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
            double total = pieChart.getData().stream().mapToDouble(PieChart.Data::getPieValue).sum();
            pieChart.getData().forEach(pieData -> {
                String percentage = String.format("%.2f%%", ((pieData.getPieValue() / total) * 100));
                Tooltip tooltip = new Tooltip(percentage);
                Tooltip.install(pieData.getNode(), tooltip);
            });
            data.getNode().setOnMouseClicked(event -> {
                String filterTerm = data.getName();
                DashboardService.getInstance().setSelectedPieSliceSearch(category,filterTerm);
                swapPage("/fxml/DataListPage.fxml");
            });
        }
    }

}


