package seng202.team5.unittests.services;

import org.junit.jupiter.api.Test;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.services.WineService;

import static org.junit.jupiter.api.Assertions.*;

public class WineServiceTest {

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
        Wine wine = new Wine(1, "Test Wine", "Fruity", 2020, 99, 15.99, "Chardonnay", "White", testVineyard, true, "I like this wine");
        wineService.setSelectedWine(wine);
        assertEquals(wine, wineService.getSelectedWine());
    }

}
