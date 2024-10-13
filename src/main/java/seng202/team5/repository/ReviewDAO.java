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
import seng202.team5.models.Review;
import seng202.team5.services.DatabaseService;

/**
 * Database Access Object for the Review table in the SQL database.
 *
 * @author Martyn Gascoigne
 */
public class ReviewDAO implements DAOInterface<Review> {
    private final DatabaseService databaseService;
    private static final Logger log = LogManager.getLogger(ReviewDAO.class);


    /**
     * Constructor - initialise / grab the singleton of DatabaseService.
     */
    public ReviewDAO() {
        databaseService = DatabaseService.getInstance();
    }


    /**
     * Gets all the reviews in the Review table.
     *
     * @return list of all review objects in the table
     */
    @Override
    public List<Review> getAll() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review";
        try (Connection conn = databaseService.connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("wineid"),
                        rs.getInt("userid"),
                        rs.getBoolean("favorite"),
                        rs.getString("notes"),
                        rs.getInt("rating"));
                reviews.add(review);
            }
            return reviews;
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
    public Review getOne(int id) throws NotImplementedException {
        throw new NotImplementedException(
                "Don't use this method! You must specify a wineId and a userId!");
    }


    /**
     * Gets a list of a given user's reviews (by id).
     *
     * @param id id of the user
     * @param onlyRated whether only the rated ones should be fetched
     * @return list of reviews from the given user
     */
    public List<Review> getFromUser(int id, boolean onlyRated) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review WHERE userid=?";
        if (onlyRated) {
            sql += "AND rating !=-1"; //default rating/ unrated
        }
        try (Connection conn = databaseService.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("wineid"),
                        rs.getInt("userid"),
                        rs.getBoolean("favorite"),
                        rs.getString("notes"),
                        rs.getInt("rating"));
                reviews.add(review);
            }
            return reviews;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }

    /**
     * Get the list of wine ids from the user id.
     *
     * @param userid the user id
     * @return the list of wine ids which the user has reviewed
     */
    public List<Integer> getIdsFromUser(int userid) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT wineid FROM review WHERE userid=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("wineid"));
            }
            return ids;
        } catch (SQLException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }


    /**
     * Gets a list of a given wine's reviews (by id).
     *
     * @param id id of the wine
     * @return list of reviews of the given wine
     */
    public List<Review> getFromWine(int id) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review WHERE wineid=?";
        try (Connection conn = databaseService.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("wineid"),
                        rs.getInt("userid"),
                        rs.getBoolean("favorite"),
                        rs.getString("notes"),
                        rs.getInt("rating"));
                reviews.add(review);
            }
            return reviews;
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
    public Review getWineReview(int wineId, int userId) {
        Review review;
        String sql = "SELECT * FROM review WHERE wineid=? AND userid=?";
        try (Connection conn = databaseService.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wineId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                review = new Review(
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
    public int add(Review toAdd) throws DuplicateEntryException {
        String sql =
                "INSERT INTO review (wineid, userid, favorite, notes, rating) VALUES (?,?,?,?,?)";
        try (Connection conn = databaseService.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, toAdd.getWineId());
            ps.setInt(2, toAdd.getUserId());
            ps.setBoolean(3, toAdd.isFavourite());
            ps.setString(4, toAdd.getNotes());
            ps.setInt(5, toAdd.getRating());
            ps.executeUpdate();
            return 1;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return -1;
        }
    }

    /**
     * implementation of DAOInterface delete.
     *
     * @param id id of object to delete
     */
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
        String sql = "DELETE FROM review WHERE wineid=? AND userid=?";
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
    public void update(Review toUpdate) {
        String sql = "UPDATE review SET favorite=?, "
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

