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

/**
 * Wine Data Access Object class. This class implements DAOInterface and handles
 * CRUD operations for Wine objects.
 *
 * @author Caitlin Tam
 * @author Sean Reitsma
 */
public class WineDAO implements DAOInterface<Wine> {
    private final DatabaseService databaseService;
    private final WineVarietyService wineVarietyService;
    private final RegionService regionService;
    private static final Logger log = LogManager.getLogger(WineDAO.class);

    /**
     * WineDAO constructor. Creates a WineDAO object and initialises it with
     * services needed for handling WineVariety and Region objects.
     *
     * @param wineVarietyService the instance of {@link WineVarietyService}
     *                           which manages currently active WineVariety's
     * @param regionService the instance of {@link RegionService}
     *                      which manages currently active Region's
     */
    public WineDAO(WineVarietyService wineVarietyService, RegionService regionService) {
        this.regionService = regionService;
        databaseService = DatabaseService.getInstance();
        this.wineVarietyService = wineVarietyService;

    }

    @Override
    public List<Wine> getAll() {
        List<Wine> wines = new ArrayList<>();
        String sql =
                "SELECT wine.id, wine.name, wine.description, wine.year, wine.rating, "
                        + "wine.variety, wine.price, vineyard.name, vineyard.region "
                        + "FROM WINE, VINEYARD WHERE vineyard.name = wine.vineyard ";
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
        String sql = "SELECT wine.id, wine.name, wine.description, wine.year, wine.rating,"
                + " wine.variety, wine.price, vineyard.name AS vineyardName, vineyard.region "
                + "FROM WINE JOIN VINEYARD ON WINE.vineyard = VINEYARD.name WHERE wine.id=?";
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
                            regionService.getRegion(rs.getString("region")),
                            new Vineyard(rs.getString("vineyardName"))
                    );
                }
                return wine;
            }
        } catch (SQLException e) {
            log.error(e);
            return null;
        }
    }

    @Override
    public int add(Wine toAdd) {
        String sqlRegion = "INSERT INTO VINEYARD(name, region) values (?,?);";
        String sqlWine = "INSERT INTO WINE(id, name, description, year, rating, price, vineyard, variety) values (?,?,?,?,?,?,?,?);";
        try (Connection conn = databaseService.connect();
                PreparedStatement psRegion = conn.prepareStatement(sqlRegion);
                PreparedStatement psWine = conn.prepareStatement(sqlWine);){
            psWine.setInt(1, toAdd.getId());
            psWine.setString(2, toAdd.getName());
            psWine.setString(3, toAdd.getDescription());
            psWine.setInt(4, toAdd.getYear());
            psWine.setDouble(5, toAdd.getRating());
            psWine.setDouble(6, toAdd.getPrice());
            psWine.setObject(7, toAdd.getVineyard().getName());
            psWine.setObject(8, toAdd.getWineVariety().getName());

            psRegion.setString(1, toAdd.getVineyard().getName());
            psRegion.setString(2, toAdd.getRegion().getName());

            psWine.executeUpdate();
            psRegion.executeUpdate();
            ResultSet rs = psWine.getGeneratedKeys();

            int insertID = -1;
            if (rs.next()) {
                insertID = rs.getInt(1);
            }
            return insertID;
        } catch (SQLException sqlException) {
            log.error("Error adding wine: ", sqlException);
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
