package seng202.team5.gui;

import javafx.fxml.FXML;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Controller class for MapPage.fxml
 * @author Martyn Gascoigne
 */
public class MapPageController extends PageController {
    
    @FXML
    private GridPane viewMap;

    /**
     * Initialize the map page
     * 
     */
    @FXML
    private void initialize() {
        MapPoint centerPoint = new MapPoint(-41, 174.886);

        MapView sampleMap = new MapView();
        sampleMap.setCenter(centerPoint);
        sampleMap.setZoom(6);

        viewMap.getChildren().add(sampleMap);
    }
}
