package seng202.team5.services;

import seng202.team5.models.Vineyard;
import seng202.team5.repository.VineyardDAO;

/**
 * A service to handle looking up and adding vineyards.
 *
 * @author Sean Reitsma
 */
public class VineyardService {
    private final VineyardDAO vineyardDAO;

    public VineyardService() {
        vineyardDAO = new VineyardDAO();
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
