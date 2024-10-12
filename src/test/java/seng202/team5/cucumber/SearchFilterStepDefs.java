package seng202.team5.cucumber;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import org.junit.Assert;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.models.*;
import seng202.team5.repository.*;
import seng202.team5.services.DataLoadService;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.UserService;
import seng202.team5.services.WineService;

/**
 * Basic step definitions for filter feature
 */

public class SearchFilterStepDefs {
    private static WineDAO wineDAO;
    private List<Wine> filteredWines;
    private static WineService wineService;
    private TagsDAO tagsDAO;

    @Given("the user is on the base search page,")
    public void theUserIsOnTheBaseSearchPage() throws InstanceAlreadyExistsException {
        if (wineDAO == null) {

            VineyardDAO vineyardDAO = new VineyardDAO();
            wineDAO = new WineDAO(vineyardDAO);
            wineService = new WineService();

            DatabaseService.removeInstance();
            DatabaseService databaseService = DatabaseService.initialiseInstanceWithUrl(
                    "jdbc:sqlite:./src/test/resources/test.db");
            databaseService.resetDb();

            String csvFilePath = System.getProperty("user.dir") + "/src/test/resources/test.csv";
            DataLoadService dataLoadService = new DataLoadService(csvFilePath);
            List<Wine> testWineList = dataLoadService.processWinesFromCsv(false);
            wineDAO.batchAdd(testWineList);
        }

    }

    @When("the user applies a year {int} filter")
    public void theUserAppliesYearFilter(int year) {
        String query =
                wineDAO.queryBuilder("0", "0", "0", "0",
                        String.valueOf(year), 0.0, 800.0, 0.0);
        filteredWines = wineDAO.executeSearchFilter(query, "");
    }

    @Then("the system displays results of wine entries from {int}.")
    public void theSystemDisplaysResultsOfWineEntriesFrom(int year) {
        for (Wine wine : filteredWines) {
            Assert.assertEquals(year, wine.getYear());
        }
    }

    @When("the user searches for {string} in the search bar")
    public void theUserSearchesForInTheSearchBar(String search) {
        String query = wineDAO.queryBuilder(search, "0", "0", "0", "0", 0.0, 800.0, 0.0);
        filteredWines = wineDAO.executeSearchFilter(query, search);
    }

    @Then("the system displays a filtered dataset which only contains {string} wine entries")
    public void theSystemDisplaysFilteredDatasetWhichOnlyContainsWineEntries(String search) {
        for (Wine wine : filteredWines) {
            Assert.assertTrue(
                    wine.getName().contains(search) || wine.getDescription().contains(search));
        }
    }

    @When("the user inputs non existent search {string}")
    public void theUserInputs(String nonExistentSearch) {
        String query =
                wineDAO.queryBuilder(nonExistentSearch, "0", "0", "0", "0", 0.0, 800.0, 0.0);
        filteredWines = wineDAO.executeSearchFilter(query, nonExistentSearch);
    }

    @Then("the system displays no entries in table")
    public void theSystemDisplaysNoEntriesInTable() {
        Assert.assertEquals(0, filteredWines.size());
    }

    @When("the user applies a variety filter {string}")
    public void theUserAppliesVarietyFilter(String variety) {
        String query = wineDAO.queryBuilder("0", variety, "0", "0", "0", 0.0, 800.0, 0.0);
        filteredWines = wineDAO.executeSearchFilter(query, "");
    }

    @Then("the system displays results of wine entries of the variety {string}")
    public void theSystemDisplaysResultsOfWineEntriesOfTheVarietyChardonnay(String variety) {
        for (Wine wine : filteredWines) {
            Assert.assertEquals(variety, wine.getWineVariety());
        }
    }


    @When("the user applies a minimum price range {int} and maximum price {int}")
    public void theUserAppliesMinimumPriceRangeAndMaximumPrice(int minPrice, int maxPrice) {
        String query =
                wineDAO.queryBuilder("0", "0", "0", "0", "0", minPrice, maxPrice, 0.0);
        filteredWines = wineDAO.executeSearchFilter(query, "");

    }

    @Then("the system displays results of wine entries of price between {int} and {int}")
    public void theSystemDisplaysResultsOfWineEntriesOfPriceBetweenAnd(int minPrice, int maxPrice) {
        for (Wine wine : filteredWines) {
            double price = wine.getPrice();
            Assert.assertTrue(price >= minPrice && price <= maxPrice);
        }
    }

    @When("the user applies a {int} year filter minimum rating filter of {int}.")
    public void theUserAppliesaYearFilterMinimumRatingFilterOf(int year, int rating) {
        String query =
                wineDAO.queryBuilder("0", "0", "0", "0", String.valueOf(year),
                        0, 800.0, rating);
        filteredWines = wineDAO.executeSearchFilter(query, "");
    }

    @Then("the system displays only wines that from {int} rate {int}.")
    public void theSystemDisplaysOnlyWinesThatFromRate(int year, int minRating) {
        for (Wine wine : filteredWines) {
            Assert.assertEquals(year, wine.getYear());
            Assert.assertTrue(wine.getRating() >= minRating);
        }
    }

    @When("the user applies filters variety {string}, colour {string}, region name {string},"
            + " year  {int}, min price {int}, max price {int},"
            + " min rating {int}, search bar {string}.")
    public void theUserAppliesFiltersVarietyRegionNameYearMinPriceMaxPriceMinRatingSearchBar(
            String variety, String colour, String region, int year,
            int minPrice, int maxPrice, int minRating, String search) {
        String query = wineDAO.queryBuilder(search, variety, colour,  region,
                String.valueOf(year), minPrice, maxPrice, minRating);
        filteredWines = wineDAO.executeSearchFilter(query, "");
    }

    @Then("the system displays results of wines with Variety {string}, "
            + "colour {string}, Region {string}, Year {int}, "
            + "Price between {int} and {int}, Rating of {int} or higher, "
            + "and containing {string} in the name")
    public void theSystemDisplays(
            String variety, String colour, String region,
            int year, int minPrice, int maxPrice, int minRating,
            String search) {
        for (Wine wine : filteredWines) {
            Assert.assertEquals(wine.getWineVariety(), variety);
            Assert.assertEquals(wine.getWineColour(), colour);
            Assert.assertEquals(wine.getRegion(), region);
            Assert.assertEquals(wine.getYear(), year);
            Assert.assertTrue(wine.getPrice() >= minPrice && wine.getPrice() <= maxPrice);
            Assert.assertTrue(wine.getRating() >= minRating);
            Assert.assertTrue(
                    wine.getName().contains(search) || wine.getDescription().contains(search));
        }
    }


    @When("the user applies filters variety {string} and region {string}")
    public void theUserAppliesFiltersVarietyAndRegion(String variety, String region) {
        String query = wineDAO.queryBuilder("", variety, "0", region, "0", 0.0, 800.0, 0);
        filteredWines = wineDAO.executeSearchFilter(query, "");
    }

    @Then("no entries are shown")
    public void noEntriesAreShown() {
        Assert.assertEquals(0, filteredWines.size());
    }
    @When("the user applies a colour filter {string}")
    public void theUserAppliesAColourFilter(String colour) {
        String query = wineDAO.queryBuilder("","0", colour,"0","0", 0.0, 800.0, 0);
        filteredWines = wineDAO.executeSearchFilter(query, "");
    }

    @Then("the system displays results of only wine entries with colour {string}")
    public void theSystemDisplaysResultsOfOnlyWineEntriesWithColour(String colour) {
        for (Wine wine : filteredWines) {
            Assert.assertEquals(wine.getWineColour(), colour);
        }
        Assert.assertFalse(filteredWines.isEmpty());
    }
    @And("the user has tagged {int} wines as {string},")
    public void theUserHasTaggedWinesAs(int numWines, String tag)  throws DuplicateEntryException {
        User wineEnthusiast = new User(1, "username", "password12@", Role.USER, 1);
        UserService.getInstance().setCurrentUser(wineEnthusiast);
        ReviewDAO reviewDAO = new ReviewDAO();
        tagsDAO = new TagsDAO();
        AssignedTagsDAO assignedTagsDAO = new AssignedTagsDAO();


        for (int i = 1; i < numWines + 1; i++) {
            Review wineReview = new Review(i, wineEnthusiast.getId());
            AssignedTag assignedTag = new AssignedTag(tagsDAO.getIdFromName(tag, wineEnthusiast.getId()),
                    wineEnthusiast.getId(), i);
            assignedTagsDAO.add(assignedTag);
            reviewDAO.add(wineReview);
        }
    }

    @When("the user applies the tag filter {string}")
    public void theUserAppliesTheTagFilter(String tag) {
        System.out.println("tag is :"+tag);
        wineService.filterWinesByTag(tag);
        System.out.println("size is:"+wineService.getWineList().size());
    }

    @Then("the system displays the {int} reviewed wines of tag {string}")
    public void theSystemDisplaysTheReviewedWinesOfTag(int numWines, String tag) {
        Assert.assertEquals(numWines, wineService.getWineList().size());
        for (Wine wine: wineService.getWineList()) {
            int wineid = wine.getId();
            int tagid = tagsDAO.getIdFromName(tag, wineid);
            Assert.assertEquals(tag,tagsDAO.getOne(tagid).getName());
        }
    }
}

