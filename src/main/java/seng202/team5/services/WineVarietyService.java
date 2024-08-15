package seng202.team5.services;

import seng202.team5.models.WineType;
import seng202.team5.models.WineVariety;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class WineVarietyService {

    private List<WineVariety> wineVarietyList;

    /**
     * constructor for wine variety list
     */
    public WineVarietyService() {
        wineVarietyList = new ArrayList<>();
    }

    /**
     * checks if wine variety is in list, if not, adds it to the list
     * returns value from the list or a new wine variety with unknown wine type
     */
    public WineVariety varietyFromString(String wineVarietyName) {
        return wineVarietyList.stream().filter(wineVariety -> wineVarietyName.equals(wineVariety.getName())).findFirst().orElse(new WineVariety(wineVarietyName, WineType.UNKNOWN));
    }

}
