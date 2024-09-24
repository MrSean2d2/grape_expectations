package seng202.team5.gui;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.models.Vineyard;
import seng202.team5.repository.VineyardDAO;

import java.util.List;

/**
 * Controller class for MapPage.fxml
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

    /**
     * Initialize the map page
     * @author Martyn Gascoigne
     *
     */
    @FXML
    public void initialize() {
        vineyardDAO = new VineyardDAO();
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getClassLoader().getResource("html/leaflet_osm_map.html").toExternalForm());
        WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceId) ->
                log.info(String.format("Map WebView console log line: %d, message : %s", lineNumber, message)));

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");

                        javaScriptConnector.call("initMap");
                        addVineyardsToMap();

                        System.out.println("Loaded map");
                    }
                });
    }

    /**
     * Add vineyards to map
     * @author Martyn Gascoigne
     */
    @FXML
    private void addVineyardsToMap() {
        if (javaScriptConnector != null) {
            List<Vineyard> vineyards = vineyardDAO.getAll(); // Retrieve all vineyards
            for (Vineyard vineyard : vineyards) {
                // Debug output to verify lat/lng values
                log.info(String.format("Adding vineyard: %s with coordinates (%f, %f)", vineyard.getName(), vineyard.getLat(), vineyard.getLon()));

                // Check if the latitude and longitude are valid numbers
                if (vineyard.getLat() != 0.0 && vineyard.getLon() != 0.0) {
                    javaScriptConnector.call("addMarker", vineyard.getId(), vineyard.getName(), vineyard.getLat(), vineyard.getLon());
                } else {
                    log.error(String.format("Invalid coordinates for vineyard: %s", vineyard.getName()));
                }
            }
        }
    }

    /**
     * Get a (temporary) list of vineyards
     * @author Martyn Gascoigne
     */
    private List<Vineyard> getVineyards() {
        return List.of(
                new Vineyard(1, "Vineyard 1", -44.0, 171.1),
                new Vineyard(2, "Vineyard 2", -44.1, 171.2),
                new Vineyard(3, "Vineyard 3", -44.2, 171.3)
        );
    }
}