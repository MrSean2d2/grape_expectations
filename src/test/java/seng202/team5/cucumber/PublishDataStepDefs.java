package seng202.team5.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;

public class PublishDataStepDefs {
    private Vineyard vineyard;
    private Wine wine;
    private Wine duplicateWine;
    private WineDAO wineDAO;

    @Given("vineyard {string} in {string}")
    public void vineyard(String name, String region) {
        vineyard = new Vineyard(name, region);
    }

    @When("the vineyard owner enters wine with details {string}, {string}, "
            + "{int}, {int}, {int}, {string}, {string}, vineyard")
    public void newWine(String name, String description, Integer year, Integer rating,
                        Integer price, String variety, String colour) throws NotFoundException {
        wine = new Wine(name, description, year, rating, price, variety, colour, vineyard);
        wineDAO = new WineDAO(new VineyardDAO());
        wineDAO.add(wine);
        wine = wineDAO.getWineFromName(name);
    }

    @Then("the data is saved, and the record is added")
    public void wineToDatabase() throws NotFoundException {
        Assertions.assertEquals("is a nice wine",
                wineDAO.getWineFromName("test p wine").getDescription());
    }

    @When("attempts to add another wine with same details {string}, {string}, {int},"
            + " {int}, {int}, {string}, {string}, vineyard")
    public void duplicateWine(String name, String description, Integer year,
                              Integer rating, Integer price, String variety, String colour) {
        duplicateWine = new Wine(name, description, year, rating, price, variety, colour, vineyard);
    }

    @Then("the system detects wine with same name in database and rejects duplicate")
    public void cannotAddDuplicateWine() throws NotFoundException {
        Assertions.assertEquals(wine, wineDAO.getWineFromName(duplicateWine.getName()));
    }
}
