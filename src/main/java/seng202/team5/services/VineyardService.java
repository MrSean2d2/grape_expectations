package seng202.team5.services;

import seng202.team5.models.Vineyard;

public class VineyardService {
    private Vineyard selectedVineyard;
    private static VineyardService instance;

    public void setSelectedVineyard(Vineyard selectedVineyard) {
        this.selectedVineyard = selectedVineyard;
    }

    public Vineyard getSelectedVineyard() {
        return selectedVineyard;
    }

    /**
     * Returns the singleton WineService instance.
     *
     * @return instance of wine service class
     */
    public static VineyardService getInstance() {
        if (instance == null) {
            instance = new VineyardService();
        }
        return instance;
    }
}
