package seng202.team5.services;

import seng202.team5.models.Vineyard;
import seng202.team5.repository.VineyardDAO;

/**
 * A service to handle looking up and adding vineyards.
 *
 * @author Amiele Miguel
 * @author Sean Reitsma
 */
public class VineyardService {
    private final VineyardDAO vineyardDAO;
    private Vineyard selectedVineyard;
    private static VineyardService instance;

    private VineyardService() {
        vineyardDAO = new VineyardDAO();
    }

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

    /**
     * Retrieve a vineyard from the database if it exists, otherwise add it.
     *
     * @param name the name of the vineyard
     * @param region the region the vineyard is located in
     * @return the Vineyard object
     * @throws IllegalArgumentException if the name is blank
     */
    public Vineyard retreiveVineyard(String name, String region) throws IllegalArgumentException {
        Vineyard vineyard;
        if (!name.isBlank()) {
            int vineyardId = vineyardDAO.getIdFromNameRegion(name, region);
            if (vineyardId == 0) {
                vineyard = new Vineyard(name, region);
                vineyard.setId(vineyardDAO.add(vineyard));
            } else {
                vineyard = vineyardDAO.getOne(vineyardId);
            }
        } else {
            throw new IllegalArgumentException("Vineyard name is blank");
        }
        return vineyard;
    }
}
