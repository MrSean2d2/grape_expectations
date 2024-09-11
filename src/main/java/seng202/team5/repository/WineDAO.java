package seng202.team5.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.services.RegionService;
import seng202.team5.services.WineVarietyService;

public class WineDAO implements DAOInterface<Wine> {
    private final DatabaseService databaseService;
    private final WineVarietyService wineVarietyService;
    private final RegionService regionService;
    private static final Logger log = LogManager.getLogger(WineDAO.class);

    public WineDAO(WineVarietyService wineVarietyService, RegionService regionService) {
        this.regionService = regionService;
        databaseService = DatabaseService.getInstance();
        this.wineVarietyService = wineVarietyService;

    }

    @Override
    public List<Wine> getAll() {
        List<Wine> wines = new ArrayList<>();
        String sql =
                "SELECT wine.id, wine.name, wine.description, wine.year, wine.rating, wine.variety, wine.price, vineyard.name, vineyard.region FROM WINE, VINEYARD WHERE vineyard.name = wine.vineyard ";
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
                        // TODO: get colour
                        wineVarietyService.varietyFromString(rs.getString(("variety"))),
                        regionService.getRegion(rs.getString("vineyard.region")),
                        new Vineyard(rs.getString("vineyard.name"))
                ));

            }
            return wines;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }

    }

    @Override
    public Wine getOne(int id) {
        Wine wine = null;
        String sql = "SELECT wine.id, wine.name, wine.description, wine.year, wine.rating, wine.variety, wine.price, vineyard.name, vineyard.region FROM WINE, VINEYARD WHERE (id=?, wine.vineyard=wine.name)";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    wine = new Wine(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("year"),
                            rs.getInt("rating"),
                            rs.getDouble("price"),
                            wineVarietyService.varietyFromString(rs.getString("variety")),
                            regionService.getRegion(rs.getString("vineyard.region")),
                            new Vineyard(rs.getString("vineyard.name"))
                    );
                }
                return wine;
            }
        } catch (SQLException e) {
            log.error(e);
            return null;
        }
    }

    public Wine getById(int id) {
        throw new NotImplementedException();
    }

    @Override
    public int add(Wine toAdd) {
        String sql = "INSERT INTO WINE(id, name, description, year, rating, price, vineyard, variety) values (?,?,?,?,?,?,?,?);";
        try (Connection conn = databaseService.connect();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, toAdd.getId());
            ps.setString(2, toAdd.getName());
            ps.setString(3, toAdd.getDescription());
            ps.setInt(4, toAdd.getYear());
            ps.setDouble(5,toAdd.getRating());
            ps.setDouble(4, toAdd.getPrice());
            ps.setObject(5, toAdd.getVineyard());
            ps.setObject(6, toAdd.getWineVariety());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int insertID = -1;
            if (rs.next()) {
                insertID = rs.getInt(1);
            }
            return insertID;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return -1;
        }

    }

    @Override
    public void delete(int id) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Wine object) {
        throw new NotImplementedException();
    }
}
