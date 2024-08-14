package seng202.team5.services;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import seng202.team5.models.Wine;

/**
 * A service to load wine data from csv files.
 *
 * @author Sean Reitsma
 */
public class DataLoadService {
    private Wine wineFromText(String[] csvEntry) {
        //TODO: method stub
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private String textFromWine(Wine wine) {
        //TODO: method stub
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Reads the specified file and returns all the csv records as a list of String[]s.
     * @param fileName the URI of the file to read
     * @return all csv records from the file as a list of String[]s
     * @throws IOException if there is an error reading the file
     * @throws CsvException if there is a csv validation error
     */
    public List<String[]> loadFile(String fileName) throws IOException, CsvException {
        try (Reader reader = Files.newBufferedReader(Paths.get(fileName))){
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').withQuoteChar('"').build();
            try (CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).withCSVParser(csvParser).build()){
                return csvReader.readAll();
            }
        }
    }

    /**
     * Reads a csv file and returns all valid wines present in a single list.
     * @param fileName the path of the csv file to read
     * @return a list of wines from the csv file
     */
    public List<Wine> processWinesFromCsv(String fileName) {
        //TODO: method stub
        throw new UnsupportedOperationException("Not implemented yet");
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
