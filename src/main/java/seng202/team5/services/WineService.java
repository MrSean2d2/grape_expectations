package seng202.team5.services;

import java.util.List;
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
        wineDAO.truncateWines();
        wineDAO.getVineyardDAO().truncateVineyards();
        List<Wine> wines = dataLoadService.processWinesFromCsv();
        wineDAO.batchAdd(wines);
    }

    /**
     * Get list of wines.
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


}
