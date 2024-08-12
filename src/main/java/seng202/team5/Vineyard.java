package seng202.team5;
public class Vineyard {
    private String name;
    private String description;//is a description given in the dataset?
    // the following were not in the uml class diagram but might be necessary for the map page
    //private String region;
    //private String subRegion;
    //private String province;

    public Vineyard(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName(Vineyard vineyard) {return name;}
    public String getDescription(Vineyard vineyard) {return description;}
}
