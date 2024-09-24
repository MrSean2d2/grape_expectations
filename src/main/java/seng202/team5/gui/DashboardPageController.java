package seng202.team5.gui;

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
    public Label piechartTitle;
    public ComboBox<String> piechartTypeComboBox;
    private DashboardService dashboardService;

    @FXML
    private void initialize() {
        ObservableList<String> piechartTypeOptions = FXCollections.observableArrayList();
        piechartTypeOptions.addAll("Variety", "Region", "Year");
        piechartTypeComboBox.setItems(piechartTypeOptions);
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

    public void createPieChart() {

    }



}
