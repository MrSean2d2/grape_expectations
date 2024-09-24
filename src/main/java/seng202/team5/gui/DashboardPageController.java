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
import seng202.team5.services.DashboardService;

public class DashboardPageController extends PageController {
    @FXML
    public TableView reviewedWinesTable;
    @FXML
    public PieChart pieChart;
    public ComboBox<String> piechartTypeComboBox;
    private DashboardService dashboardService;

    @FXML
    private void initialize() {
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
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
     // set title dynamically depending on selected pie chart type
        stage.setWidth(500);
        stage.setHeight(500);
    }

    public void onPiechartComboBoxClicked( ) {
        String selectedPieChart = piechartTypeComboBox.getValue();
      //  dashboardService.getPieChart

    }

    public void updatePieChartData(String category) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        switch (category) {
            case "Variety":
                pieChartData.addAll(
                    new PieChart.Data("Pinot Noir", 13),
                    new PieChart.Data("Chardonnay", 25),
                    new PieChart.Data("Pinot Gris", 10),
                    new PieChart.Data("Rose something or rather", 11),
                    new PieChart.Data("Blanco", 11)
                );
                break;
            case "Region":
                pieChartData.addAll(
                        new PieChart.Data("Canterbury", 13),
                        new PieChart.Data("Auckland", 25),
                        new PieChart.Data("Wellington", 10),
                        new PieChart.Data("Kepler 51-B", 22)
                );
                break;
            case "Year":
                pieChartData.addAll(
                        new PieChart.Data("2001", 13),
                        new PieChart.Data("2002", 25),
                        new PieChart.Data("1984", 10),
                        new PieChart.Data("201 BC", 22)
                );
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
