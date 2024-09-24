package seng202.team5.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class DashboardPageController extends PageController {
    @FXML
    public TableView reviewedWinesTable;
    @FXML
    public PieChart pieChart;

    @FXML
    private void initialize() {

    }
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Pie Chart!");
        stage.setWidth(500);
        stage.setHeight(500);
    }

}
