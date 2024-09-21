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
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.models.User;
import seng202.team5.services.DatabaseService;

/**
 * Database Access Object for the User table in the SQL database.
 *
 * @author Martyn Gascoigne
 * @author Finn Brown
 */
public class UserDAO implements DAOInterface<User> {
    private final DatabaseService databaseService;
    private static final Logger log = LogManager.getLogger(UserDAO.class);

    /**
     * Constructor - initialise / grab the singleton of DatabaseService.
     */
    public UserDAO() {
        databaseService = DatabaseService.getInstance();
    }


    /**
     * Gets a list of users in the user table.
     *
     * @return list of all user objects in the table
     */
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (Connection conn = databaseService.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("icon"));
                users.add(user);
            }
            return users;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }


    /**
     * Gets an individual user from database by id.
     *
     * @param id id of user to get
     * @return user object from database that matches id
     */
    @Override
    public User getOne(int id) {
        User user = null;
        String sql = "SELECT * FROM user WHERE id=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getInt("icon"));
                }
                return user;
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return null;
        }
    }


    /**
     * Gets a single user from database by username,
     * throws not found exception if user does not exist.
     *
     * @param username username to search for
     * @return User retrieved with matching username
     * @throws NotFoundException no user found with that username
     */
    public User getFromUserName(String username) throws NotFoundException {
        String sql = "SELECT * FROM user WHERE username=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("icon"));
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
        throw new NotFoundException(String.format("No user with %s found", username));
    }

    public List<User> getMatchingUserName(String term) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE username LIKE ?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + term + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("icon"));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }


    /**
     * Checks if a username is unique.
     *
     * @param username username to search for
     * @return Boolean - true if user is unique, false if not.
     */
    public boolean userIsUnique(String username) {
        String sql = "SELECT * FROM user WHERE username=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return false;
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
        return true;
    }

    /**
     * Get how many admin users are present in the database.
     *
     * @return the count of admin users
     */
    public int getAdminCount() {
        String sql = "SELECT COUNT(*) FROM user WHERE role='admin'";
        int count = 0;
        try (Connection conn = databaseService.connect();
                Statement s = conn.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return 0;
        }
    }


    /**
     * Adds an individual user to database.
     *
     * @param toAdd user to add
     * @return userId if new user could be created, -1 if not
     */
    @Override
    public int add(User toAdd) throws DuplicateEntryException {
        String sql = "INSERT INTO user (username, password, role, icon) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toAdd.getUsername());
            ps.setString(2, toAdd.getPassword());
            ps.setString(3, toAdd.getRole());
            ps.setInt(4, toAdd.getIconNumber());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int insertId = -1;
            if (rs.next()) {
                insertId = rs.getInt(1);
            }
            return insertId;
        } catch (SQLException sqlException) {
            if (sqlException.getErrorCode() == 19) {
                throw new DuplicateEntryException("Duplicate username");
            }
            log.error(sqlException);
            return -1;
        }
    }


    /**
     * Deletes user from database by id.
     *
     * @param id id of object to delete
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM user WHERE id=?";
        String sqlReview = "DELETE FROM review WHERE userid=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatement rps = conn.prepareStatement(sqlReview)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            rps.setInt(1, id);
            rps.executeUpdate();

        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }


    /**
     * Updates user in database.
     *
     * @param toUpdate user that needs to be updated
     *        (this object must be able to identify itself and its previous self)
     */
    @Override
    public void update(User toUpdate) {
        String sql  = "UPDATE user SET username=?, "
                    + "role=? "
                    + "WHERE id=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toUpdate.getUsername());
            ps.setString(2, toUpdate.getRole());
            ps.setInt(3, toUpdate.getId());
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }
}

