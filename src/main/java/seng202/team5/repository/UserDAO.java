package seng202.team5.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements DAOInterface<User> {
    private final DatabaseService databaseService;
    private static final Logger log = LogManager.getLogger(UserDAO.class);

    public UserDAO() {
        databaseService = DatabaseService.getInstance();
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try(Connection conn = databaseService.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("role")));
            }
            return users;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }

    /**
     * Gets an individual user from database by id
     * @param id id of user to get
     * @return user object from database that matches id
     */
    @Override
    public User getOne(int id) {
        throw new NotImplementedException();
    }


    /**
     * Gets a single user from database by username, throws not found exception if user does not exist
     * @param username username to search for
     * @return User retrieved with matching username
     * @throws NotFoundException no user found with that username
     */
    public User getFromUserName(String username) throws NotFoundException {
        String sql = "SELECT * FROM users WHERE username=?";
        try(Connection conn = databaseService.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("role"));
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
        throw new NotFoundException(String.format("No user with %s found", username));
    }

    /**
     * Adds an individual user to database
     * @param toAdd user to add
     * @return true if no error, false if sql error
     */
    @Override
    public int add(User toAdd) throws DuplicateEntryException {
        String sql = "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)";
        try(Connection conn = databaseService.connect();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, toAdd.getId());
            ps.setString(2, toAdd.getUsername());
            ps.setString(3, toAdd.getPassword());
            ps.setString(4, toAdd.getRole());
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
     * Deletes user from database by id
     * @param id id of object to delete
     */
    @Override
    public void delete(int id) {
        throw new NotImplementedException();
    }

    /**
     * Updates user in database
     * @param toUpdate user that needs to be updated (this object must be able to identify itself and its previous self)
     */
    @Override
    public void update(User toUpdate) {
        throw new NotImplementedException();
    }

}

