package seng202.team5.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seng202.team5.models.*;
import seng202.team5.repository.*;
import seng202.team5.services.DashboardService;
import seng202.team5.services.UserService;

import java.util.*;

import static seng202.team5.services.ColourLookupService.getTagLabelColour;

public class DashboardPageController extends PageController {
    @FXML
    public Label notEnoughRatingsMessageLabel;
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

    public ComboBox<String> piechartTypeComboBox;
    private DashboardService dashboardService;
    private TagsDAO tagsDAO;
    private int userID;

    /**
     * Initalises the dashboard page
     * Fetches user reviews, processes data and updates the pie chart
     */
    @FXML
    private void initialize() {
        userID = UserService.getInstance().getCurrentUser().getId();


        dashboardService = new DashboardService(userID,new VineyardDAO(), new WineDAO(new VineyardDAO()), new ReviewDAO());

        if (dashboardService.getUserReviews().size()< 3) {
            pieChart.setVisible(false);
            notEnoughRatingsMessageLabel.setVisible(true);
            piechartTypeComboBox.setDisable(true);
        } else {
            pieChart.setVisible(true);
            notEnoughRatingsMessageLabel.setVisible(false);
            piechartTypeComboBox.setDisable(false);
        }
        updateTopLabels();

        updatePieChartComboBox();


        piechartTypeComboBox.setTooltip(new Tooltip("Select Type Of Pie Chart"));


        // Add tables
        tagsDAO = new TagsDAO();
        AssignedTagsDAO assignedTagsDAO = new AssignedTagsDAO();
        List<Tag> tags = tagsDAO.getFromUser(userID);

        // Add default reviewed options
        createNewTagList("My Reviewed Wines", dashboardService.getUserReviews().size(), -1);

        // Add all of the user tag options
        for (Tag tag : tags) {
            /*
            Get a list of the assigned tags
            This could be used to get the list of wines that have this tag
            as the assignedtag would have a wineID also
             */
            List<AssignedTag> numWines = assignedTagsDAO.getTagsFromUser(
                    userID,
                    tag.getTagId());

            createNewTagList(tag.getName(), numWines.size(), tag.getColour());
        }
    }

    private void updatePieChartComboBox() {
        ObservableList<String> piechartTypeOptions = FXCollections.observableArrayList();
        piechartTypeOptions.addAll("Variety", "Region", "Year");
        piechartTypeComboBox.setItems(piechartTypeOptions);

        // Update the title and data
        piechartTypeComboBox.valueProperty().addListener((observable, oldOption, newOption) -> {
            updatePieChartData(newOption);
        });

        // Default value (Variety)
        piechartTypeComboBox.getSelectionModel().select(0);
    }

    private void updateTopLabels() {
        // get max
        List<Map.Entry<String, Integer>> topVariety = dashboardService.getTopVariety();
        List<Map.Entry<String, Integer>> topRegion = dashboardService.getTopRegion();
        List<Map.Entry<Integer,Integer>> topYear = dashboardService.getTopYear();

        if(!topVariety.isEmpty()) {
            topVarietyLabel.setText(topVariety.getFirst().getKey());
        }

        if(!topRegion.isEmpty()) {
            topRegionLabel.setText(topRegion.getFirst().getKey());
        }

        if(!topYear.isEmpty()) {
            topYearLabel.setText(String.valueOf(topYear.getFirst().getKey()));
        }
    }

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
                System.out.println(name);
                headerController.loadDataListPageWithTag(name); // Pass tag name or ID to DataListPage
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        userListPane.getChildren().add(tagContainer);
    }



    /**
     * Updates the pie chart data based on selected category
     *
     * @param category selected for pie chart
     */

    public void updatePieChartData(String category) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        List<PieChart.Data> dataList = new ArrayList<>();

        switch (category) {
            case "Variety":
                List<Map.Entry<String, Integer>> topVariety = dashboardService.getTopVariety();
                for(int i = 0; i < Math.min(5, topVariety.size()); i++) {
                    dataList.add(new PieChart.Data(topVariety.get(i).getKey(), topVariety.get(i).getValue()));
                }

                pieChartData.addAll(dataList);
                break;
            case "Region":
                List<Map.Entry<String, Integer>> topRegion = dashboardService.getTopRegion();
                for(int i = 0; i < Math.min(5, topRegion.size()); i++) {
                    dataList.add(new PieChart.Data(topRegion.get(i).getKey(), topRegion.get(i).getValue()));
                }

                pieChartData.addAll(dataList);
                break;
            case "Year":
                List<Map.Entry<Integer, Integer>> topYear = dashboardService.getTopYear();
                for(int i = 0; i < Math.min(5, topYear.size()); i++) {
                    dataList.add(new PieChart.Data(String.valueOf(topYear.get(i).getKey()), topYear.get(i).getValue()));
                }

                pieChartData.addAll(dataList);
                break;
            default:
                // Don't add any data!!!
                break;
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle("Favourite " + category);
        pieChart.setClockwise(true);
        pieChart.setStartAngle(180);
        pieChart.setLabelsVisible(true);
    }

}
