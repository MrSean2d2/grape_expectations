package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.DataLoadService;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.WineService;

public class WineServiceTest {
    private static WineService wineService;
    private static DatabaseService databaseService;

    /**
     * Initial setup.
     */
    @BeforeAll
    public static void setUp() throws InstanceAlreadyExistsException {
        DatabaseService.removeInstance();
        databaseService =  DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test.db");
        wineService = WineService.getInstance();
    }

    @BeforeEach
    public void resetDb() {
        databaseService.resetDb();
    }

    /**
     * tests that only one instance of wine is created
     */
    @Test
    public void testSingletonInstance() {
        WineService instance1 = WineService.getInstance();
        WineService instance2 = WineService.getInstance();
        assertSame(instance1, instance2);
    }

    /**
     * tests getting and setting an instance of wine
     */
    @Test
    public void testSetAndGetSelectedWine() {
        WineService wineService = WineService.getInstance();
        Vineyard testVineyard = null;
        Wine wine = new Wine(1, "Test Wine", "Fruity", 2020, 99, 15.99, "Chardonnay", "White",
                testVineyard);
        wineService.setSelectedWine(wine);
        assertEquals(wine, wineService.getSelectedWine());
    }

    /**
     * create a valid wine, test that it is valid
     */
    @Test
    public void testValidWine() {
        Wine validWine = new Wine("wineName", "description", 2014, 85, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertTrue(wineService.isValidWine(validWine));
    }

    /**
     * create an unnamed wine, test that it is invalid
     */
    @Test
    public void testUnnamedWine() {
        Wine invalidWine = new Wine("", "description", 2014, 85, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(wineService.isValidWine(invalidWine));
    }

    /**
     * create a wine without a description, test that it is invalid
     * wines do not need description to be valid
     */
    @Test
    public void testNoDescWine() {
        Wine validWine = new Wine("wineName", "", 2014, 85, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertTrue(wineService.isValidWine(validWine));
    }

    /**
     * create a wine with year of -10, test that it is invalid
     */
    @Test
    public void testNegativeYearWine() {
        Wine invalidWine = new Wine("wineName", "description", -10, 85, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(wineService.isValidWine(invalidWine));
    }

    /**
     * create a wine with year 1000, test that it is invalid
     * wine from year 100 is too old to realistically be in the database
     */
    @Test
    public void testOldWine() {
        Wine invalidWine = new Wine("wineName", "description", 1000, 85, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(wineService.isValidWine(invalidWine));
    }

    /**
     * create a valid wine, test that it is valid
     */
    @Test
    public void testFutureWine() {
        Wine invalidWine = new Wine("wineName", "description", 2300, 85, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(wineService.isValidWine(invalidWine));
    }

    /**
     * create a wine with negative score, test that it is invalid
     */
    @Test
    public void testNegativeScoreWine() {
        Wine invalidWine = new Wine("wineName", "description", 2014, -1, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(wineService.isValidWine(invalidWine));
    }

    /**
     * create a wine with score over 100, test that it is valid
     */
    @Test
    public void testOver100ScoreWine() {
        Wine validWine = new Wine("wineName", "description", 2014, 101, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(wineService.isValidWine(validWine));
    }

    /**
     * create a wine with negative price, test that it is invalid
     */
    @Test
    public void testNegativePriceWine() {
        Wine invalidWine = new Wine("wineName", "description", 2014, 85, -1, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(wineService.isValidWine(invalidWine));
    }

    /**
     * create a wine with empty variety, test that it is valid
     */
    @Test
    public void testNoVarietyWine() {
        Wine validWine = new Wine("wineName", "description", 2014, 85, 20, "",
                "white", new Vineyard("vineyard", "region"));
        assertTrue(wineService.isValidWine(validWine));
    }

    /**
     * Test that Unknown gets set for blank colour.
     */
    @Test
    public void testBlankColour() {
        String colour = "   ";
        assertEquals("Unknown", wineService.validColour(colour));
    }

    /**
     * Test that Unknown gets set for empty colour.
     */
    @Test
    public void testEmptyColour() {
        String colour = "";
        assertEquals("Unknown", wineService.validColour(colour));
    }

    /**
     * Verify that a valid variety is indeed valid.
     */
    @Test
    public void testValidVariety() {
        String variety = "Pinot Noir";
        assertEquals("Pinot Noir", wineService.validVariety(variety));
    }

    /**
     * Test that getWineList returns an empty list without any wines in the database.
     */
    @Test
    public void testGetWineListEmpty() {
        WineService mockedWineService = spy(wineService);
        doNothing().when(mockedWineService).populateDatabase(any());
        assertEquals(0, mockedWineService.getWineList().size());
    }

    /**
     * Test getWineList with a non-empty database.
     */
    @Test
    public void testGetWineList() {
        WineService mockedWineService = spy(wineService);
        doNothing().when(mockedWineService).populateDatabase(any());
        Wine wine = new Wine("Wine", "Description", 2014, 85, 10, "Pinot Noir", "Red",
                new Vineyard("Vineyard", "Region"));
        Wine wine1 = new Wine("Another wine", "Descriptiion", 2014, 85, 10, "Pinot Gris", "White",
                new Vineyard("Another Vineyard", "A different region"));
        WineDAO wineDAO = new WineDAO(new VineyardDAO());
        wine.setId(wineDAO.add(wine));
        wineDAO.add(wine1);
        assertEquals(2, mockedWineService.getWineList().size());
        assertEquals(wine, mockedWineService.getWineList().getFirst());
    }

    @Test
    public void testDuplicateWine() {
        Vineyard vineyard = new Vineyard("Vineyard", "Region");
        Wine wine = new Wine("Wine", "Description", 2014, 85, 10, "Pinot Noir", "Red",
                vineyard);
        Wine wine1 = new Wine("Wine", "Description", 2014, 85, 10, "Pinot Noir", "Red",
                vineyard);
        WineDAO wineDAO = new WineDAO(new VineyardDAO());
        wineDAO.add(wine);
        assertTrue(wineService.checkExistingWine(wine1.getName()));
    }

    @Test
    public void testNonDuplicateWine() {
        Vineyard vineyard = new Vineyard("Vineyard", "Region");
        Wine wine = new Wine("Wine", "Description", 2014, 85, 10, "Pinot Noir", "Red",
                vineyard);
        WineDAO wineDAO = new WineDAO(new VineyardDAO());
        wineDAO.add(wine);
        assertFalse(wineService.checkExistingWine("Not That Wine"));
    }

    @Test
    public void populateDatabaseTest() {
        DataLoadService dataLoadService = new DataLoadService(
                System.getProperty("user.dir") + "/src/test/resources/test.csv");
        wineService.populateDatabase(dataLoadService);
        WineDAO wineDAO = new WineDAO(new VineyardDAO());
        assertEquals(16, wineDAO.getAll().size());
    }
}
