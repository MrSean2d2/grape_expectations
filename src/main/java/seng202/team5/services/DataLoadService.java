package seng202.team5.services;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;


/**
 * A service to load wine data from csv files.
 *
 * @author Sean Reitsma
 */
public class DataLoadService {
    private static final Logger log = LogManager.getLogger(DataLoadService.class);

    private final String fileName;

    /**
     * DataLoadService constructor.
     *
     */
    public DataLoadService() {
        this.fileName = parseFileName();
    }

    private String parseFileName() {
        Yaml yaml = new Yaml();
        String configPath = this.getClass().getProtectionDomain()
                .getCodeSource().getLocation().getPath();
        configPath = URLDecoder.decode(configPath, StandardCharsets.UTF_8);
        File jarDir = new File(configPath);
        configPath = jarDir.getParentFile() + "/config.yaml";
        try (InputStream inputStream = Files.newInputStream(Paths.get(configPath))) {
            Map<String, Object> obj = yaml.load(inputStream);
            Object result = obj.get("csvPath");
            if (result instanceof String) {
                return (String) result;
            } else {
                log.error("Unable to parse config file, 'csvPath' value is not a string");
                return this.getClass().getClassLoader().getResource("nzcsv.csv").getPath();
            }
        } catch (IOException e) {
            log.error(e);
            return this.getClass().getClassLoader().getResource("nzcsv.csv").getPath();
        }
    }

    /**
     * Creates a wine object from a csv entry parsed by {@link DataLoadService#loadFile(String)}.
     *
     * @param csvEntry a String[] representing one csv record
     * @return the new wine object
     */
    private Wine wineFromText(String[] csvEntry) {
        try {

            //String country = csvEntry[1];

            // Wine Description
            // description can be empty
            String description = csvEntry[2];

            // Wine Rating
            if (csvEntry[4] == null) {
                throw new Exception();
            }
            int ratingValue = numFromTextOr0(csvEntry[4]);

            // Wine Price
            if (csvEntry[5] == null) {
                //price is not in csv
                throw new Exception();
            }
            double price = numFromTextOr0(csvEntry[5]);


            // Wine Region
            String regionName = csvEntry[6] != null ? csvEntry[6] : "NoRegion";

            // Wine Name
            String name = csvEntry[11];


            // Wine Year
            Pattern yearPattern = Pattern.compile("\\d{4}");
            Matcher yearMatcher = yearPattern.matcher(csvEntry[11]);
            boolean matchFound = yearMatcher.find();
            int year = matchFound ? numFromTextOr0(yearMatcher.group()) : 0;
            if (year == 0) {
                //year was not in csv
                throw new Exception();
            }

            // Wine Variety
            String varietyName = csvEntry[12];

            // Winery
            String winery = csvEntry[13];
            Vineyard vineyard = new Vineyard(winery, regionName);

            // Return the created Wine object
            Wine resultWine = new Wine(name, description, year, ratingValue, price,
                    varietyName, "Unknown", vineyard);
            if (resultWine.isValidWine()) {
                return resultWine;
            } else {
                return null;
            }
        } catch (Exception e) {
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
     * @param filePath the URI of the file to read
     * @return all csv records from the file as a list of String[]s
     */
    public List<String[]> loadFile(String filePath) {
        List<String[]> result;
        try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
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
     * @return a list of wines from the csv file
     */
    public List<Wine> processWinesFromCsv() {
        List<String[]> csvResult = loadFile(fileName);
        List<Wine> wines = new ArrayList<>();
        for (String[] entry : csvResult) {
            Wine wine = wineFromText(entry);
            if (wine != null) {
                wines.add(wine);
            }
        }
        return wines;
    }
}
