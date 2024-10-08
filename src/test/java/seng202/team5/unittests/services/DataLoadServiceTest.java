package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.models.Wine;
import seng202.team5.services.DataLoadService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

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
        String[] expectedFirst = {"174", "New Zealand", "The Stoneleigh style traditionally favors ripeness over herbaceousness, and the 2008 holds true to form," +
                " offering up grapefruit and nectarine aromas and an appealing blend of citrus and stone-fruit flavors. " +
                new String("It's plump and round, yet finishes fresh. Good as an apéritif or with light dishes.".getBytes(), StandardCharsets.UTF_8), null,
                "88", "19", "Marlborough",
                null, null, "Joe Czerwinski",
                "@JoeCz", "Stoneleigh 2008 Sauvignon Blanc (Marlborough)",
                "Sauvignon Blanc", "Stoneleigh", null};
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
        String expected = new String("Stoneleigh 2008 Sauvignon Blanc (Marlborough)".getBytes(),
                StandardCharsets.UTF_8);

        String wineName = wine.getName();

        assertEquals(wineName, expected);

        assertEquals(2008, wine.getYear());
        assertEquals(88, wine.getRating());
    }

    /**
     * test that a wine is not made from an object with invalid price attribute
     */
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
        assertEquals(16, wines.size());
    }

    @Test
    public void testColourPinotNoirWine() {
        List<Wine> wines = dataLoadService.processWinesFromCsv();
        Wine wine = wines.get(4);
        assertEquals("Pinot Noir", wine.getWineVariety());
        assertEquals("Red", wine.getWineColour());
    }

    @Test
    public void testColourSauvignonBlancWine() {
        List<Wine> wines = dataLoadService.processWinesFromCsv();
        Wine wine = wines.get(2);
        assertEquals("Sauvignon Blanc", wine.getWineVariety());
        assertEquals("White", wine.getWineColour());
    }

//    @Test
//    public void testUnknownColourWine() {//colours csv only contains those from nz data so some in test data are unknown
//        List<Wine> wines = dataLoadService.processWinesFromCsv();
//        Wine wine = wines.get(4);
//        for (Wine fwine : wines) {
//            System.out.println(fwine.getWineColour());
//        }
//        assertEquals("Tempranillo-Merlot", wine.getWineVariety());
//        assertEquals("Unknown", wine.getWineColour());
//    }


}
