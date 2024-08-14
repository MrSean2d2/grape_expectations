package seng202.team5.models;

import java.util.ArrayList;

/**
 * Model class for Region attribute of wines
 */
public class Region {
    private String name;
    //private String description;
    // description removed because it is not included in the csv
    private ArrayList<Region> subRegions;
    private ArrayList<Vineyard> vineyards;

    /**
     * Constructor
     */
    public Region(String name, ArrayList<Region> subRegions) {
        this.name = name;
        //this.description = description;
        this.subRegions = subRegions;
    }

    /**
     * Get subregions of region
     * @return List of region's subregions
     */
    ArrayList<Region> getSubRegions(){
        return subRegions;
    }
    /**
     * Get name of region
     * @return String of region name
     */
    public String getName(){return name;
    }

    /**
     * Add new subregion to region
     */
    public void addSubRegion(Region region){
        subRegions.add(region);
    }
}
