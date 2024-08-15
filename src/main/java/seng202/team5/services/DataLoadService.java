package seng202.team5.services;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.gui.AppEnvironment;
import seng202.team5.gui.MainWindow;
import seng202.team5.models.*;


/**
 * A service to load wine data from csv files.
 *
 * @author Sean Reitsma
 */
public class DataLoadService {
    private static final Logger log = LogManager.getLogger(DataLoadService.class);

    /**
     * Creates a wine object from a csv entry parsed by {@link DataLoadService#loadFile(String)}.
     *
     * @param csvEntry a String[] representing one csv record
     * @return the new wine object
     */
    private Wine wineFromText(String[] csvEntry) {
        //String country = csvEntry[1];

        // Wine Description
        String description = csvEntry[2];

        // Wine Rating
        int ratingValue = numFromTextOr0(csvEntry[4]);

        // Wine Price
        double price = numFromTextOr0(csvEntry[5]);

        // Wine Region
        String regionName = csvEntry[7] != null ? csvEntry[7] : "NoRegion";
        String subRegionName = csvEntry[8] != null ? csvEntry[8] : "NoSubRegion";

        Region region = AppEnvironment.regionService.getSubRegion(regionName, subRegionName);

        // Wine Name
        String name = csvEntry[11];

        // Wine Year
        Pattern yearPattern = Pattern.compile("\\d{4}");
        Matcher yearMatcher = yearPattern.matcher(csvEntry[11]);
        boolean matchFound = yearMatcher.find();
        int year = matchFound ? numFromTextOr0(yearMatcher.group()) : 0;

        // Wine Variety
        String varietyName = csvEntry[12];
        WineVariety variety = AppEnvironment.wineVarietyService.varietyFromString(varietyName);

        // Winery
        String winery = csvEntry[13];
        Vineyard vineyard = new Vineyard(winery);

        // Return the created Wine object
        return new Wine(name, description, year, ratingValue, price, variety, region, vineyard);
    }

    /**
     * Reads the specified file and returns all the csv records as a list of String[]s.
     *
     * @param text the string to be parsed
     * @return parsed integer from input string (if string is null, returns 0)
     * @throws NumberFormatException if there is an error parsing
     */
    private int numFromTextOr0(String text) {
        int num;
        if (text != null) {
            try {
                num = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                log.error(e);
                num = 0;
            }
        } else {
            num = 0;
        }
        return num;
    }

    private String textFromWine(Wine wine) {
        //TODO: method stub
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Reads the specified file and returns all the csv records as a list of String[]s.
     *
     * @param fileName the URI of the file to read
     * @return all csv records from the file as a list of String[]s
     * @throws IOException if there is an error reading the file
     * @throws CsvException if there is a csv validation error
     */
    public List<String[]> loadFile(String fileName) {
        List<String[]> result;
        try (Reader reader = Files.newBufferedReader(Paths.get(fileName))) {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',')
                    .withQuoteChar('"')
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).build();
            try (CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1)
                    .withCSVParser(csvParser).build()) {
                result = csvReader.readAll();
            }
        } catch (IOException | CsvException e) {
            log.error(e);
            result = new ArrayList<>();
        }
        return result;
    }

    /**
     * Reads a csv file and returns all valid wines present in a single list.
     *
     * @param fileName the path of the csv file to read
     * @return a list of wines from the csv file
     */
    public List<Wine> processWinesFromCsv(String fileName) {
        List<String[]> csvResult = loadFile(fileName);
        List<Wine> wines = new ArrayList<>();
        for (String[] entry : csvResult) {
            Wine wine = wineFromText(entry);
            wines.add(wine);
        }
        return wines;

    }

    public void overwriteWine(String fileName, Wine wine) {
        //TODO: method stub
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void appendWine(String fileName, Wine wine) {
        //TODO: method stub
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
