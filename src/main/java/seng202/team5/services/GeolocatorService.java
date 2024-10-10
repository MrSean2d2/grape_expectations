package seng202.team5.services;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team5.models.Vineyard;

/**
 * Class to handle requesting location from Nominatim Geolocation API.
 *
 * @author Morgan English
 */
public class GeolocatorService {
    private static final Logger log = LogManager.getLogger(GeolocatorService.class);
    private final Map<String, double[]> coordsCache = new HashMap<>();

    private static GeolocatorService instance;

    /**
     * Returns the singleton GeolocatorService instance.
     *
     * @return instance of geolocator service class
     */
    public static GeolocatorService getInstance() {
        if (instance == null) {
            instance = new GeolocatorService();
        }
        return instance;
    }

    /**
     * Runs a query with the address given and finds the most applicable lat, lng co-ordinates.
     */
    public void queryAddress(Vineyard vineyard) {
        String region = vineyard.getRegion();
        if (coordsCache.containsKey(region)) {
            double[] coords = coordsCache.get(region);
            vineyard.setLat(coords[0]);
            vineyard.setLon(coords[1]);
            return;
        }
        String logMessage = String.format("Requesting geolocation from Nominatim for region: %s, "
                + "New Zealand", vineyard.getRegion());
        log.info(logMessage);
        String formattedRegion = vineyard.getRegion().replace(' ', '+');
        try (HttpClient client = HttpClient.newHttpClient()) {
            // Creating the http request
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://nominatim.openstreetmap.org/search?q=" + formattedRegion + ",+New+Zealand&format=json")
            ).build();
            // Getting the response
            HttpResponse<String> response = client.send(
                    request, HttpResponse.BodyHandlers.ofString());
            // Parsing the json response to get the latitude and longitude co-ordinates
            JSONParser parser = new JSONParser();
            JSONArray results = (JSONArray)  parser.parse(response.body());
            JSONObject bestResult = (JSONObject) results.getFirst();
            double lat = Double.parseDouble((String) bestResult.get("lat"));
            double lon = Double.parseDouble((String) bestResult.get("lon"));
            coordsCache.put(region, new double[]{lat, lon});
            vineyard.setLat(lat);
            vineyard.setLon(lon);
        } catch (IOException | ParseException e) {
            log.error("Problem retrieving API response", e);
        } catch (InterruptedException ie) {
            log.error("Interrupt received!", ie);
            Thread.currentThread().interrupt();
        }
    }
}
