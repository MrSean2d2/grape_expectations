package seng202.team5.unittests.services;

import com.opencsv.exceptions.CsvException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.gui.AppEnvironment;
import seng202.team5.models.Wine;
import seng202.team5.services.DataLoadService;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;


public class DataLoadServiceTest {
    private static final Logger log = LogManager.getLogger(DataLoadServiceTest.class);
    private DataLoadService dataLoadService;
    private String csvFilePath;

    @BeforeEach
    public void setUp() {
        csvFilePath = System.getProperty("user.dir") + "/src/test/resources/test.csv";
        dataLoadService = new DataLoadService();
        dataLoadService.externalDependencies = false;
    }

    @Test
    public void loadFileTestFirst() {
        List<String[]> records = dataLoadService.loadFile(csvFilePath);
        String[] expectedFirst = {"0","Italy","Aromas include tropical fruit, broom, brimstone and dried herb. The palate isn't overly expressive, offering unripened apple, citrus and dried sage alongside brisk acidity.",
                "Vulkà Bianco","87",null,"Sicily & Sardinia","Etna",null,"Kerin O’Keefe","@kerinokeefe","Nicosia 2013 Vulkà Bianco  (Etna)","White Blend","Nicosia"};
        assertEquals(16, records.size());
        assertArrayEquals(expectedFirst, records.getFirst());
    }

    @Test
    public void convertWineYear() {

    }

    @Test
    public void loadWinesTestFirst() {
        List<Wine> wines = dataLoadService.processWinesFromCsv(csvFilePath);
        assertEquals(16, wines.size());
        Wine wine = wines.getFirst();
        // Multiple asserts to check wine equality because Wine.equals() isn't implemented yet
        assertEquals("Nicosia 2013 Vulkà Bianco  (Etna)", wine.getName());
        assertEquals(2013, wine.getYear());
        assertEquals(87, wine.getRating());
    }
}
