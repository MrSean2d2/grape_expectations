package seng202.team5.gui;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.textfield.TextFields;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.WineService;

/**
 * A controller for the edit wine popup window.
 *
 * @author Sean Reitsma
 */
public class EditWinePopupController extends PageController {

    @FXML
    private Button closeButton;

    @FXML
    private Label actionLabel;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private Label priceErrorLabel;

    @FXML
    private Slider ratingSlider;

    @FXML
    private TextField ratingField;

    @FXML
    private TextField varietyField;

    @FXML
    private TextField colourField;

    @FXML
    private TextField vineyardField;

    @FXML
    private TextField regionField;

    @FXML
    private Label wineLabel;

    @FXML
    private TextField yearField;

    @FXML
    private Label yearErrorLabel;

    private final int maxChars = 500;
    private Wine wine;
    private boolean isWineValid = true;

    private static final List<EditWinePopupController> openInstances = new ArrayList<>();


    @FXML
    private void initialize() {
        wine = WineService.getInstance().getSelectedWine();
        if (wine != null) {
            actionLabel.setText("Edit wine: ");
            wineLabel.setText(wine.getName());
            nameField.setText(wine.getName());
            yearField.setText(String.valueOf(wine.getYear()));
            ratingSlider.setValue(wine.getRating());
            ratingSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                    ratingSlider.setValue(newVal.intValue()));
            StringConverter<Number> stringConverter = new NumberStringConverter();
            ratingField.textProperty().bindBidirectional(
                    ratingSlider.valueProperty(), stringConverter);
            priceField.setText(String.valueOf(wine.getPrice()));
            varietyField.setText(wine.getWineVariety());
            vineyardField.setText(wine.getVineyard().getName());
            regionField.setText(wine.getRegion());
            descriptionArea.setText(wine.getDescription());
            descriptionArea.setWrapText(true);
            colourField.setText(wine.getWineColour());
        } else {
            actionLabel.setText("Add wine: ");
            wineLabel.setVisible(false);
        }
        descriptionArea.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= maxChars ? change : null));

        VineyardDAO vineyardDAO = new VineyardDAO();
        WineDAO wineDAO = new WineDAO(vineyardDAO);
        List<String> varietySuggestions = wineDAO.getVariety();
        TextFields.bindAutoCompletion(varietyField, varietySuggestions);
        List<String> vineyardSuggestions = vineyardDAO.getDistinctNames();
        TextFields.bindAutoCompletion(vineyardField, vineyardSuggestions);
        List<String> regionSuggestions = vineyardDAO.getRegions();
        TextFields.bindAutoCompletion(regionField, regionSuggestions);

        openInstances.add(this);
    }

    @FXML
    private void close() {
        openInstances.remove(this);
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Close all windows of this type.
     */
    @FXML
    public static void closeAll() {
        for (EditWinePopupController instance : new ArrayList<>(openInstances)) {
            instance.close();
        }
    }

    private void yearError() {
        yearField.getStyleClass().add("field_error");
        yearErrorLabel.setText("Invalid year!");
        yearErrorLabel.setVisible(true);
        isWineValid = false;
    }

    private void priceError() {
        priceField.getStyleClass().add("field_error");
        priceErrorLabel.setText("Invalid price!");
        priceErrorLabel.setVisible(true);
        isWineValid = false;
    }

    private int validateYear() {
        int year = 0;
        try {
            year = Integer.parseInt(yearField.getText());
        } catch (NumberFormatException e) {
            yearError();
        }
        int currentYear = Year.now().getValue();
        if (year < 1700 || year > currentYear) {
            yearError();
        }
        return year;
    }

    private double validatePrice() {
        double price = 0;
        try {
            price = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            priceError();
        }
        if (price < 0) {
            priceError();
        }
        return price;
    }

    private String validateVariety() {
        String variety = varietyField.getText();
        if (variety.isBlank()) {
            variety = "Unknown variety";
        }
        return variety;
    }

    private Vineyard retreiveVineyard(VineyardDAO vineyardDAO) {
        Vineyard vineyard = null;
        String vineyardName = vineyardField.getText();
        if (vineyardName.isBlank()) {
            vineyardField.getStyleClass().add("field_error");
            isWineValid = false;
        } else {
            String region = regionField.getText();
            int vineyardId = vineyardDAO.getIdFromNameRegion(vineyardName, region);
            if (vineyardId == 0) {
                vineyard = new Vineyard(vineyardName, region);
                vineyard.setId(vineyardDAO.add(vineyard));
            } else {
                vineyard = vineyardDAO.getOne(vineyardId);
            }
        }
        return vineyard;
    }

    private String validateName() {
        String name = nameField.getText();
        if (name.isBlank()) {
            nameField.getStyleClass().add("field_error");
            isWineValid = false;
        }
        return name;
    }

    private String validateColour() {
        String colour = colourField.getText();
        if (colour.isBlank()) {
            colour = "Unknown";
        }
        return colour;
    }

    @FXML
    private void submit() {
        isWineValid = true;
        priceErrorLabel.setVisible(false);
        yearErrorLabel.setVisible(false);
        yearField.getStyleClass().remove("field_error");
        priceField.getStyleClass().remove("field_error");
        nameField.getStyleClass().remove("field_error");
        String colour = validateColour();
        int year = validateYear();
        double price = validatePrice();
        int rating = Math.toIntExact(Math.round(ratingSlider.getValue()));
        String variety = validateVariety();
        VineyardDAO vineyardDAO = new VineyardDAO();
        Vineyard vineyard = retreiveVineyard(vineyardDAO);
        WineDAO wineDAO = new WineDAO(vineyardDAO);
        String description = descriptionArea.getText();
        String name = validateName();
        if (isWineValid) {
            if (wine == null) {
                wine = new Wine(name, description, year, rating, price, variety, colour, vineyard);
                wineDAO.add(wine);
            } else {
                wine.setName(name);
                wine.setDescription(description);
                wine.setYear(year);
                wine.setPrice(price);
                wine.setRating(rating);
                wine.setVariety(variety);
                wine.setColour(colour);
                wine.setVineyard(vineyard);
                wineDAO.update(wine);
            }
            close();
        }
    }

}
