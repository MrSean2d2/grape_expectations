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

/**
 * Wine Data Access Object class. This class implements DAOInterface and handles
 * CRUD operations for Wine objects.
 *
 * @author Caitlin Tam
 * @author Sean Reitsma
 */
public class WineDAO implements DAOInterface<Wine> {
    private final DatabaseService databaseService;
    private static final Logger log = LogManager.getLogger(WineDAO.class);
    private final VineyardDAO vineyardDAO;

    /**
     * WineDAO constructor. Creates a WineDAO object and initialises it with
     * services needed for handling WineVariety and Region objects.
     *
     */
    public WineDAO(VineyardDAO vineyardDAO) {
        databaseService = DatabaseService.getInstance();
        this.vineyardDAO = vineyardDAO;

    }

    /**
     * Get all wine objects from the database and put them in a list.
     *
     * @return the list of all the wine objects in the database
     */
    @Override
    public List<Wine> getAll() {
        List<Wine> wines = new ArrayList<>();
        String sql =
                "SELECT wine.id, wine.name, wine.description, wine.year, wine.rating, "
                        + "wine.variety, wine.price, wine.colour, vineyard.name AS vineyardName, vineyard.region "
                        + "FROM WINE, VINEYARD WHERE vineyard.id = wine.vineyard ";
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
                        rs.getString("colour"),
                        rs.getString(("variety")),
                        new Vineyard(rs.getString("vineyardName"), rs.getString("Region"))
                ));

            }
            return wines;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }

    }

    /**
     * Get one Wine from the database by id primary key attribute.
     *
     * @param id id of object to get
     * @return the Wine object if found, null otherwise
     */
    @Override
    public Wine getOne(int id) {
        Wine wine = null;
        String sql = "SELECT wine.id, wine.name, wine.description, wine.year, wine.rating, "
                + "wine.variety, wine.price, wine.colour, vineyard.name AS vineyardName, vineyard.region "
                + "FROM WINE, VINEYARD WHERE vineyard.id = wine.vineyard AND wine.id=? ";
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
                            rs.getString("variety"),
                            rs.getString("colour"),
                            new Vineyard(rs.getString("vineyardName"), rs.getString("region"))
                    );
                }
                return wine;
            }
        } catch (SQLException e) {
            log.error(e);
            return null;
        }
    }

    /**
     * Add a wine to the database.
     *
     * @param toAdd object of type Wine to add
     * @return the id of the added wine
     */
    @Override
    public int add(Wine toAdd) {
        String sqlWine;
        /* sqlite autogenerated ROWID's start at 1, so any value less than 1
        indicates that the id has not yet been set.
         */
        if (toAdd.getId() < 1) {
            sqlWine = "INSERT OR IGNORE INTO WINE(name, description, year, rating, price, "
                    + "vineyard, variety, colour) values (?,?,?,?,?,?,?,?);";
        } else {
            sqlWine = "INSERT OR IGNORE INTO WINE(id, name, description, year, rating, "
                    + "price, vineyard, variety, colour) values (?,?,?,?,?,?,?,?,?);";
        }
        try (Connection conn = databaseService.connect();
                PreparedStatement psWine = conn.prepareStatement(sqlWine)) {
            int startIndex = 0;
            if (toAdd.getId() > 0) {
                psWine.setInt(1, toAdd.getId());
                startIndex = 1;
            }

            int vineyardIndex = vineyardDAO.getIdFromName(toAdd.getVineyard().getName());
            if (vineyardIndex == 0) {
                toAdd.getVineyard().setId(vineyardDAO.add(toAdd.getVineyard()));
            } else {
                toAdd.getVineyard().setId(vineyardIndex);
                //log.info(toAdd.getVineyard().getName());
            }

            psWine.setString(1 + startIndex, toAdd.getName());
            psWine.setString(2 + startIndex, toAdd.getDescription());
            psWine.setInt(3 + startIndex, toAdd.getYear());
            psWine.setDouble(4 + startIndex, toAdd.getRating());
            psWine.setDouble(5 + startIndex, toAdd.getPrice());
            psWine.setObject(6 + startIndex, toAdd.getVineyard().getId());
            psWine.setObject(7 + startIndex, toAdd.getWineColour());
            psWine.setObject(8 + startIndex, toAdd.getWineVariety());

            psWine.executeUpdate();
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

    public void batchAdd(List<Wine> toAdd) {
        String sql = "INSERT OR IGNORE INTO WINE (name, year, variety, rating, "
                + "price, colour, vineyard, description) values (?,?,?,?,?,?,?,?);";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Wine wine : toAdd) {
                ps.setString(1, wine.getName());
                ps.setInt(2, wine.getYear());
                ps.setString(3, wine.getWineVariety());
                ps.setInt(4, wine.getRating());
                ps.setDouble(5, wine.getPrice());
                ps.setString(6, wine.getWineColour());
                //System.out.println(wine.getVineyard().getName());

                int vineyardIndex = vineyardDAO.getIdFromName(wine.getVineyard().getName());
                if (vineyardIndex == 0) {
                    wine.getVineyard().setId(vineyardDAO.add(wine.getVineyard()));
                } else {
                    wine.getVineyard().setId(vineyardIndex);
                    //log.info(wine.getVineyard().getName());
                }

                ps.setInt(7, wine.getVineyard().getId());
                ps.setString(8, wine.getDescription());
                ps.addBatch();
            }
            ps.executeBatch();
            ResultSet rs = ps.getGeneratedKeys();
            while (rs.next()) {
                log.info(rs.getLong(1));
            }
            conn.commit();
        } catch (SQLException e) {
            log.error(e);
        }
    }

    @Override
    public void delete(int id) {
        vineyardDAO.delete(id);
        String sql = "DELETE FROM WINE WHERE id=?";
        try (Connection conn = databaseService.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    @Override
    public void update(Wine object) {
        throw new NotImplementedException();
    }
}
