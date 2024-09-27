package seng202.team5.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.models.AssignedTag;
import seng202.team5.services.DatabaseService;

/**
 * Database Access Object for the Assigned_tags tables in the SQL database.
 *
 * @author Martyn Gascoigne
 */
public class AssignedTagsDAO implements DAOInterface<AssignedTag> {
    private static final Logger log = LogManager.getLogger(AssignedTagsDAO.class);
    private final DatabaseService databaseService;


    /**
     * Constructor - initialise / grab the singleton of DatabaseService.
     */
    public AssignedTagsDAO() {
        databaseService = DatabaseService.getInstance();
    }


    /**
     * Gets all the tags in the Assigned_Tags table.
     *
     * @return list of all assigned tags objects in the table
     */
    @Override
    public List<AssignedTag> getAll() {
        List<AssignedTag> tags = new ArrayList<>();
        String sql = "SELECT * FROM assigned_tags";
        try (Connection conn = databaseService.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AssignedTag tag = new AssignedTag(
                        rs.getInt("tagid"),
                        rs.getInt("userid"),
                        rs.getInt("wineid"));
                tags.add(tag);
            }
            return tags;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }

    /**
     * Gets a tag from tag id.
     *
     * @param tagId id of the tag
     * @return The tag with a given id
     */
    @Override
    public AssignedTag getOne(int tagId) {
        AssignedTag tag;
        String sql = "SELECT * FROM assigned_tags WHERE tagid=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tagId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tag = new AssignedTag(
                        rs.getInt("tagid"),
                        rs.getInt("userid"),
                        rs.getInt("wineid"));
                return tag;
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return null;
        }
        return null;
    }


    /**
     * Gets a list of a given user's tags (by id).
     *
     * @param userid id of the user
     * @param tagid id of the tag
     * @return list of tags from a given user of a given type
     */
    public List<AssignedTag> getTagsFromUser(int userid, int tagid) {
        List<AssignedTag> tags = new ArrayList<>();
        String sql = "SELECT * FROM assigned_tags WHERE userid=? AND tagid=?";
        try (Connection conn = databaseService.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userid);
            ps.setInt(2, tagid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AssignedTag tag = new AssignedTag(
                        rs.getInt("tagid"),
                        rs.getInt("userid"),
                        rs.getInt("wineid"));
                tags.add(tag);
            }
            return tags;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }


    /**
     * Gets a list of a given user's tags (by id).
     *
     * @param id id of the user
     * @return list of tags from the given user
     */
    public List<AssignedTag> getFromUser(int id) {
        List<AssignedTag> tags = new ArrayList<>();
        String sql = "SELECT * FROM assigned_tags WHERE userid=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AssignedTag tag = new AssignedTag(
                        rs.getInt("tagid"),
                        rs.getInt("userid"),
                        rs.getInt("wineid"));
                tags.add(tag);
            }
            return tags;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }


    /**
     * Gets a list of a given wine's tags (by id).
     *
     * @param wineId id of the wine
     * @param userId id of the wine
     * @return list of tags from the given wine
     */
    public List<AssignedTag> getAllAssigned(int wineId, int userId) {
        List<AssignedTag> tags = new ArrayList<>();
        String sql = "SELECT * FROM assigned_tags WHERE wineid=? AND userid=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wineId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AssignedTag tag = new AssignedTag(
                        rs.getInt("tagid"),
                        rs.getInt("userid"),
                        rs.getInt("wineid"));
                tags.add(tag);
            }
            return tags;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }


    /**
     * Adds an individual tag to database.
     *
     * @param toAdd tag to add
     * @return return 1 if it could be created, -1 otherwise.
     */
    @Override
    public int add(AssignedTag toAdd) throws DuplicateEntryException {
        String sql = "INSERT OR IGNORE INTO assigned_tags (wineid, tagid, userid) VALUES (?, ?, ?)";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, toAdd.getWineId());
            ps.setInt(2, toAdd.getTagId());
            ps.setInt(3, toAdd.getUserId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int insertId = -1;
            if (rs.next()) {
                insertId = rs.getInt(1);
            }
            return insertId;
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
    public void update(AssignedTag toUpdate) {
        throw new NotImplementedException();
    }

    /**
     * implementation of DAOInterface delete.
     *
     * @param tagId id of object to delete
     */
    public void deleteFromTagId(int tagId) {
        String sql = "DELETE FROM assigned_tags WHERE tagid=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tagId);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    /**
     * implementation of DAOInterface delete.
     *
     * @param userId id of object to delete
     */
    public void deleteFromUserId(int userId) {
        String sql = "DELETE FROM assigned_tags WHERE userid=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    /**
     * implementation of DAOInterface delete.
     *
     * @param userId id of object to delete
     * @param wineId id of object to delete
     */
    public void deleteFromUserWineId(int userId, int wineId) {
        String sql = "DELETE FROM assigned_tags WHERE userid=? AND wineid=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, wineId);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    /**
     * implementation of DAOInterface delete.
     *
     * @param wineId id of object to delete
     */
    public void deleteFromWineId(int wineId) {
        String sql = "DELETE FROM assigned_tags WHERE wineid=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wineId);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    /**
     * implementation of DAOInterface delete.
     *
     * @param wineId id of object to delete
     */
    public void deleteAssignedTag(int wineId, int tagId, int userId) {
        String sql = "DELETE FROM assigned_tags WHERE wineid=? AND tagid=? AND userId=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wineId);
            ps.setInt(2, tagId);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }
}

