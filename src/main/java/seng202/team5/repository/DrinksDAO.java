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
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.models.Drinks;

/**
 * Database Access Object for the Drinks table in the SQL database.
 *
 * @author Martyn Gascoigne
 */
public class DrinksDAO implements DAOInterface<Drinks> {
    private final DatabaseService databaseService;
    private static final Logger log = LogManager.getLogger(DrinksDAO.class);


    /**
     * Constructor - initialise / grab the singleton of DatabaseService.
     */
    public DrinksDAO() {
        databaseService = DatabaseService.getInstance();
    }


    /**
     * Gets all the reviews in the Drinks table.
     *
     * @return list of all drinks objects in the table
     */
    @Override
    public List<Drinks> getAll() {
        List<Drinks> drinks = new ArrayList<>();
        String sql = "SELECT * FROM drinks";
        try (Connection conn = databaseService.connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Drinks review = new Drinks(
                        rs.getInt("wineid"),
                        rs.getInt("userid"),
                        rs.getBoolean("favorite"),
                        rs.getString("notes"),
                        rs.getInt("rating"));
                drinks.add(review);
            }
            return drinks;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }

    /**
     * Not implemented, this table has a two column primary key.
     *
     * @param id id of object to get
     * @return Nothing, this method should not be used
     * @throws NotImplementedException because this method doesn't make sense
     *                                 in this context
     * @deprecated Don't use this method for this implementation of DAOInterface
     *             as this table has a two attribute primary key.
     */
    @Deprecated
    @Override
    public Drinks getOne(int id) throws NotImplementedException {
        throw new NotImplementedException(
                "Don't use this method! You must specify a wineId and a userId!");
    }


    /**
     * Gets a list of a given user's reviews (by id).
     *
     * @param id id of the user
     * @return list of reviews from the given user
     */
    public List<Drinks> getFromUser(int id) {
        List<Drinks> drinks = new ArrayList<>();
        String sql = "SELECT * FROM drinks WHERE userid=?";
        try (Connection conn = databaseService.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Drinks review = new Drinks(
                        rs.getInt("wineid"),
                        rs.getInt("userid"),
                        rs.getBoolean("favorite"),
                        rs.getString("notes"),
                        rs.getInt("rating"));
                drinks.add(review);
            }
            return drinks;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }


    /**
     * Gets a list of a given wine's reviews (by id).
     *
     * @param id id of the wine
     * @return list of reviews of the given wine
     */
    public List<Drinks> getFromWine(int id) {
        List<Drinks> drinks = new ArrayList<>();
        String sql = "SELECT * FROM drinks WHERE wineid=?";
        try (Connection conn = databaseService.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Drinks review = new Drinks(
                        rs.getInt("wineid"),
                        rs.getInt("userid"),
                        rs.getBoolean("favorite"),
                        rs.getString("notes"),
                        rs.getInt("rating"));
                drinks.add(review);
            }
            return drinks;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }


    /**
     * Gets a wine's review from a given user.
     *
     * @param wineId id of the wine
     * @param userId id of the user
     * @return The review of the given wine from a given user
     */
    public Drinks getWineReview(int wineId, int userId) {
        Drinks review;
        String sql = "SELECT * FROM drinks WHERE wineid=? AND userid=?";
        try (Connection conn = databaseService.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wineId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                review = new Drinks(
                        rs.getInt("wineid"),
                        rs.getInt("userid"),
                        rs.getBoolean("favorite"),
                        rs.getString("notes"),
                        rs.getInt("rating"));
                return review;
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return null;
        }
        return null;
    }


    /**
     * Adds an individual review to database.
     *
     * @param toAdd review to add
     * @return return 1 if it could be created, -1 otherwise.
     */
    @Override
    public int add(Drinks toAdd) throws DuplicateEntryException {
        String sql =
                "INSERT INTO drinks (wineid, userid, favorite, notes, rating) VALUES (?,?,?,?,?)";
        try (Connection conn = databaseService.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, toAdd.getWineId());
            ps.setInt(2, toAdd.getUserId());
            ps.setBoolean(3, toAdd.isFavourite());
            ps.setString(4, toAdd.getNotes());
            ps.setInt(5, toAdd.getRating());
            ps.executeUpdate();

            //            ResultSet rs = ps.getGeneratedKeys();
            //            int insertId = -1;
            //            if (rs.next()) {
            //                insertId = rs.getInt(1);
            //            }
            return 1;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return -1;
        }
    }

    @Override
    public void delete(int id) {
        throw new NotImplementedException(
                "Don't use this method! You must specify a wineId and a userId!");
    }


    /**
     * Deletes a wine's review from a given user.
     *
     * @param wineId id of the wine
     * @param userId id of the user
     */
    public void delete(int wineId, int userId) {
        String sql = "DELETE FROM drinks WHERE wineid=? AND userid=?";
        try (Connection conn = databaseService.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wineId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }


    /**
     * Updates a review in the database.
     *
     * @param toUpdate user that needs to be updated
     *                 (this object must be able to identify itself and its previous self)
     */
    @Override
    public void update(Drinks toUpdate) {
        String sql = "UPDATE drinks SET favorite=?, "
                + "notes=?, "
                + "rating=? "
                + "WHERE wineid=? AND userid=?";
        try (Connection conn = databaseService.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, toUpdate.isFavourite());
            ps.setString(2, toUpdate.getNotes());
            ps.setInt(3, toUpdate.getRating());
            ps.setInt(4, toUpdate.getWineId());
            ps.setInt(5, toUpdate.getUserId());
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }
}

