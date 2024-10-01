package seng202.team5.unittests.services;

import org.junit.jupiter.api.Test;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.services.VineyardService;
import seng202.team5.services.WineService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VineyardServiceTest {
    @Test
    public void testSetAndGetSelectedVineyard() {
        VineyardService vineyardService = VineyardService.getInstance();
        Vineyard testVineyard = new Vineyard("Test Vineyard", "Test Region");
        vineyardService.setSelectedVineyard(testVineyard);
        assertEquals(testVineyard, vineyardService.getSelectedVineyard());
    }
}
