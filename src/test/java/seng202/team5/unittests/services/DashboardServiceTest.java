package seng202.team5.unittests.services;

import impl.org.controlsfx.collections.MappingChange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.*;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.UserDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.DashboardService;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.UserService;

import java.util.List;
import java.util.Map;

public class DashboardServiceTest {
    private DashboardService dashboardService;
    private ReviewDAO reviewDAO;
    private WineDAO wineDAO;
    private VineyardDAO vineyardDAO;
    private UserDAO userDAO;
    public static DatabaseService databaseService;
    private UserService userService;

    @BeforeAll
    public static void setup() throws DuplicateEntryException, InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test_database.db");
    }
    @BeforeEach
    public void resetDB() throws DuplicateEntryException {

        databaseService.resetDb();
        UserService.removeInstance();
        userService = UserService.getInstance();
        userService.signOut();


        reviewDAO = new ReviewDAO();
        vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);
        userDAO = new UserDAO();

        User testUser = new User("Test User!", "password", Role.USER, 0);
        testUser.setId(userDAO.add(testUser));

        this.dashboardService = new DashboardService(testUser.getId(), vineyardDAO, wineDAO, reviewDAO);

        Vineyard vineyard1 = new Vineyard("Vineyard A", "Region A");
        Vineyard vineyard2 = new Vineyard("Vineyard B", "Region B");
        Vineyard vineyard3 = new Vineyard("Vineyard C", "Region C");

        Wine wine1 = new Wine("TestWine1","delicious",2020,
                65, 12 , "Pinot Noir", "white", vineyard1);
        Wine wine2 = new Wine("TestWine2","yummy",2018,
                84, 38 , "Syrah", "red", vineyard2);
        Wine wine3 = new Wine("TestWine3","excellent",2003,
                91, 55 , "Sauvingon Blanc", "white", vineyard3);

        wine1.setId(wineDAO.add(wine1));
        wine2.setId(wineDAO.add(wine2));
        wine3.setId(wineDAO.add(wine3));


        reviewDAO.add(new Review(wine1.getId(), testUser.getId(), true, "Great", 5));
        reviewDAO.add(new Review(wine2.getId(), testUser.getId(), true, "Mid", 3));
        reviewDAO.add(new Review(wine3.getId(), testUser.getId(), true, "bad", 1));

        dashboardService.initializeData();
    }

    @Test
    public void testGetTopVariety(){
        List<Map.Entry<String, Integer>> topVariety = dashboardService.getTopVariety();
        Assertions.assertEquals(3, topVariety.size());

        Assertions.assertEquals("Pinot Noir", topVariety.get(0).getKey());
        Assertions.assertEquals(5, topVariety.get(0).getValue().intValue());

    }
    @Test
    public void testTopRegion(){
        List<Map.Entry<String, Integer>> topRegion = dashboardService.getTopRegion();

        Assertions.assertEquals("Region A", topRegion.get(0).getKey());
        Assertions.assertEquals(5, topRegion.get(0).getValue().intValue());

    }
    @Test
    public void testTopYear(){
        List<Map.Entry<Integer, Integer>> topYear = dashboardService.getTopYear();

        Assertions.assertEquals(2020, topYear.get(0).getKey());
        Assertions.assertEquals(5, topYear.get(0).getValue().intValue());

    }


}
