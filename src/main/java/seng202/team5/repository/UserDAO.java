package seng202.team5.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.models.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements DaoInterface {
    private final DatabaseService databaseService;
    private static final Logger log = LogManager.getLogger(UserDAO.class);

    public UserDAO() {
        databaseService = DatabaseService.getInstance();
    }




    @Override
    public List getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USER";
        try (Connection conn = databaseService.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
//                users.add(new User(
//
//                ))
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
        return null;
    }

    @Override
    public Object getById(int id) {
        return null;
    }

    @Override
    public int add(Object object) {
        return 0;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void update(Object object) {

    }
}
