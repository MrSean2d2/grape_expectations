package seng202.team5.unittests.models;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.DatabaseService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * tests for wine model
 */
public class WineTest {
    /**
     * create a valid wine, test that it is valid
     */
    @Test
    public void testValidWine() {
        Wine validWine = new Wine("wineName", "description", 2014, 85, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertTrue(validWine.isValidWine());
    }
    /**
     * create an unnamed wine, test that it is invalid
     */
    @Test
    public void testUnnamedWine() {
        Wine invalidWine = new Wine("", "description", 2014, 85, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(invalidWine.isValidWine());
    }
    /**
     * create a wine without a description, test that it is invalid
     * wines do not need description to be valid
     */
    @Test
    public void testNoDescWine() {
        Wine validWine = new Wine("wineName", "", 2014, 85, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertTrue(validWine.isValidWine());
    }
    /**
     * create a wine with year of -10, test that it is invalid
     */
    @Test
    public void testNegativeYearWine() {
        Wine invalidWine = new Wine("wineName", "description", -10, 85, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(invalidWine.isValidWine());
    }
    /**
     * create a wine with year 1000, test that it is invalid
     * wine from year 100 is too old to realistically be in the database
     */
    @Test
    public void testOldWine() {
        Wine invalidWine = new Wine("wineName", "description", 1000, 85, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(invalidWine.isValidWine());
    }
    /**
     * create a valid wine, test that it is valid
     */
    @Test
    public void testFutureWine() {
        Wine invalidWine = new Wine("wineName", "description", 2300, 85, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(invalidWine.isValidWine());
    }
    /**
     * create a wine with negative score, test that it is invalid
     */
    @Test
    public void testNeagativeScoreWine() {
        Wine invalidWine = new Wine("wineName", "description", 2014, -1, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(invalidWine.isValidWine());
    }
    /**
     * create a wine with score over 100, test that it is valid
     */
    @Test
    public void testOver100ScoreWine() {
        Wine validWine = new Wine("wineName", "description", 2014, 101, 20, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(validWine.isValidWine());
    }
    /**
     * create a wine with negative price, test that it is invalid
     */
    @Test
    public void testNegativePriceWine() {
        Wine invalidWine = new Wine("wineName", "description", 2014, 85, -1, "pinot noir",
                "white", new Vineyard("vineyard", "region"));
        assertFalse(invalidWine.isValidWine());
    }
    /**
     * create a wine with empty variety, test that it is valid
     */
    @Test
    public void testNoVarietyWine() {
        Wine validWine = new Wine("wineName", "description", 2014, 85, 20, "",
                "white", new Vineyard("vineyard", "region"));
        assertTrue(validWine.isValidWine());
    }


}
