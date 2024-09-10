package seng202.team5.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.models.Region;
import seng202.team5.services.RegionService;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RegionServiceTest {
    private RegionService regionService;

    @BeforeEach
    public void setUp() {
        regionService = new RegionService();
    }

    @Test
    public void checkRegionExists() {
        regionService.getRegion("TestRegion1");

        Region region = regionService.getRegionList().getFirst();

        assertEquals("TestRegion1", region.getName());
    }

    @Test
    public void checkNumRegions() {
        regionService.getRegion("TestRegion1");
        regionService.getRegion("TestRegion2");
        regionService.getRegion("TestRegion3");

        assertEquals(3, regionService.getRegionList().size());
    }

    @Test
    public void checkSubRegionAdding() {
        regionService.getRegion("TestRegion1");
        regionService.getRegion("TestRegion2");
        regionService.getRegion("TestRegion3");

        Region newSub = regionService.getSubRegion("TestRegion1", "subRegion1");
        regionService.getSubRegion("TestRegion1", "subRegion2");

        Region subRegion = regionService.regionExistsList(regionService.getRegion("TestRegion1").getSubRegions(), "subRegion1");

        assertEquals(subRegion, newSub);
    }

    @Test
    public void checkNumSubRegions() {
        Region newReg = regionService.getRegion("TestRegion1");

        regionService.getSubRegion("TestRegion1", "subRegion1");
        regionService.getSubRegion("TestRegion1", "subRegion2");
        regionService.getSubRegion("TestRegion1", "subRegion3");

        assertEquals(3, newReg.getSubRegions().size());
    }

    @Test
    public void addSubRegionFirst() {
        regionService.getSubRegion("TestRegion1", "subRegion1");

        assertEquals(1, regionService.getRegionList().size());
    }
}
