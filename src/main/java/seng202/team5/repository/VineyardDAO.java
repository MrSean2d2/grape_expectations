package seng202.team5.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.models.Vineyard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Database Access Object for Vineyard related actions.
 *
 * @author Amiele Miguel
 * @author Matthew Wills
 */
public class VineyardDAO implements DAOInterface<Vineyard> {
    private static final Logger log = LogManager.getLogger(VineyardDAO.class);
    private final DatabaseService databaseService;

    /**
     * VineyardDAO constructor creates new VineyardDAO object.
     */
    public VineyardDAO() {
        databaseService = DatabaseService.getInstance();
    }

    /**
     * Gets all Vineyard objects from database and adds to a list.
     *
     * @return list of all Vineyards in database
     */
    @Override
    public List<Vineyard> getAll() {
        List<Vineyard> vineyards = new ArrayList<>();
        String sql = "SELECT * FROM vineyard";
        try (Connection conn = databaseService.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vineyards.add(new Vineyard(rs.getString("name"), rs.getString("region")));
            }
            return vineyards;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }

    /**
     * Gets one Vineyard object in database using primary key id.
     *
     * @param id id of object to get
     * @return Vineyard object if found otherwise null
     */
    @Override
    public Vineyard getOne(int id) {
        Vineyard vineyard = null;
        String sql = "SELECT * FROM vineyard WHERE id=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vineyard = new Vineyard(rs.getString("name"), rs.getString("region"));
                }
                return vineyard;
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return null;
        }
    }

    /**
     * add Vineyard object to the database.
     *
     * @param toAdd object of type Vineyard to add
     * @return id of added Vineyard
     */
    @Override
    public int add(Vineyard toAdd) {
        String sql = "INSERT INTO vineyard (name, region) values (?, ?);";
        String checksql = "SELECT 1 FROM vineyard WHERE name = ?;";
        try (Connection conn = databaseService.connect();
                PreparedStatement checkPs = conn.prepareStatement(checksql);
                PreparedStatement insertPs = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            checkPs.setString(1, toAdd.getName());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                return -1;
            }
            insertPs.setString(1, toAdd.getName());
            insertPs.setString(2, toAdd.getRegion());
            insertPs.executeUpdate();
            ResultSet rs2 = insertPs.getGeneratedKeys();
            int insertId = -1;
            if (rs2.next()) {
                insertId = rs2.getInt(1);
            }
            return insertId;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return -1;
        }
    }

    /**
     * deletes Vineyard from database.
     *
     * @param id id of Vineyard to delete
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM vineyard WHERE id=?";
        try (Connection conn = databaseService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    @Override
    public void update(Vineyard toUpdate) {
        throw new NotImplementedException();
    }
}
