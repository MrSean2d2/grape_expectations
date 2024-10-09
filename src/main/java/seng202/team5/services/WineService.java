package seng202.team5.services;

import java.time.Year;
import java.util.List;
import seng202.team5.models.Wine;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

/**
 * Service Class to manage wine actions.
 */
public class WineService {
    private Wine selectedWine;
    private final WineDAO wineDAO;
    private static WineService instance;

    public WineService() {
        wineDAO = new WineDAO(new VineyardDAO());
    }

    /**
     * Populates the database.
     *
     * @param dataLoadService instance
     */
    public void populateDatabase(DataLoadService dataLoadService) {
        wineDAO.truncateWines();
        wineDAO.getVineyardDAO().truncateVineyards();
        List<Wine> wines = dataLoadService.processWinesFromCsv();
        wineDAO.batchAdd(wines);
    }

    /**
     * Get list of wines from the database.
     *
     * @return Wine list
     */
    public List<Wine> getWineList() {
        List<Wine> dbWines = wineDAO.getAll();
        if (dbWines.isEmpty()) {
            populateDatabase(new DataLoadService());
            dbWines = wineDAO.getAll();
        }
        return dbWines;
    }

    /**
     * Returns the singleton WineService instance.
     *
     * @return instance of wine service class
     */
    public static WineService getInstance() {
        if (instance == null) {
            instance = new WineService();
        }
        return instance;
    }


    /**
     * Sets selectedWine to the wine clicked on in DataListPage.
     *
     * @param wine the selected wine
     */
    public void setSelectedWine(Wine wine) {
        this.selectedWine = wine;
    }

    /**
     * Returns the wine selected in DataListPage.
     *
     * @return selectedWine
     */
    public Wine getSelectedWine() {
        return selectedWine;
    }

    /**
     * Returns true if the year is valid.
     *
     * @param year the year
     * @return true if valid, false otherwise
     */
    public boolean validYear(int year) {
        int currentYear = Year.now().getValue(); //gets current year for future-proofing
        return (year >= 1700 && year <= currentYear); //1700 is arbitrary boundary
    }

    /**
     * Returns true if the name is valid.
     *
     * @param name the name
     * @return true if valid, false otherwise
     */
    public boolean validName(String name) {
        return (!name.isBlank());
    }

    /**
     * Returns true if the price is valid.
     *
     * @param price the price
     * @return true if valid, false otherwise
     */
    public boolean validPrice(double price) {
        return (price >= 0);
    }

    /**
     * Returns true if the rating is valid.
     *
     * @param rating the rating
     * @return true if valid, false otherwise
     */
    public boolean validRating(int rating) {
        return (rating >= 0 && rating <= 100);
    }

    /**
     * Returns the passed colour if it is valid, otherwise a placeholder value
     * will be returned.
     *
     * @param colour the colour to validate
     * @return the passed colour if valid, otherwise the string "Unknown"
     */
    public String validColour(String colour) {
        return (colour.isBlank()) ? "Unknown" : colour;
    }

    /**
     * Returns a valid string for the variety string given. If the string given
     * is blank, then the value returned will be a placeholder value.
     *
     * @param wineVariety the variety string to validate
     * @return the passed wineVariety if non-blank, otherwise the string
     *         "Unknown Variety"
     */
    public String validVariety(String wineVariety) {
        return (wineVariety.isBlank()) ? "Unknown Variety" : wineVariety;
    }

    /**
     * checks if a wine has valid values for its attributes.
     * eg: year and price aren't 0
     *
     * @return boolean whether given wine is valid enough to be added to database
     */
    public boolean isValidWine(Wine wine) {
        return (validName(wine.getName()) && validYear(wine.getYear())
                && validRating(wine.getRating()) && validPrice(wine.getPrice()));
    }
}
