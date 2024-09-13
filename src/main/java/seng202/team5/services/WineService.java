package seng202.team5.services;

import java.util.List;
import seng202.team5.gui.AppEnvironment;
import seng202.team5.models.Wine;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

/**
 * Service Class to manage wine actions.
 */
public class WineService {
    private List<Wine> wineList;
    private Wine selectedWine;
    private final WineDAO wineDAO;
    private static WineService instance;

    private WineService() {
        wineDAO = new WineDAO(new VineyardDAO());
    }

    public void populateDatabase(DataLoadService dataLoadService) {
        List<Wine> wines = dataLoadService.processWinesFromCsv();
        wineDAO.batchAdd(wines);
    }

    /**
     * Get list of wines.
     *
     * @return Wine list
     */
    public List<Wine> getWineList() {
        return wineDAO.getAll();
    }

    /**
     * Add new Wine entry.
     */
    public void addWine(Wine wineEntry) {
        wineList.add(wineEntry);
    }

    /**
     * Delete existing Wine entry.
     */
    public void delWine(Wine wineEntry) {
        wineList.remove(wineEntry);
    }

    /**
     * Apply filter to column by input filter condition.
     */
    public void filter(String colName, String filter) {

    }

    /**
     * Search for a wine by specified term.
     */
    public void search(String term) {

    }

    /**
     * Returns the singleton WineService instance.
     *
     * @return instance
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
     * @param wine the
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

//    public String buildSearchQuery(String search) {
//        // select * from wine where name LIKE '%Red%' or description like '%Red%'
//    }

//    public List<Wine> getQueryResults() {
//
//    }
}
