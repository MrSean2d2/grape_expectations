package seng202.team5.services;

import seng202.team5.models.Vineyard;

/**
 * Service class for vineyard actions.
 */
public class VineyardService {
    private Vineyard selectedVineyard;
    private static VineyardService instance;

    /**
     * Sets selected vineyard.
     *
     * @param selectedVineyard vineyard selected in map page
     */
    public void setSelectedVineyard(Vineyard selectedVineyard) {
        this.selectedVineyard = selectedVineyard;
    }

    /**
     * Gets the selected vineyard.
     *
     * @return selected vineyard
     */
    public Vineyard getSelectedVineyard() {
        return selectedVineyard;
    }

    /**
     * Returns the singleton VineyardService instance.
     *
     * @return instance of vineyard service class
     */
    public static VineyardService getInstance() {
        if (instance == null) {
            instance = new VineyardService();
        }
        return instance;
    }
}
