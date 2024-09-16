package seng202.team5.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import seng202.team5.models.Wine;
import seng202.team5.services.UserService;
import seng202.team5.services.WineService;
import javafx.fxml.FXMLLoader;

/**
 * Controller for the Data List Page.
 */
public class DataListPageController extends PageController {

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
    private TableColumn<Wine, Boolean> favouriteColumn;

    /**
     * Initializes the data List by calling {@link seng202.team5.services.WineService}
     * to populate the list of wines.
     */
    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        //favouriteColumn.setCellValueFactory(new PropertyValueFactory<>("favourite"));

        ObservableList<Wine> wines = FXCollections.observableArrayList(WineService.getInstance()
                .getWineList());

        // Add data to TableView
        wineTable.setItems(wines);
        System.out.println(wines.size());

        wineTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(wineTable.getSelectionModel().getSelectedItem().getName());
                if (mouseEvent.getClickCount() == 2) {
                    try {
                        Wine selectedWine = wineTable.getSelectionModel().getSelectedItem();
                        if (selectedWine != null) {  // && UserService.getInstance().getCurrentUser() != null
                            WineService.getInstance().setSelectedWine(selectedWine);
                            openDetailedViewPage(selectedWine);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        });
    }
    /**
     * Opens detailed wine view page for the wine that was double-clicked.
     *
     * @param selectedWine the currently selected wine
     */
    private void openDetailedViewPage(Wine selectedWine) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DetailedViewPage.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Wine Details");
            Scene scene = new Scene(root);

            String styleSheetURL = "/fxml/style.css";
            scene.getStylesheets().add(styleSheetURL);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

}
