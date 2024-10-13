package seng202.team5.services;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.exceptions.InvalidCsvEntryException;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;


/**
 * A service to load wine data from csv files.
 *
 * @author Sean Reitsma
 */
public class DataLoadService {
    private static final Logger log = LogManager.getLogger(DataLoadService.class);

    private final WineService wineService;

    private final Path fileName;

    private final GeolocatorService geolocatorService = new GeolocatorService();

    /**
     * DataLoadService constructor.
     */
    public DataLoadService() {
        this.fileName = null;
        this.wineService = WineService.getInstance();
    }

    /**
     * DataLoadService constructor with string of file path
     * used for testing with separate csv.
     *
     * @param specifiedFileName the name of the file to load
     */
    public DataLoadService(String specifiedFileName) {
        this.fileName = Path.of(specifiedFileName);
        this.wineService = WineService.getInstance();
    }

    /**
     * Creates a wine object from a csv entry parsed by
     * {@link DataLoadService#loadFile(InputStream)}.
     *
     * @param csvEntry a String[] representing one csv record
     * @return the new wine object
     */
    private Wine wineFromText(String[] csvEntry, boolean loadGeolocation) {
        try {
            // Wine Rating
            if (csvEntry[4] == null) {
                throw new InvalidCsvEntryException("Invalid rating");
            }

            // Wine Price
            if (csvEntry[5] == null) {
                //price is not in csv
                throw new InvalidCsvEntryException("Invalid price");
            }
            double price = numFromTextOr0(csvEntry[5]);

            // Wine Region
            String regionName = csvEntry[6] != null ? csvEntry[6] : "NoRegion";

            // Wine Name
            String name = csvEntry[11];
            int year;
            if (name != null && wineService.validName(name)) {
                // Wine Year
                Pattern yearPattern = Pattern.compile("\\d{4}");
                Matcher yearMatcher = yearPattern.matcher(name);
                boolean matchFound = yearMatcher.find();
                year = matchFound ? numFromTextOr0(yearMatcher.group()) : 0;
                if (year == 0) {
                    //year was not in csv
                    throw new InvalidCsvEntryException("Invalid year");
                }
            } else {
                throw new InvalidCsvEntryException("Invalid name");
            }

            // Wine Variety
            String varietyName = wineService.validVariety(csvEntry[12]);

            // Winery
            String winery = csvEntry[13];
            Vineyard vineyard = new Vineyard(winery, regionName);
            if (loadGeolocation) {
                geolocatorService.queryAddress(vineyard);
            }

            // Wine Description - description can be empty
            String description = csvEntry[2];
            int ratingValue = numFromTextOr0(csvEntry[4]);

            // Return the created Wine object
            Wine resultWine = new Wine(name, description, year, ratingValue, price,
                    varietyName, "Unknown", vineyard);

            // Check if the new wine is valid and return it
            if (wineService.isValidWine(resultWine)) {
                return resultWine;
            } else {
                return null;
            }
        } catch (InvalidCsvEntryException e) {
            return null;
        }
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

    /**
     * Reads the specified file and returns all the csv records as a list of String[]s.
     *
     * @param fileInputStream the InputStream of the file to read
     * @return all csv records from the file as a list of String[]s
     */
    public List<String[]> loadFile(InputStream fileInputStream) {
        List<String[]> result;
        try (Reader reader = new BufferedReader(
                new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))) {
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
     * @param loadGeolocation whether the geolocation should be loaded
     * @return a list of wines from the csv file
     */
    public List<Wine> processWinesFromCsv(boolean loadGeolocation) {
        List<String[]> csvResult = new ArrayList<>();
        try (InputStream fileInputStream = (fileName != null)
                ? Files.newInputStream(fileName) : this.getClass().getResourceAsStream("/nz.csv")) {
            csvResult = loadFile(fileInputStream);
        } catch (IOException e) {
            log.error(e);
        }

        List<String[]> wineColourCsv = new ArrayList<>();
        try (InputStream colourInputStream = this.getClass().getResourceAsStream(
                "/wineColour_NZ_list.csv")) {
            wineColourCsv = loadFile(colourInputStream);
        } catch (IOException e) {
            log.error(e);
        }

        List<Wine> wines = new ArrayList<>();
        for (String[] entry : csvResult) {
            Wine wine = wineFromText(entry, loadGeolocation);
            if (wine != null) {

                for (String[] strings : wineColourCsv) {
                    if (strings[0].equals(wine.getWineVariety())) {
                        wine.setColour(strings[1]);
                        break;
                    }
                }
                wines.add(wine);
            }
        }
        return wines;
    }
}
