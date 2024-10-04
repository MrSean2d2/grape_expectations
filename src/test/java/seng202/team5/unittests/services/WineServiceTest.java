package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.services.WineService;

public class WineServiceTest {
    private static WineService wineService;

    /**
     * Initial setup.
     */
    @BeforeAll
    public static void setUp() {
        wineService = WineService.getInstance();
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

    @Test
    public void testBlankColour() {
        String colour = "   ";
        assertEquals("Unknown", wineService.validColour(colour));
    }

    @Test
    public void testEmptyColour() {
        String colour = "";
        assertEquals("Unknown", wineService.validColour(colour));
    }

    @Test
    public void testValidVariety() {
        String variety = "Pinot Noir";
        assertEquals("Pinot Noir", wineService.validVariety(variety));
    }
}
