package seng202.team5.unittests.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;

/**
 * Tests for the wine object.
 */
public class WineTest {
    /**
     * Sanity test for Wine equality because it always seems to be a gotcha
     * somehow.
     */
    @Test
    public void testEquality() {
        Wine wine1 = new Wine("A nice Wine", "Very tasty", 1980, 98, 20, "Chardonnay",
                "White", new Vineyard("A vineyard", "A region"));
        Wine wine2 = new Wine("A nice Wine", "Very tasty", 1980, 98, 20, "Chardonnay",
                "White", new Vineyard("A vineyard", "A region"));
        assertEquals(wine1, wine2);
    }
}
