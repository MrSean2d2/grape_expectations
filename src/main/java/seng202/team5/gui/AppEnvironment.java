package seng202.team5.gui;

import seng202.team5.models.Wine;
import seng202.team5.services.DataLoadService;
import seng202.team5.services.RegionService;
import seng202.team5.services.WineService;

import java.util.List;

public class AppEnvironment {
    public static DataLoadService dataService; // I made these static, we can discuss later
    public static RegionService regionService;
    public static WineService wineService;

    public AppEnvironment() {
        dataService = new DataLoadService();

        String filepath = System.getProperty("user.dir") + "/src/main/resources/test_dataset.csv";
        List<Wine> listOfWines = dataService.processWinesFromCsv(filepath);

        wineService = new WineService(listOfWines);

        regionService = new RegionService();
    }
}
