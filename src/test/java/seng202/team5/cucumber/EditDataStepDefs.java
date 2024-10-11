package seng202.team5.cucumber;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team5.exceptions.InstanceAlreadyExistsException;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.DatabaseService;
import seng202.team5.services.WineService;

public class EditDataStepDefs {
    private Vineyard vineyard;
    private Wine wine;
    private WineDAO wineDAO;
    private WineService wineService;
    private boolean validYear;
    private boolean validPrice;
    private boolean validName;

    @BeforeAll
    public static void setUp() throws InstanceAlreadyExistsException {
        DatabaseService databaseService;
        DatabaseService.removeInstance();
        databaseService = DatabaseService.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test.db");
        databaseService.resetDb();
    }

    @Given("a vineyard {string} in {string}")
    public void vineyard(String name, String region) {
        vineyard = new Vineyard(name, region);
    }

    @Given("wine with details {string}, {string}, {int}, {int}, {int}, {string}, "
            + "{string}, vineyard in database")
    public void wineInDatabase(String name, String description, Integer year,
                               Integer rating, Integer price, String variety,
                               String colour) throws NotFoundException {
        wineDAO = new WineDAO(new VineyardDAO());
        wine = new Wine(name, description, year, rating, price, variety, colour, vineyard);
        wineDAO.add(wine);
        wine = wineDAO.getWineFromName(name);
    }

    @When("the admin changes the description of the wine to {string}")
    public void changeDescription(String newDescription) {
        wine.setDescription(newDescription);
        wineDAO.update(wine);
    }

    @Then("the system updates the existing record with the new data")
    public void existingWineUpdated() throws NotFoundException {
        Assertions.assertEquals("very nice wine",
                wineDAO.getWineFromName("test e wine").getDescription());
    }

    @When("the admin enters the year {int} and submits the form")
    public void invalidYear(Integer year) {
        wineService = WineService.getInstance();
        validYear = wineService.validYear(year);
    }

    @Then("data is not saved, error message of year can't be in the future or older than 1700")
    public void yearReject() {
        Assertions.assertFalse(validYear);
    }

    @When("the admin enters the price {int} and submits the form")
    public void invalidPrice(Integer price) {
        wineService = WineService.getInstance();
        validPrice = wineService.validPrice(price);
    }

    @Then("data is not saved, error message price can't be negative")
    public void priceReject() {
        Assertions.assertFalse(validPrice);
    }

    @When("the user submits {string} in the name field")
    public void invalidName(String name) {
        wineService = WineService.getInstance();
        validName = wineService.validName(name);
    }

    @Then("data is not saved, error message name can't be blank")
    public void nameReject() {
        Assertions.assertFalse(validName);
    }

    @When("the admin deletes {string}")
    public void deleteWine(String wine) throws NotFoundException {
        wineDAO.delete(wineDAO.getWineFromName(wine).getId());
    }

    @Then("{string} is no longer in the database")
    public void wineNotInDatabase(String wine) {
        wineService = WineService.getInstance();
        Assertions.assertFalse(wineService.checkExistingWine(wine));
    }
}
