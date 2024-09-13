package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.models.Wine;
import seng202.team5.services.DataLoadService;

public class DataLoadServiceTest {
    private DataLoadService dataLoadService;
    private String csvFilePath;

    @BeforeEach
    public void setUp() {
        csvFilePath = System.getProperty("user.dir") + "/src/test/resources/test.csv";
        dataLoadService = new DataLoadService(csvFilePath
        );
        dataLoadService.externalDependencies = false;
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

    @Test
    public void loadWinesTestFirst() {
        List<Wine> wines = dataLoadService.processWinesFromCsv();
        Wine wine = wines.getFirst();
        // Multiple asserts to check wine equality because Wine.equals() isn't implemented yet
        String expected = new String("Nicosia 2013 Vulkà Bianco  (Etna)".getBytes(), StandardCharsets.UTF_8);

        String wineName = wine.getName();

        assertEquals(wineName, expected);

        assertEquals(2013, wine.getYear());
        assertEquals(87, wine.getRating());
    }

    @Test
    public void loadWinesSizeTest() {
        List<Wine> wines = dataLoadService.processWinesFromCsv();
        assertEquals(16, wines.size());
    }
}
