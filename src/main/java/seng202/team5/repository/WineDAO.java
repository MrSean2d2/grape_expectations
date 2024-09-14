package seng202.team5.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.bean.processor.ConvertEmptyOrBlankStringsToNull;
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
            psWine.setInt(6 + startIndex, toAdd.getVineyard().getId());
            psWine.setString(7 + startIndex, toAdd.getWineVariety());
            psWine.setString(8 + startIndex, toAdd.getWineColour());

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
     * Adds a batch of wine objects.
     * @param toAdd list of wines to add to database
     */
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

    /**
     * Deletes wine entry.
     * @param id id of wine to delete
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
     * Updates wine entry
     * @param object Object that needs to be updated (this object must be able to identify itself and its previous self)
     */
    @Override
    public void update(Wine object) {
        throw new NotImplementedException();
    }

    /**
     * Builds sql query for search and filter.
     *
     * @param search term to search for
     * @param year to filter
     * @return sql string
     */
    public String queryBuilder(String search, String variety, String region, String year, double minPrice, double maxPrice, double minRating, double maxScore, boolean favourite){
        String sql = "SELECT DISTINCT wine.id, wine.name, wine.description, wine.year, wine.rating, "
                + "wine.variety, wine.price, wine.colour, vineyard.name AS vineyardName, vineyard.region "
                + "FROM WINE, VINEYARD WHERE vineyard.id = wine.vineyard";
        if (search != null) {
            sql +=  " AND (wine.name LIKE ? OR wine.description LIKE ?) ";
        }
        if(variety != "0") {
            sql += " AND wine.variety = "+variety;
        }
        if (region != "0") {
            sql += " AND vineyard.region = "+region;
        }
        if (year != "0") {
            sql += " AND wine.year = "+ year;
        }
        //TODO: implement minPrice sql query
        if (maxPrice != 800.0) {
            sql+= " AND wine.price <= "+ String.valueOf(maxPrice);
        }
        //TODO: implement minRating sql query
        if(maxScore != 100.0) {
            sql += " AND wine.rating <= " + String.valueOf(maxScore);
        }
        //TODO: implement favourite toggle -- wait for drinks table

        sql += ";";
        return sql;
    }

    /**
     * Searches for wines by name or description and
     * returns list of wines matching search.
     *
     * @param search name or description of wine
     * @return list of wines matching search
     */
    public List<Wine> executeSearchFilter(String querySql, String search) {
        List<Wine> searchedWines = new ArrayList<>();

        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(querySql)) {
            String searchPattern = "%" + search + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                searchedWines.add(new Wine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("year"),
                        rs.getInt("rating"),
                        rs.getInt("price"),
                        rs.getString("variety"),
                        rs.getString("colour"),
                        new Vineyard(rs.getString("vineyardName"), rs.getString("Region"))
                ));
            }
            return searchedWines;
        } catch (SQLException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }
}
