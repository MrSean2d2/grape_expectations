package seng202;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team5.models.Vineyard;
import seng202.team5.repository.VineyardDAO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Class to handle requesting location from Nominatim Geolocation API
 * @author Morgan English
 */
public class GeolocatorService {

    /**
     * Runs a query with the address given and finds the most applicable lat, lng co-ordinates
     */
    public void queryAddress(Vineyard vineyard) {
        String logMessage = String.format("Requesting geolocation from Nominatim for region: %s, New Zealand", vineyard.getRegion());
        System.out.println(logMessage);
        String region = vineyard.getRegion().replace(' ', '+');
        try {
            // Creating the http request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://nominatim.openstreetmap.org/search?q=" + region + ",+New+Zealand&format=json")
            ).build();
            // Getting the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Parsing the json response to get the latitude and longitude co-ordinates
            JSONParser parser = new JSONParser();
            JSONArray results = (JSONArray)  parser.parse(response.body());
            JSONObject bestResult = (JSONObject) results.get(0);
            double lat = Double.parseDouble((String) bestResult.get("lat"));
            double lon = Double.parseDouble((String) bestResult.get("lon"));
            vineyard.setLat(lat);
            vineyard.setLon(lon);
        } catch (IOException | ParseException e) {
            System.err.println(e);
        } catch (InterruptedException ie) {
            System.err.println(ie);
            Thread.currentThread().interrupt();
        }
    }
}
