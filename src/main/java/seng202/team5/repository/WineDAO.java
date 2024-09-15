package seng202.team5.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;

/**
 * Wine Data Access Object class. This class implements DAOInterface and handles
 * CRUD operations for Wine objects.
 *
 * @author Caitlin Tam
 * @author Sean Reitsma
 * @author Martyn Gascoigne
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
                        + "FROM WINE, VINEYARD WHERE vineyard.id = wine.vineyard;";
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
                        rs.getString(("variety")),
                        rs.getString("colour"),
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
        String sql = "SELECT wine.id, wine.name, wine.description, wine.year, wine.rating, "
                + "wine.variety, wine.price, wine.colour, vineyard.name AS vineyardName, vineyard.region "
                + "FROM WINE, VINEYARD WHERE vineyard.id = wine.vineyard AND wine.id=?;";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Wine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("year"),
                        rs.getInt("rating"),
                        rs.getDouble("price"),
                        rs.getString("variety"),
                        rs.getString("colour"),
                        new Vineyard(rs.getString("vineyardName"), rs.getString("region")));
            }
        } catch (SQLException e) {
            log.error(e);
            return null;
        }
        return null;
    }


    /**
     * Gets the id of a wine from its name,
     * throws not found exception if wine does not exist.
     *
     * @param name the id of the object
     * @return the id of the wine object
     * @throws NotFoundException no user found with that username
     */
    public Wine getWineFromName(String name) throws NotFoundException {
        String sql = "SELECT wine.id, wine.name, wine.description, wine.year, wine.rating, "
                + "wine.variety, wine.price, wine.colour, vineyard.name AS vineyardName, vineyard.region "
                + "FROM WINE, VINEYARD WHERE vineyard.id = wine.vineyard AND wine.name=?;";
        try (Connection conn = databaseService.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Wine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("year"),
                        rs.getInt("rating"),
                        rs.getDouble("price"),
                        rs.getString("variety"),
                        rs.getString("colour"),
                        new Vineyard(rs.getString("vineyardName"), rs.getString("region")));
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
        throw new NotFoundException(String.format("No wine with name %s found", name));
    }


    /**
     * Add a single wine to the database.
     *
     * @param toAdd object of type Wine to add
     * @return the id of the added wine
     */
    @Override
    public int add(Wine toAdd) {
        if (!toAdd.isValidWine()) {
            return -1;
        }
        String sqlWine = "INSERT OR IGNORE INTO WINE(name, description, year, rating, "
                + "price, vineyard, variety, colour) values (?,?,?,?,?,?,?,?);";
        try (Connection conn = databaseService.connect();
                PreparedStatement psWine = conn.prepareStatement(sqlWine)) {
            int vineyardIndex = vineyardDAO.getIdFromName(toAdd.getVineyard().getName());
            if (vineyardIndex == 0) {
                toAdd.getVineyard().setId(vineyardDAO.add(toAdd.getVineyard()));
            } else {
                toAdd.getVineyard().setId(vineyardIndex);
                //log.info(toAdd.getVineyard().getName());
            }

            psWine.setString(1, toAdd.getName());
            psWine.setString(2, toAdd.getDescription());
            psWine.setInt(3, toAdd.getYear());
            psWine.setDouble(4, toAdd.getRating());
            psWine.setDouble(5, toAdd.getPrice());
            psWine.setInt(6, toAdd.getVineyard().getId());
            psWine.setString(7, toAdd.getWineVariety());
            psWine.setString(8, toAdd.getWineColour());

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


    /**
     * Add a batch of wines to the database.
     * The wines get lumped together and then pushed, making it more efficient
     * for larger batches of data, such as the initial data step.
     *
     * @param toAdd the list of wines to be added
     */
    public void batchAdd(List<Wine> toAdd) {
        String sql = "INSERT OR IGNORE INTO WINE (name, year, variety, rating, "
                + "price, colour, vineyard, description) values (?,?,?,?,?,?,?,?);";

        // Cache for vineyard names and IDs
        Map<String, Integer> vineyardCache = new HashMap<>();

        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Wine wine : toAdd) {
                if (wine.isValidWine()) {//checks if wine attributes are valid
                    ps.setString(1, wine.getName());
                    ps.setInt(2, wine.getYear());
                    ps.setString(3, wine.getWineVariety());
                    ps.setInt(4, wine.getRating());
                    ps.setDouble(5, wine.getPrice());
                    ps.setString(6, wine.getWineColour());

                    // We need to make this more efficient -
                    // I have implemented a hash map which hopefully increases the performance
                    // However we could further improve it by initialising this at the start
                    // of the app so that if any more wines are added they can just reference
                    // the hash map, rather than query the db.
                    Vineyard curVineyard = wine.getVineyard();
                    int vineyardIndex;
                    if (vineyardCache.containsKey(curVineyard.getName())) {
                        vineyardIndex = vineyardCache.get(curVineyard.getName());
                        wine.getVineyard().setId(vineyardIndex);
                    } else {
                        vineyardIndex = vineyardDAO.getIdFromName(wine.getVineyard().getName());
                        if (vineyardIndex == 0) {
                            wine.getVineyard().setId(vineyardDAO.add(wine.getVineyard()));
                        } else {
                            wine.getVineyard().setId(vineyardIndex);
                        }
                        vineyardCache.put(curVineyard.getName(), curVineyard.getId());
                    }

                    ps.setInt(7, wine.getVineyard().getId());
                    ps.setString(8, wine.getDescription());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }


    /**
     * Delete a wine from the database.
     *
     * @param id id of the wine to delete
     */
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


    /**
     * Update a wine in the database.
     *
     * @param toUpdate Object that needs to be updated
     *                 (this object must be able to identify itself and its previous self)
     */
    @Override
    public void update(Wine toUpdate) {
        String sql  = "UPDATE wine SET name=?, "
                + "year=?, "
                + "variety=?, "
                + "rating=?, "
                + "price=?, "
                + "colour=?, "
                + "vineyard=?, "
                + "description=? "
                + "WHERE id=?";
        try (Connection conn = databaseService.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toUpdate.getName());
            ps.setInt(2, toUpdate.getYear());
            ps.setString(3, toUpdate.getWineVariety());
            ps.setInt(4, toUpdate.getRating());
            ps.setDouble(5, toUpdate.getPrice());
            ps.setString(6, toUpdate.getWineColour());
            ps.setInt(7, toUpdate.getVineyard().getId());
            ps.setString(8, toUpdate.getDescription());
            ps.setInt(9, toUpdate.getId());
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    /**
     * deletes all wines from wine table
     * called before populating the database to get rid of left-over wines in database
     */
    public void truncateWines () {
        String sql = "Delete FROM WINE;";
        try (Connection conn = databaseService.connect();
        PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }
}
