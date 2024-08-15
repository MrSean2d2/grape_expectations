package seng202.team5.models;

public class Wine {
    private String name = null;
    private String description = null;
    private int year = 0;
    private int ratingValue = 0;
    private double price = 0.0;
    //private boolean favourite = false;
    private WineVariety wineVariety;
    private Region region;
    private Vineyard vineyard;

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getYear() { return year; }
    public double getRating() { return ratingValue; }
    //public boolean isFavourite() { return favourite; }
    // removed favourite because it is individual to each user, not wine
    public WineVariety getWineVariety() { return wineVariety; }
    public Region getRegion() { return region; }
    public Vineyard getVineyard() { return vineyard; }

    public Wine(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public Wine(String name, String description, int year, int ratingValue, double price, WineVariety wineVariety, Region region, Vineyard vineYard) {
        this.name = name;
        this.description = description;

        this.year = year;
        this.ratingValue = ratingValue;
        this.price = price;
        //this.favourite = favourite;
        this.wineVariety = wineVariety;
        this.region = region;
        this.vineyard = vineYard;
    }
}
