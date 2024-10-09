package seng202.team5.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import org.junit.Assert;
import seng202.team5.models.Wine;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

/**
 * Basic step definitions for filter feature
 */

public class SearchFilterStepDefs {
    private WineDAO wineDAO;
    private List<Wine> filteredWines;

    @Given("the user is on the base search page,")
    public void theUserIsOnTheBaseSearchPage() {
        VineyardDAO vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);
    }

    @When("the user applies a year {int} filter")
    public void theUserAppliesYearFilter(int year) {
        String query =
                wineDAO.queryBuilder("0", "0", "0", String.valueOf(year), 0.0, 800.0, 0.0, 100.0,
                        false);
        filteredWines = wineDAO.executeSearchFilter(query, null);
    }

    @Then("the system displays results of wine entries from {int}.")
    public void theSystemDisplaysResultsOfWineEntriesFrom(int year) {
        for (Wine wine : filteredWines) {
            Assert.assertEquals(year, wine.getYear());
        }
    }

    @When("the user searches for {string} in the search bar")
    public void theUserSearchesForInTheSearchBar(String search) {
        String query = wineDAO.queryBuilder(search, "0", "0", "0", 0.0, 800.0, 0.0, 100.0, false);
        filteredWines = wineDAO.executeSearchFilter(query, null);
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
                wineDAO.queryBuilder(nonExistentSearch, "0", "0", "0", 0.0, 800.0, 0.0, 100.0,
                        false);
        filteredWines = wineDAO.executeSearchFilter(query, null);
    }

    @Then("the system displays no entries in table")
    public void theSystemDisplaysNoEntriesInTable() {
        Assert.assertEquals(0, filteredWines.size());
    }

    @When("the user applies a variety filter {string}")
    public void theUserAppliesVarietyFilter(String variety) {
        String query = wineDAO.queryBuilder("0", variety, "0", "0", 0.0, 800.0, 0.0, 100.0, false);
        filteredWines = wineDAO.executeSearchFilter(query, null);
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
                wineDAO.queryBuilder("0", "0", "0", "0", minPrice, maxPrice, 0.0, 100.0, false);
        filteredWines = wineDAO.executeSearchFilter(query, null);

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
                wineDAO.queryBuilder("0", "0", "0", String.valueOf(year), 0, 800.0, 0.0, rating,
                        false);
        filteredWines = wineDAO.executeSearchFilter(query, null);
    }

    @Then("the system displays only wines that from {int} rate {int}.")
    public void theSystemDisplaysOnlyWinesThatFromRate(int year, int minRating) {
        for (Wine wine : filteredWines) {
            Assert.assertEquals(year, wine.getYear());
            Assert.assertTrue(wine.getRating() >= minRating);
        }
    }

    @When("the user applies filters variety {string}, region name {string},"
            + " year  {int}, min price {int}, max price {int},"
            + " min rating {int}, search bar {string}.")
    public void theUserAppliesFiltersVarietyRegionNameYearMinPriceMaxPriceMinRatingSearchBar(
            String variety, String region, int year, int minPrice, int maxPrice, int minRating,
            String search) {
        String query = wineDAO.queryBuilder(search, variety, region, String.valueOf(year), minPrice,
                maxPrice, 0, minRating, false);
        filteredWines = wineDAO.executeSearchFilter(query, null);
    }

    @Then("the system displays results of wines with Variety {string}, "
            + "Region {string}, Year {int}, Price between {int} and {int}, "
            + "Rating of {int} or higher, and containing {string} in the name")
    public void theSystemDisplays(
            String variety, String region, int year, int minPrice, int maxPrice, int minRating,
            String search) {
        for (Wine wine : filteredWines) {
            Assert.assertEquals(wine.getWineVariety(), variety);
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
        String query = wineDAO.queryBuilder("", variety, region, "0", 0.0, 800.0, 0, 100.0, false);
        filteredWines = wineDAO.executeSearchFilter(query, null);
    }


    @Then("no entries are shown")
    public void noEntriesAreShown() {
        Assert.assertEquals(0, filteredWines.size());
    }


}

