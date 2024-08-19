package seng202.team5.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.models.Region;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.models.WineType;
import seng202.team5.models.WineVariety;

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
                Wine wine = new Wine(name, description, year, rating, price,
                        // TODO: Replace with a call to WineVarietyService
                        new WineVariety(varietyName, WineType.UNKNOWN),
                        // TODO: Replace with a call to RegionService
                        new Region(regionName, new ArrayList<>(), new ArrayList<>()),
                        // TODO: Replace with a call to VineyardService
                        new Vineyard(vineyardName));
                wines.add(wine);
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return wines;
    }

    public void addWine(Wine wine) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbUrl)) {
            PreparedStatement addWineStatement = conn.prepareStatement(
                    "insert into wine(name, description, year, rating, price, region_name,"
                            +
                            "vineyard_id, variety_name) values (?, ?, ?, ?, ?, ?, ?, ?)");
            addWineStatement.setString(1, wine.getName());
        } catch (SQLException e) {
            log.error(e);
        }
    }
}
