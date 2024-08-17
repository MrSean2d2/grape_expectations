package seng202.team5.services;

import java.util.List;
import seng202.team5.models.Wine;

/**
 * Service Class to manage wine actions.
 */
public class WineService {

    // private WineDataStoreService dataStorage;

    private List<Wine> wineList;

    /**
     * Constructor.
     */
    public WineService(DataLoadService dataLoadService) {
        this.wineList = dataLoadService.processWinesFromCsv();
    }


    /**
     * Get list of wines.
     *
     * @return Wine list
     */
    public List<Wine> getWineList() {
        return wineList;
    }

    /**
     * Add new Wine entry.
     */
    public void addWine(Wine wineEntry) {
        wineList.add(wineEntry);
    }

    /**
     *  Delete existing Wine entry.
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


    private Wine selectedWine;
    private static WineService instance;
    private WineService() {}

    /**
     * returns the singleton WineService instance
     * @return instance
     */
    public static WineService getInstance() {
        if (instance == null) {
            instance = new WineService();
        }
        return instance;
    }


    /**
     * Sets selectedWine to the wine clicked on in DataListPage
     * @param wine
     */
    public void setSelectedWine(Wine wine) {
        this.selectedWine = wine;
    }

    /**
     * Returns the wine selected in DataListPage
     * @return selectedWine
     */
    public Wine getSelectedWine() {
        return selectedWine;
    }

}
