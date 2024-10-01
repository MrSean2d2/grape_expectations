package seng202.team5.gui;

import com.sun.javafx.webkit.WebConsoleListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.models.Vineyard;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.services.VineyardService;
import seng202.team5.services.WineService;

/**
 * Controller class for MapPage.fxml.
 *
 * @author Martyn Gascoigne
 */
public class MapPageController extends PageController {

    private static final Logger log = LogManager.getLogger(MapPageController.class);

    @FXML
    private WebView webView;
    private WebEngine webEngine;
    private JSObject javaScriptConnector;
    private boolean markersDisplayed = true;
    private VineyardDAO vineyardDAO;
    @FXML
    private TableView<Vineyard> vineyardTable;
    @FXML
    private TableColumn<Vineyard, String> vineyardColumn;

    /**
     * Initialize the map page.
     *
     * @author Martyn Gascoigne
     *
     */
    @FXML
    public void initialize() {
        vineyardTable.setPlaceholder(new Label("Select a region to view vineyards"));
        vineyardColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        vineyardDAO = new VineyardDAO();

        WineService.getInstance().getWineList();

        vineyardTable.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                Vineyard vineyard = vineyardTable.getSelectionModel().getSelectedItem();
                if (vineyard != null) {
                    VineyardService.getInstance().setSelectedVineyard(vineyard);
                    swapPage("/fxml/DataListPage.fxml");
                }
            }
        });

        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getClassLoader().getResource(
                "html/leaflet_osm_map.html").toExternalForm());
        //WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceId) ->
        //log.info(String.format(
        //"Map WebView console log line: %d, message : %s", lineNumber, message)));

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaConnector", this);
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");

                        javaScriptConnector.call("initMap");
                        addVineyardsToMap();

                        System.out.println("Loaded map");
                    }
                });
    }

    /**
     * Sets items in vineyard table view to matching vineyards in selected region.
     *
     * @param title region of selected marker
     */
    public void onMarkerClicked(String title) {
        List<Vineyard> vineyards = vineyardDAO.getAll();
        ObservableList<Vineyard> matchingVineyards = FXCollections.observableArrayList();
        for (Vineyard vineyard : vineyards) {
            if (vineyard.getRegion().equals(title)) {
                matchingVineyards.add(vineyard);
            }
        }
        vineyardTable.setItems(matchingVineyards);
    }

    /**
     * Add vineyards to map.
     *
     * @author Martyn Gascoigne
     */
    @FXML
    private void addVineyardsToMap() {
        if (javaScriptConnector != null) {
            List<Vineyard> vineyards = vineyardDAO.getAll(); // Retrieve all vineyards
            Set<String> regions = new HashSet<>();
            for (Vineyard vineyard : vineyards) {
                // Debug output to verify lat/lng values
                log.info(String.format("Adding vineyard: %s with coordinates (%f, %f)",
                        vineyard.getName(), vineyard.getLat(), vineyard.getLon()));

                // Check if the latitude and longitude are valid numbers
                if (vineyard.getLat() != 0.0 && vineyard.getLon() != 0.0) {
                    if (!regions.contains(vineyard.getRegion())) {
                        javaScriptConnector.call("addMarker", vineyard.getId(),
                                vineyard.getRegion(), vineyard.getLat(), vineyard.getLon());
                        regions.add(vineyard.getRegion());
                    } else {
                        log.info(String.format("Skipping vineyard: %s, "
                                + "region %s already has a marker", vineyard.getName(),
                                vineyard.getRegion()));
                    }
                } else {
                    log.error(String.format("Invalid coordinates for vineyard: %s",
                            vineyard.getName()));
                }
            }
        }
    }


}