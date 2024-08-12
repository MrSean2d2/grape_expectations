package seng202.team5.models;

import seng202.team5.Vineyard;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for Region attribute of wines
 */
public class Region {
    private String name;
    private String description;
    private ArrayList<Region> subRegions;
    private ArrayList<Vineyard> vineyards;

    /**
     * Constructor
     */
    public Region(String name, String description, ArrayList<Region> subRegions){
        this.name = name;
        this.description = description;
        this.subRegions = subRegions;
    }
    /**
     * Get parent of region
     * @Return Region parent
     */
//    public Region getParentRegion(){}
    /**
     * Get subregions of region
     * @Return List of region's subregions
     */
    ArrayList<Region> getSubRegions(){return subRegions;}
    /**
     * Get name of region
     * @Return String of region name
     */
    public String getName(){ return name;}
    /**
     * Get description of region
     * @Return String of region description
     */
    public String getDescription(){return description;}


    /**
     * Add new subregion to region
     */
    public void addSubRegion(Region region){subRegions.add(region);}
}