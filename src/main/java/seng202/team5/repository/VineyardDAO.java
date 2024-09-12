package seng202.team5.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.models.Vineyard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VineyardDAO implements DAOInterface<Vineyard> {
    private static final Logger log = LogManager.getLogger(VineyardDAO.class);
    private final DatabaseService databaseService;

    public VineyardDAO(){
        databaseService = DatabaseService.getInstance();
    }

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
    @Override
    public int add (Vineyard toAdd){
        String sql = "INSERT INTO vineyard (name) values (?);";
        try (Connection conn = databaseService.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toAdd.getName());

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
