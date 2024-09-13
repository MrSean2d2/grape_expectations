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

    public int getIdFromName(String vineyardName) {
        int id = 0;
        String sql = "SELECT * FROM vineyard WHERE name=?";
        try (Connection conn = databaseService.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vineyardName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    id = rs.getInt("id");
                }
                return id;
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return 0;
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
        String sql = "INSERT OR IGNORE INTO vineyard (name, region) VALUES (?, ?)";
        try(Connection conn = databaseService.connect();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, toAdd.getName());
            ps.setString(2, toAdd.getRegion());
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


    /**
     * Get all the distinct regions in the database by alphabetical order.
     *
     * @return the list of distinct region names
     */
    public List<String> getRegions() {
        List<String> regions = new ArrayList<>();
        String sql = "SELECT DISTINCT region FROM VINEYARD ORDER BY region;";
        try (Connection conn = databaseService.connect();
                Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                regions.add(rs.getString("region"));
            }
            return regions;
        } catch (SQLException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }

    @Override
    public void update(Vineyard toUpdate) {
        throw new NotImplementedException();
    }
}
