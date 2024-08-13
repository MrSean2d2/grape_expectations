package seng202.team5.services;

import seng202.team5.models.Wine;

import java.util.List;

/**
 * Service Class to manage wine actions
 */
public class WineService {

    // private DataLoadService dataService;
    // private WineDataStoreService dataStorage;

    private List<Wine> wineList;

    /**
     * Constructor
     */
    public WineService(List<Wine> wineList){
        this.wineList = wineList;
    }


    /**
     * Get list of wines
     * @Return Wine list
     */
    public List<Wine> getWineList() {return wineList;}

    /**
     * Add new Wine entry
     */
    public void addWine(Wine wineEntry) {wineList.add(wineEntry);}

    /**
     *  Delete existing Wine entry
     */
    public void delWine(Wine wineEntry) {wineList.remove(wineEntry);}

    /**
     * Apply filter to column by input filter condition
     */
    public void filter(String colName, String filter) {}

    /**
     * Search for a wine by specified term
     */
    public void search(String term) { }
}
