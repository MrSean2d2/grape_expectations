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
import java.util.Objects;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.WineService;

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
    private final String defaultStringQuery = "0";
    private final double defaultMaxPrice = 800.0;
    private final double defaultMinPrice = 0.0;
    private final int defaultMinRating = 0;

    /**
     * WineDAO constructor. Creates a WineDAO object and initialises it with
     * services needed for handling WineVariety and Region objects.
     *
     * @param vineyardDAO the VineyardDAO to use
     */
    public WineDAO(VineyardDAO vineyardDAO) {
        databaseService = DatabaseService.getInstance();
        this.vineyardDAO = vineyardDAO;

    }

    /**
     * Gets vineyardDAO.
     *
     * @return the vineyardDAO
     */
    public VineyardDAO getVineyardDAO() {
        return vineyardDAO;
    }

    /**
     * Get all wine objects from the database and put them in a list.
     *
     * @return the list of all the wine objects in the database
     */
    @Override
    public List<Wine> getAll() {
        List<Wine> wines = new ArrayList<>();
        String sql = "SELECT wine.id, wine.name, wine.description, wine.year, wine.rating,"
                        + "wine.variety, wine.price, wine.colour, vineyard.name "
                        + "AS vineyardName, vineyard.region FROM WINE, VINEYARD "
                        + "WHERE vineyard.id = wine.vineyard;";
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
                        new Vineyard(rs.getInt("id"),
                                rs.getString("vineyardName"),
                                rs.getString("Region"))
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
        String sql = "SELECT wine.id, wine.name, wine.description, wine.year, wine.rating,"
                        + "wine.variety, wine.price, wine.colour, vineyard.name "
                        + "AS vineyardName, vineyard.region FROM WINE, VINEYARD "
                        + "WHERE vineyard.id = wine.vineyard AND wine.id = ?;";
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
                        new Vineyard(rs.getInt("id"),
                                rs.getString("vineyardName"),
                                rs.getString("region")));
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
        String sql = "SELECT wine.id, wine.name, wine.description, wine.year, wine.rating,"
                + "wine.variety, wine.price, wine.colour, vineyard.name "
                + "AS vineyardName, vineyard.region FROM WINE, VINEYARD "
                + "WHERE vineyard.id = wine.vineyard AND wine.name = ?;";
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
                        new Vineyard(rs.getInt("id"),
                                rs.getString("vineyardName"),
                                rs.getString("region")));
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
        if (!WineService.getInstance().isValidWine(toAdd)) {
            return -1;
        } else {
            String sqlWine = "INSERT OR IGNORE INTO WINE(name, description, year, rating, "
                    + "price, vineyard, variety, colour) values (?,?,?,?,?,?,?,?);";
            try (Connection conn = databaseService.connect();
                     PreparedStatement psWine = conn.prepareStatement(sqlWine)) {
                int vineyardIndex = vineyardDAO.getIdFromNameRegion(toAdd.getVineyard().getName(),
                        toAdd.getVineyard().getRegion());
                if (vineyardIndex == 0) {
                    toAdd.getVineyard().setId(vineyardDAO.add(toAdd.getVineyard()));
                } else {
                    toAdd.getVineyard().setId(vineyardIndex);
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

                int insertId = -1;
                if (rs.next()) {
                    insertId = rs.getInt(1);
                }
                return insertId;
            } catch (SQLException sqlException) {
                log.error("Error adding wine: ", sqlException);
                return -1;
            }
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
        Map<Pair<String, String>, Integer> vineyardCache = new HashMap<>();

        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Wine wine : toAdd) {
                //checks if wine attributes are valid
                if (WineService.getInstance().isValidWine(wine)) {
                    ps.setString(1, wine.getName());
                    ps.setInt(2, wine.getYear());
                    ps.setString(3, wine.getWineVariety());
                    ps.setInt(4, wine.getRating());
                    ps.setDouble(5, wine.getPrice());
                    ps.setString(6, wine.getWineColour());

                    Vineyard curVineyard = wine.getVineyard();
                    Pair<String, String> vineyardSecondaryKey = Pair.of(curVineyard.getName(),
                            curVineyard.getRegion());
                    int vineyardIndex;
                    if (vineyardCache.containsKey(vineyardSecondaryKey)) {
                        vineyardIndex = vineyardCache.get(vineyardSecondaryKey);
                        wine.getVineyard().setId(vineyardIndex);
                    } else {
                        vineyardIndex = vineyardDAO.getIdFromNameRegion(
                                wine.getVineyard().getName(),
                                wine.getVineyard().getRegion());
                        if (vineyardIndex == 0) {
                            wine.getVineyard().setId(vineyardDAO.add(wine.getVineyard()));
                        } else {
                            wine.getVineyard().setId(vineyardIndex);
                        }
                        vineyardCache.put(vineyardSecondaryKey, curVineyard.getId());
                    }

                    ps.setInt(7, wine.getVineyard().getId());
                    ps.setString(8, wine.getDescription());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
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
        String sql = "DELETE FROM WINE WHERE id=?";
        String sqlReview = "DELETE FROM review WHERE wineid=?";
        String sqlAssignedTag = "DELETE FROM assigned_tags WHERE wineid=?";
        try (Connection conn = databaseService.connect();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 PreparedStatement rps = conn.prepareStatement(sqlReview);
                 PreparedStatement atps = conn.prepareStatement(sqlAssignedTag)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            rps.setInt(1, id);
            rps.executeUpdate();

            atps.setInt(1, id);
            atps.executeUpdate();

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
        if (WineService.getInstance().isValidWine(toUpdate)) {
            String sql = "UPDATE wine SET name=?, "
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
    }

    /**
     * Deletes all wines from wine table.
     * Called before populating the database to get rid of left-over wines in database
     */
    public void truncateWines() {
        String sql = "Delete FROM wine;";
        String sqlAssignedTag = "Delete FROM assigned_tags;";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatement atps = conn.prepareStatement(sqlAssignedTag)) {
            ps.executeUpdate();
            atps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    /**
     * Gets list of different varieties of wine.
     *
     * @return list of varieties
     */
    public List<String> getVariety() {
        List<String> varieties = new ArrayList<>();
        String sql = "SELECT DISTINCT variety FROM WINE ORDER BY variety;";
        try (Connection conn = databaseService.connect();
                 Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                varieties.add(rs.getString("variety"));
            }
            return varieties;
        } catch (SQLException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }

    /**
     * Gets list of different colours of wine.
     *
     * @return list of colours
     */
    public List<String> getColour() {
        List<String> varieties = new ArrayList<>();
        String sql = "SELECT DISTINCT colour FROM WINE ORDER BY colour;";
        try (Connection conn = databaseService.connect();
                Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                varieties.add(rs.getString("colour"));
            }
            return varieties;
        } catch (SQLException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }


    /**
     * Get the variety from the wine colour.
     *
     * @param givenColour the colour to use
     * @return the variety name
     */
    public List<String> getVarietyFromColour(String givenColour) {
        List<String> varieties = new ArrayList<>();
        String sql = "SELECT DISTINCT variety FROM WINE WHERE colour = ?;";

        try (Connection conn = databaseService.connect();
                PreparedStatement prepstatement = conn.prepareStatement(sql)) {
            prepstatement.setString(1, givenColour);
            ResultSet rs = prepstatement.executeQuery();
            while (rs.next()) {
                varieties.add(rs.getString("variety"));
            }
            return varieties;
        } catch (SQLException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }

    /**
     * Gets list of different years of wine.
     *
     * @return list of years
     */
    public List<String> getYear() {
        List<String> years = new ArrayList<>();
        String sql = "SELECT DISTINCT year FROM WINE ORDER BY year;";
        try (Connection conn = databaseService.connect();
                 Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                years.add(rs.getString("year"));
            }
            return years;
        } catch (SQLException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }

    /**
     * Gets the minimum rating of a wine.
     *
     * @return minimum rating
     */
    public int getMinRating() {
        int minRating = 0;
        String sql = "SELECT min(rating) FROM WINE";
        try (Connection conn = databaseService.connect();
                 Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                minRating = rs.getInt(1);
            }
            return minRating;
        } catch (SQLException e) {
            log.error(e);
            return minRating;
        }
    }

    /**
     * Gets max rating of a wine.
     *
     * @return max rating
     */
    public int getMaxRating() {
        int maxRating = 0;
        String sql = "SELECT max(rating) FROM WINE";
        try (Connection conn = databaseService.connect();
                 Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                maxRating = rs.getInt(1);
            }
            return maxRating;
        } catch (SQLException e) {
            log.error(e);
            return maxRating;
        }
    }

    /**
     * Gets minimum price of a wine.
     *
     * @return minimum price
     */
    public int getMinPrice() {
        int minPrice = 0;
        String sql = "SELECT min(price) FROM WINE";
        try (Connection conn = databaseService.connect();
                 Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                minPrice = rs.getInt(1);
            }
            return minPrice;
        } catch (SQLException e) {
            log.error(e);
            return minPrice;
        }
    }

    /**
     * Gets max price of a wine.
     *
     * @return max price
     */
    public int getMaxPrice() {
        int maxPrice = 0;
        String sql = "SELECT max(price) FROM WINE";
        try (Connection conn = databaseService.connect();
                 Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                maxPrice = rs.getInt(1);
            }
            return maxPrice;
        } catch (SQLException e) {
            log.error(e);
            return maxPrice;
        }
    }

    /**
     * The default string value for the query builder for when the filter
     * should not be applied.
     *
     * @return the default string value "0"
     */
    public String getDefaultStringQuery() {
        return defaultStringQuery;
    }

    /**
     * The default max price allowed for the query builder.
     *
     * @return the default max price 800.00
     */
    public double getDefaultMaxPrice() {
        return defaultMaxPrice;
    }

    /**
     * The default min price allowed for the query builder.
     *
     * @return the default min price 0
     */
    public double getDefaultMinPrice() {
        return defaultMinPrice;
    }

    /**
     * The default min rating allowed for the query builder.
     *
     * @return the default min rating 0
     */
    public int getDefaultMinRating() {
        return defaultMinRating;
    }

    /**
     * Builds sql query for search and filter.
     *
     * @param search term to search for
     * @param variety the variety to use
     * @param colour the colour to use
     * @param region the region to use
     * @param year the year to filter
     * @param minPrice the min price to use
     * @param maxPrice the max price to use
     * @param minRating the min rating to use
     * @return sql string
     */
    public String queryBuilder(String search, String variety,
                               String colour, String region,
                               String year, double minPrice,
                               double maxPrice, double minRating) {

        // Build the SQL statement
        String sql = "SELECT DISTINCT wine.id, wine.name, wine.description, wine.year, "
                + "wine.rating, wine.variety, wine.price, wine.colour, "
                + "vineyard.name AS vineyardName, vineyard.region FROM WINE, VINEYARD "
                + "WHERE vineyard.id = wine.vineyard";

        // Append onto the sql statement if necessary
        if (search != null) {
            sql +=  " AND (wine.name LIKE ? OR wine.description LIKE ?)";
        }

        // If the variety is valid, add it to the query
        String nullStringQuery = "null";
        if (!(Objects.equals(variety, defaultStringQuery) || variety.equals(nullStringQuery))) {
            sql += " AND wine.variety = '" + variety + "'";
        }
        //If the colour is valid, add it to the query
        if (!(Objects.equals(colour, defaultStringQuery)
                || Objects.equals(colour, nullStringQuery))) {
            sql += " AND wine.colour = '" + colour + "'";
        }

        // If the region is valid, add it to the query
        if (!Objects.equals(region, defaultStringQuery) && region != null) {
            region = region.replace("'", "''");
            sql += " AND vineyard.region = '" + region + "'";
        }

        // If the year is valid, add it to the query
        if (!Objects.equals(year, defaultStringQuery)) {
            sql += " AND wine.year = " + year;
        }

        // If the max price is valid, add it to the query
        if (maxPrice != defaultMaxPrice) {
            sql += " AND wine.price <= " + maxPrice;
        }

        // If the min price is valid, add it to the query
        if (minPrice != defaultMinPrice) {
            sql += " AND wine.price >= " + minPrice;
        }

        // If the rating fields are valid, add them to the query
        int defaultMaxRating = 100;
        if (minRating > defaultMinRating && minRating <= defaultMaxRating) {
            sql += " AND wine.rating >= " + minRating;
        }

        // "cap off" the SQL statement and return it
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
                        new Vineyard(rs.getInt("id"),
                                rs.getString("vineyardName"),
                                rs.getString("Region"))
                ));
            }
            return searchedWines;
        } catch (SQLException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }
}
