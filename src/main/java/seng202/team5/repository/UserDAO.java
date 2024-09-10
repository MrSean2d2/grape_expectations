package seng202.team5.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserDAO implements DaoInterface {
    private final DatabaseService databaseService;
    private static final Logger log = LogManager.getLogger(UserDAO.class);

    public UserDAO() {
        databaseService = DatabaseService.getInstance();
    }




    @Override
    public List getAll() {

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
