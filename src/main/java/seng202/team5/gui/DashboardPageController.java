package seng202.team5.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import seng202.team5.models.Review;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.DashboardService;
import seng202.team5.services.UserService;

import java.util.*;

public class DashboardPageController extends PageController {

    @FXML
    private TableView reviewedWinesTable;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label topVarietyLabel;

    @FXML
    private Label topRegionLabel;

    @FXML
    private Label topYearLabel;

    public ComboBox<String> piechartTypeComboBox;
    private DashboardService dashboardService;
    private VineyardDAO vineyardDAO;
    private WineDAO wineDAO;
    private ReviewDAO reviewDAO;

    // Setup default hash maps
    private Map<String, Integer> varietyMap;
    private Map<String, Integer> regionMap;
    private Map<Integer, Integer> yearMap;

    private List<Map.Entry<String, Integer>> topVariety;
    private List<Map.Entry<String, Integer>> topRegion;
    private List<Map.Entry<Integer, Integer>> topYear;

    @FXML
    private void initialize() {
        // Setup default hash maps
        varietyMap = new HashMap<String, Integer>();
        regionMap = new HashMap<String, Integer>();
        yearMap = new HashMap<Integer, Integer>();

        // fetch user reviews
        reviewDAO = new ReviewDAO();
        vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);

        List<Review> userReviews = reviewDAO.getFromUser(UserService.getInstance().getCurrentUser().getId());

        // Create a hash map for each property
        for(Review review : userReviews) {
            Wine wine = wineDAO.getOne(review.getWineId());
            Vineyard vineyard = vineyardDAO.getOne(wine.getVineyard().getId());

            // Add to maps
            varietyMap.merge(wine.getWineVariety(), review.getRating(), Integer::sum);
            regionMap.merge(vineyard.getRegion(), review.getRating(), Integer::sum);
            yearMap.merge(wine.getYear(), review.getRating(), Integer::sum);
        }

        // get max
        topVariety = sortHashMap(varietyMap);
        topRegion = sortHashMap(regionMap);
        topYear = sortHashMap(yearMap);

        if(topVariety != null) {
            topVarietyLabel.setText(topVariety.getFirst().getKey());
        }

        if(topRegion != null) {
            topRegionLabel.setText(topRegion.getFirst().getKey());
        }

        if(topYear != null) {
            topYearLabel.setText(String.valueOf(topYear.getFirst().getKey()));
        }

        // Update the combo box and pie chart

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

    /**
     * Find the max value of a hash map.
     *
     * @param inputMap the map to search
     * @return the maximum entry if found, null otherwise.
     */
    public <K, V extends Comparable<V>> List<Map.Entry<K, V>> sortHashMap(Map<K, V> inputMap) {
        List<Map.Entry<K, V>> list = new ArrayList<>(inputMap.entrySet());

        list.sort(Map.Entry.<K, V>comparingByValue().reversed());
        return list;
    }

    public void onPiechartComboBoxClicked( ) {
        String selectedPieChart = piechartTypeComboBox.getValue();
      //  dashboardService.getPieChart
    }

    public void updatePieChartData(String category) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        List<PieChart.Data> dataList = new ArrayList<>();

        switch (category) {
            case "Variety":
                for(int i = 0; i < Math.min(5, topVariety.size()); i++) {
                    dataList.add(new PieChart.Data(topVariety.get(i).getKey(), topVariety.get(i).getValue()));
                }

                pieChartData.addAll(dataList);
                break;
            case "Region":
                for(int i = 0; i < Math.min(5, topRegion.size()); i++) {
                    dataList.add(new PieChart.Data(topRegion.get(i).getKey(), topRegion.get(i).getValue()));
                }

                pieChartData.addAll(dataList);
                break;
            case "Year":
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
