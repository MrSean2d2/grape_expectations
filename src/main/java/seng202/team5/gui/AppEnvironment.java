package seng202.team5.gui;

import seng202.team5.services.DataLoadService;
import seng202.team5.services.RegionService;
import seng202.team5.services.WineService;
import seng202.team5.services.WineVarietyService;

/**
 * A class to manage application state and hold references to instances of all the
 * various services.
 */
public class AppEnvironment {
    public DataLoadService dataService; // I made these static, we can discuss later
    public RegionService regionService;
    public WineService wineService;
    public WineVarietyService wineVarietyService;

    /**
     * Constructor, initialises an instance of AppEnvironment. There should only
     * be one instance of AppEnvironment in use.
     */
    public AppEnvironment() {
        wineVarietyService = new WineVarietyService();
        regionService = new RegionService();
        String filepath = System.getProperty("user.dir") + "/src/main/resources/test_dataset.csv";
        dataService = new DataLoadService(filepath, wineVarietyService, regionService);
        wineService = new WineService(dataService);
    }
}
