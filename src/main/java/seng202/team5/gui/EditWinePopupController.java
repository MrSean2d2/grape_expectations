package seng202.team5.gui;

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
import seng202.team5.services.OpenWindowsService;
import seng202.team5.services.VineyardService;
import seng202.team5.services.WineService;

/**
 * A controller for the edit wine popup window.
 *
 * @author Sean Reitsma
 */
public class EditWinePopupController extends PageController implements ClosableWindow{

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

    private WineService wineService;


    /**
     * Init rating slider to use integer values. Also bind the rating field
     * to the slider.
     */
    private void initRating() {
        ratingSlider.setValue(wine.getRating());
        ratingSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                ratingSlider.setValue(newVal.intValue()));
        StringConverter<Number> stringConverter = new NumberStringConverter();
        ratingField.textProperty().bindBidirectional(
                ratingSlider.valueProperty(), stringConverter);
    }

    /**
     * Init the labels with information about the current wine.
     */
    private void initLabels() {
        actionLabel.setText("Edit wine: ");
        wineLabel.setText(wine.getName());
        nameField.setText(wine.getName());
        yearField.setText(String.valueOf(wine.getYear()));
    }

    /**
     * Init the text fields with the pre-existing wine information.
     */
    private void initFields() {
        priceField.setText(String.valueOf(wine.getPrice()));
        varietyField.setText(wine.getWineVariety());
        vineyardField.setText(wine.getVineyard().getName());
        regionField.setText(wine.getRegion());
        descriptionArea.setText(wine.getDescription());
        descriptionArea.setWrapText(true);
        colourField.setText(wine.getWineColour());
    }

    /**
     * Init auto-completion for variety, vineyard, and region fields.
     */
    private void initAutoComplete() {
        VineyardDAO vineyardDAO = new VineyardDAO();
        WineDAO wineDAO = new WineDAO(vineyardDAO);
        List<String> varietySuggestions = wineDAO.getVariety();
        TextFields.bindAutoCompletion(varietyField, varietySuggestions);
        List<String> vineyardSuggestions = vineyardDAO.getDistinctNames();
        TextFields.bindAutoCompletion(vineyardField, vineyardSuggestions);
        List<String> regionSuggestions = vineyardDAO.getRegions();
        TextFields.bindAutoCompletion(regionField, regionSuggestions);
    }

    /**
     * Initialise the edit wine popup.
     */
    @FXML
    private void initialize() {
        OpenWindowsService.getInstance().addWindow(this);
        wineService = WineService.getInstance();
        wine = wineService.getSelectedWine();
        if (wine != null) {
            initLabels();
            initRating();
            initFields();
        } else {
            actionLabel.setText("Add wine: ");
            wineLabel.setVisible(false);
        }
        descriptionArea.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= maxChars ? change : null));

        initAutoComplete();


    }

    /**
     * Called when the close button is pressed.
     */
    @FXML
    @Override
    public void closeWindow() {
        OpenWindowsService.getInstance().closeWindow(this);
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Show a field error and set the current wine information as invalid.
     *
     * @param  fieldName the name of the field which has the error (to be used
     *                   in the error message)
     * @param  field the TextField containing the error
     * @param errorLabel the label to show the error message on
     */
    private void fieldError(String fieldName, TextField field, Label errorLabel) {
        field.getStyleClass().add("field_error");
        errorLabel.setText(String.format("Invalid %s!", fieldName));
        errorLabel.setVisible(true);
        isWineValid = false;
    }

    /**
     * Show a field error and set the current wine information as invalid.
     *
     * @param field the TextField containing the error
     */
    private void fieldError(TextField field) {
        field.getStyleClass().add("field_error");
        isWineValid = false;
    }


    private int validateYear() {
        int year = 0;
        try {
            year = Integer.parseInt(yearField.getText());
        } catch (NumberFormatException e) {
            fieldError("year", yearField, yearErrorLabel);
        }
        return year;
    }

    private double validatePrice() {
        double price = 0;
        try {
            price = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            fieldError("price", priceField, priceErrorLabel);
        }
        return price;
    }

    private void resetErrors() {
        isWineValid = true;
        priceErrorLabel.setVisible(false);
        yearErrorLabel.setVisible(false);
        yearField.getStyleClass().remove("field_error");
        priceField.getStyleClass().remove("field_error");
        nameField.getStyleClass().remove("field_error");
        vineyardField.getStyleClass().remove("field_error");
    }

    @FXML
    private void submit() {
        resetErrors();
        String colour = colourField.getText();
        int year = validateYear();
        double price = validatePrice();
        int rating = ratingSlider.valueProperty().intValue();
        String variety = varietyField.getText();
        VineyardService vineyardService = VineyardService.getInstance();
        Vineyard vineyard = null;
        try {
            vineyard = vineyardService.retreiveVineyard(vineyardField.getText(),
                    regionField.getText());
        } catch (IllegalArgumentException e) {
            fieldError(vineyardField);
        }
        VineyardDAO vineyardDAO = new VineyardDAO();
        WineDAO wineDAO = new WineDAO(vineyardDAO);
        String description = descriptionArea.getText();
        String name = nameField.getText();
        showErrors(name, year, price);
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
            closeWindow();
        }
    }

    private void showErrors(String name, int year, double price) {
        if (!wineService.validName(name)) {
            fieldError(nameField);
        }
        if (!wineService.validPrice(price)) {
            fieldError("price", priceField, priceErrorLabel);
        }
        if (!wineService.validYear(year)) {
            fieldError("year", yearField, yearErrorLabel);
        }
    }

}
