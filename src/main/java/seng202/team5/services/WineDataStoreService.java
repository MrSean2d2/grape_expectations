package seng202.team5.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle data storage for wines, regions, vineyards, and varieties.
 *
 * @author Sean Reitsma
 */
public class WineDataStoreService {
    private static final Logger log = LogManager.getLogger(WineDataStoreService.class);
    String dbUrl;

    public WineDataStoreService(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * Return the list of all wine records in the database.
     *
     * @return the list of wine records
     */
    public List<Wine> getWines() {
        List<Wine> wines = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbUrl)) {
            PreparedStatement statement = conn.prepareStatement("select * from wine ");
            PreparedStatement vineyardStatement = conn.prepareStatement(
                    "select name, region_name from vineyard where id = ?");
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                String name = results.getString("NAME");
                String description = results.getString("DESCRIPTION");
                int year = results.getInt("YEAR");
                int rating = results.getInt("RATING");
                double price = results.getDouble("PRICE");
                String regionName = results.getString("REGION_NAME");
                int vineyardId = results.getInt("VINEYARD_ID");
                vineyardStatement.setInt(1, vineyardId);
                ResultSet vineyardResult = vineyardStatement.executeQuery();
                String vineyardName = vineyardResult.getString("NAME");
                String varietyName = results.getString("VARIETY_NAME");
                Wine wine = new Wine(name, description, year, rating, price, false,
                        new WineVariety(varietyName, WineType.UNKNOWN),
                        new Region(regionName, new ArrayList<>(), new ArrayList<>()),
                        new Vineyard(vineyardName));
                wines.add(wine);
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return wines;
    }
}
