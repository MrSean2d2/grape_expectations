package seng202.team5.gui;

import javafx.fxml.FXML;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class MapPageController {

    @FXML
    private GridPane viewMap;
    @FXML
    public void initialize() {
        MapPoint centerPoint = new MapPoint(-41, 174.886);

        MapView sampleMap = new MapView();
        sampleMap.setCenter(centerPoint);
        sampleMap.setZoom(6);

        viewMap.getChildren().add(sampleMap);
    }
}
