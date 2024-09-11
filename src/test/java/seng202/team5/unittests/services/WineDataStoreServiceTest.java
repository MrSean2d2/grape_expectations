package seng202.team5.unittests.services;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.models.*;
import seng202.team5.services.WineDataStoreService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link seng202.team5.services.WineDataStoreService}.
 *
 * @author Sean Reitsma
 */
public class WineDataStoreServiceTest {
    WineDataStoreService wineDataStoreService;

    @BeforeEach
    public void setUp() {
        wineDataStoreService = new WineDataStoreService(System.getProperty("user.dir")
                + "/src/test/resources/test.db");
    }

    @Test
    public void getTestWineTest() {
//        List<Wine> wines = wineDataStoreService.getWines();
//        Wine testWine = new Wine(0, "Test Wine",
//                "Test Wine is a lovely fruity wine that tastes very nice",
//                2024, 99, 20,
//                new WineVariety("Pinot Noir", WineType.UNKNOWN),
//                new Region("Test Region", new ArrayList<>(), new ArrayList<>()),
//                new Vineyard("Test Vineyard"));
//        assertEquals(testWine.getName(), wines.getFirst().getName());
//        assertEquals(testWine.getYear(), wines.getFirst().getYear());
    }
}
