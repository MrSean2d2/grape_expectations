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
     * Default Constructor
     *
     * @param name The name of the region
     */
    public Region(String name) {
        this.name = name;
        //this.description = description;
        this.subRegions = new ArrayList<>();
        this.vineyards = new ArrayList<>();
    }

    /**
     * Constructor
     *
     * @param name The name of the region
     * @param subRegions a list of subregions
     * @param vineyards a list of vineyards
     */
    public Region(String name, ArrayList<Region> subRegions, ArrayList<Vineyard> vineyards) {
        this.name = name;
        //this.description = description;
        this.subRegions = subRegions;
        this.vineyards = vineyards;
    }

    /**
     * Get subregions of region
     * @return List of region's subregions
     */
    public ArrayList<Region> getSubRegions(){
        return subRegions;
    }
    /**
     * Get name of region
     * @return String of region name
     */
    public String getName(){ return name; }

    /**
     * Add new subregion to region
     */
    public void addSubRegion(Region region){
        subRegions.add(region);
    }

    /**
     * get vineyards of region
     */
    public ArrayList getVineyards() { return vineyards; }

    /**
     * add vineyard to list of vineyards in region
     *
     * @param vineyard the vineyard to add to the list
     */
    public void addVineyard(Vineyard vineyard) { vineyards.add(vineyard); }
}
