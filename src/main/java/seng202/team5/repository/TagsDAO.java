package seng202.team5.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.models.Tag;
import seng202.team5.services.DatabaseService;

/**
 * Database Access Object for the Created_tags tables in the SQL database.
 *
 * @author Martyn Gascoigne
 */
public class TagsDAO implements DAOInterface<Tag> {
    private static final Logger log = LogManager.getLogger(TagsDAO.class);
    private final DatabaseService databaseService;


    /**
     * Constructor - initialise / grab the singleton of DatabaseService.
     */
    public TagsDAO() {
        databaseService = DatabaseService.getInstance();
    }


    /**
     * Gets all the tags in the Created_Tags table.
     *
     * @return list of all created tags objects in the table
     */
    @Override
    public List<Tag> getAll() {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT * FROM created_tags";
        try (Connection conn = databaseService.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Tag tag = new Tag(
                        rs.getInt("tagid"),
                        rs.getInt("userid"),
                        rs.getString("name"),
                        rs.getInt("colour"));
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
    public Tag getOne(int tagId) {
        Tag tag;
        String sql = "SELECT * FROM created_tags WHERE tagid=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tagId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tag = new Tag(
                        rs.getInt("tagid"),
                        rs.getInt("userid"),
                        rs.getString("name"),
                        rs.getInt("colour"));
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
     * @param id id of the user
     * @return list of tags from the given user
     */
    public List<Tag> getFromUser(int id) {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT * FROM created_tags WHERE userid=? OR userid=-1";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Tag tag = new Tag(
                        rs.getInt("tagid"),
                        rs.getInt("userid"),
                        rs.getString("name"),
                        rs.getInt("colour"));
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
    public int add(Tag toAdd) throws DuplicateEntryException {
        String sql = "INSERT INTO created_tags (userId, name, colour) VALUES (?, ?, ?)";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, toAdd.getUserId());
            ps.setString(2, toAdd.getName());
            ps.setInt(3, toAdd.getColour());
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

    /**
     * implementation of DAOInterface delete.
     *
     * @param tagId id of object to delete
     */
    @Override
    public void delete(int tagId) {
        String sql = "DELETE FROM created_tags WHERE tagid=?";
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
     * @param userId id of the user from which to delete object to delete
     */
    public void deleteFromUser(int userId) {
        String sql = "DELETE FROM created_tags WHERE userid=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    /**
     * Updates a tag in the database.
     *
     * @param toUpdate tag that needs to be updated
     *                 (this object must be able to identify itself and its previous self)
     */
    @Override
    public void update(Tag toUpdate) {
        String sql = "UPDATE created_tags SET name=?, "
                + "colour=? "
                + "WHERE tagid=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toUpdate.getName());
            ps.setInt(2, toUpdate.getColour());
            ps.setInt(3, toUpdate.getTagId());
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }
}
