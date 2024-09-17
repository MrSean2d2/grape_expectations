package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.models.Wine;
import seng202.team5.services.DataLoadService;
import seng202.team5.services.WineService;

public class DataLoadServiceTest {
    private DataLoadService dataLoadService;
    private String csvFilePath;
    private WineService wineservice;

    @BeforeEach
    public void setUp() {
        wineservice = WineService.getInstance();
        csvFilePath = System.getProperty("user.dir") + "/src/test/resources/test.csv";
        dataLoadService = spy(new DataLoadService());
    }

    @Test
    public void loadFileTestFirst() {
        List<String[]> records = dataLoadService.loadFile(csvFilePath);
        String[] expectedFirst = {"0", "Italy", "Aromas include tropical fruit, broom, brimstone and dried herb. The palate isn't overly expressive, offering unripened apple, citrus and dried sage alongside brisk acidity.",
                new String("Vulkà Bianco".getBytes(), StandardCharsets.UTF_8), "87", null, "Sicily & Sardinia",
                "Etna", null, new String("Kerin O’Keefe".getBytes(), StandardCharsets.UTF_8),
                "@kerinokeefe", new String("Nicosia 2013 Vulkà Bianco  (Etna)".getBytes(), StandardCharsets.UTF_8),
                "White Blend", "Nicosia"};
        assertEquals(16, records.size());
        assertArrayEquals(expectedFirst, records.getFirst());
    }

//    @Test
//    public void loadWinesTestFirst() {
//        List<Wine> wines = wineservice.getWineList();
//        System.out.println(wines.size());
//
//        List<Wine> wines = dataLoadService.processWinesFromCsv();
//
//        Wine wine = wines.getFirst();
//        String expected = new String("Quinta dos Avidagos 2011 Avidagos Red (Douro)".getBytes(), StandardCharsets.UTF_8);
//
//        String wineName = wine.getName();
//
//        assertEquals(wineName, expected);
//
//        assertEquals(2013, wine.getYear());
//        assertEquals(87, wine.getRating());
//    }

//    @Test
//    public void loadWinesSizeTest() {
//        List<Wine> wines = dataLoadService.processWinesFromCsv();
//        assertEquals(16, wines.size());
//    }
}
