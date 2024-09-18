package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
        dataLoadService = spy(new DataLoadService(csvFilePath));
    }

    /**
     * Test that the list given from loadfile has the correct amount of lines
     * and the expected first value.
     */
    @Test
    public void loadFileTestFirst() throws IOException {
        List<String[]> records = dataLoadService.loadFile(Files.newInputStream(
                Path.of(csvFilePath)));
        String[] expectedFirst = {"0", "Italy", "Aromas include tropical fruit, broom, "
                + "brimstone and dried herb. The palate isn't overly expressive, offering "
                + "unripened apple, citrus and dried sage alongside brisk acidity.",
                new String("Vulkà Bianco".getBytes(), StandardCharsets.UTF_8),
                "87", null, "Sicily & Sardinia",
                "Etna", null, new String("Kerin O’Keefe".getBytes(), StandardCharsets.UTF_8),
                "@kerinokeefe", new String("Nicosia 2013 Vulkà Bianco  (Etna)".getBytes(),
                                           StandardCharsets.UTF_8),
                "White Blend", "Nicosia"};
        assertEquals(16, records.size());
        assertArrayEquals(expectedFirst, records.getFirst());
    }

    /**
     * test that the first item from the list gotten from processcsv gives the correct item
     */
    @Test
    public void loadWinesTestFirst() {

        List<Wine> wines = dataLoadService.processWinesFromCsv();

        Wine wine = wines.getFirst();
        String expected = new String("Quinta dos Avidagos 2011 Avidagos Red (Douro)".getBytes(),
                StandardCharsets.UTF_8);

        String wineName = wine.getName();

        assertEquals(wineName, expected);

        assertEquals(2011, wine.getYear());
        assertEquals(87, wine.getRating());
    }

    @Test
    public void loadWinesTestInvalidPrice() {
        csvFilePath = System.getProperty("user.dir") + "/src/test/resources/test_small.csv";
        dataLoadService = new DataLoadService(csvFilePath);
        assertEquals(0, dataLoadService.processWinesFromCsv().size());
    }

    /**
     * test that the size of the list gotten from process wines is correct
     */
    @Test
    public void loadWinesSizeTest() {
        List<Wine> wines = dataLoadService.processWinesFromCsv();
        //2 have no price so 14/16 will be in the list
        assertEquals(14, wines.size());
    }
}
