package seng202.team5.services;

import seng202.team5.models.Wine;

import java.util.List;

/**
 * Service Class to manage wine actions.
 */
public class WineService {

    // private DataLoadService dataService;
    // private WineDataStoreService dataStorage;

    private List<Wine> wineList;

    /**
     * Constructor.
     */
    public WineService(List<Wine> wineList) {
        this.wineList = wineList;
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
