package seng202.team5.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import seng202.team5.models.Wine;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

import java.util.List;

public class FilterStepDefs {
    private WineDAO wineDAO;
    private VineyardDAO vineyardDAO;
    private List<Wine> filteredWines;

    @Given("the user is on the base search page,")
    public void theUserIsOnTheBaseSearchPage() {
        vineyardDAO = new VineyardDAO();
        wineDAO = new WineDAO(vineyardDAO);
    }

    @When("the user applies a year {int} filter")
    public void theUserAppliesAYearFilter(int year) {
        String query = wineDAO.queryBuilder("0","0","0",String.valueOf(year), 0.0, 800.0, 0.0, 100.0, false);
        filteredWines = wineDAO.executeSearchFilter(query, null);
    }
    @Then("the system displays results of wine entries from {int}.")
    public void theSystemDisplaysResultsOfWineEntriesFrom(int year) {
        for(Wine wine: filteredWines) {
            Assert.assertEquals(year, wine.getYear());
        }
    }
}
