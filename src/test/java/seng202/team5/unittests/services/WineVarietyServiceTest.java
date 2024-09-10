package seng202.team5.unittests.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import seng202.team5.models.WineVariety;
import seng202.team5.services.WineVarietyService;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WineVarietyServiceTest {

    @Test
    public void wineVarietyFromString() {
        WineVarietyService service = new WineVarietyService();
        WineVariety variety = service.varietyFromString("varietyName");
        assertEquals("varietyName", variety.getName());
    }

    @Test
    public void getSizeEmptyList(){
        WineVarietyService service = new WineVarietyService();
        assertEquals(0, service.getVarietySize());
    }

    @Test
    public void getSizeList(){
        WineVarietyService service = new WineVarietyService();
        WineVariety variety = service.varietyFromString("variety");
        assertEquals(1, service.getVarietySize());
    }

    @Test
    public void getSizeDuplicateList(){
        WineVarietyService service = new WineVarietyService();
        WineVariety variety = service.varietyFromString("variety");
        WineVariety duplicateVariety = service.varietyFromString("variety");
        assertEquals(1, service.getVarietySize());
    }

    @Test
    public void getSizeMultiItemList(){
        WineVarietyService service = new WineVarietyService();
        WineVariety variety = service.varietyFromString("variety1");
        WineVariety otherVariety = service.varietyFromString("variety2");
        assertEquals(2, service.getVarietySize());
    }

    @Test
    public void getSizeDuplicateAndMultiItemList(){
        WineVarietyService service = new WineVarietyService();
        WineVariety variety = service.varietyFromString("variety1");
        WineVariety otherVariety = service.varietyFromString("variety2");
        WineVariety samevariety = service.varietyFromString("variety1");
        WineVariety anotherVariety = service.varietyFromString("variety2");
        assertEquals(2, service.getVarietySize());
    }

}
