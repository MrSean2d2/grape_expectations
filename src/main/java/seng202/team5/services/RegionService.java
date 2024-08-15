package seng202.team5.services;

import seng202.team5.models.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager class for Regions
 * @author mga138
 */
public class RegionService {
    private List<Region> regionList;

    /**
     * Constructor for RegionService
     */
    public RegionService() {
        regionList = new ArrayList<>();
    }

    /**
     * Getter for region list
     * @return List of currently available regions
     */
    public List<Region> getRegionList() { return regionList; }

    /**
     * Check if a region exists currently in the array list
     * @param checkList the list to search through
     * @param regionName the name of the region to search for
     * @return Region object if found, null otherwise
     */
    public Region regionExistsList(List<Region> checkList, String regionName) {
        return checkList.stream().filter(region -> regionName.equals(region.getName())).findFirst().orElse(null);
    }

    /**
     * Check if a region exists currently in the array list
     * @param regionName the name of the region to search for
     * @return Region object if found, null otherwise
     */
    public Region regionExists(String regionName) {
        return regionExistsList(regionList, regionName);
    }

    /**
     * Return a region if it currently exists, otherwise create a new region and return it
     * @param regionName the name of the region to search for
     * @return Found Region object
     */
    public Region getRegion(String regionName) {
        Region foundRegionObject = regionExists(regionName);

        if (foundRegionObject != null) {
            return foundRegionObject;
        } else {
            Region regionFiller = new Region(regionName);
            regionList.add(regionFiller);
            return regionFiller;
        }
    }

    /**
     * Return a subregion if it currently exists, otherwise create a new one and return it
     * @param regionName the name of the region to search for
     * @param subRegionName the name of the subregion to search for
     * @return Return the found / created subregion
     */
    public Region getSubRegion(String regionName, String subRegionName) {
        // Search for the region first (or create a new region if it doesn't exist)
        Region foundRegion = getRegion(regionName);

        // Search for SubRegion in region
        Region foundSubRegionObject = regionExistsList(foundRegion.getSubRegions(), subRegionName);

        // Check if we found the subregion in the given region list
        if (foundSubRegionObject != null) {
            return foundSubRegionObject;
        } else {
            Region subRegionFiller = new Region(subRegionName);
            foundRegion.addSubRegion(subRegionFiller);
            return subRegionFiller;
        }
    }
}
