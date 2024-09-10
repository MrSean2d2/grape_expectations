package seng202.team5.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.models.Wine;
import seng202.team5.models.WineVariety;
import seng202.team5.services.WineVarietyService;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WineDAO implements DaoInterface{
    private final DatabaseService databaseService;
    private final WineVarietyService wineVarietyService;
    private static final Logger log = LogManager.getLogger(WineDAO.class);

    public WineDAO(WineVarietyService wineVarietyService) {
        databaseService = DatabaseService.getInstance();
        this.wineVarietyService = wineVarietyService;

    }

    @Override
    public List<Wine> getAll() {
        List<Wine> wines = new ArrayList<>();
        String sql = "SELECT wine.id, wine.name, wine.description, wine.year, wine.rating, wine.variety, wine.price, vineyard.name, vineyard.region FROM WINE, VINEYARD WHERE vineyard.name = wine.vineyard ";
        try (Connection conn = databaseService.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                wines.add(new Wine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("year"),
                        rs.getInt("rating"),
                        rs.getDouble("price"),
                        wineVarietyService.varietyFromString(rs.getString(("variety"))),
                        (null), // TODO: get colour, get region;
                        (null) // TODO: get region;

                ));

            }
            return wines;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }

    }

    @Override
    public Object getById(int id) {
        return null;
    }

    @Override
    public int add(Object object) {
        return 0;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void update(Object object) {

    }
}
