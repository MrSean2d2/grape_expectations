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
import seng202.team5.exceptions.NotFoundException;
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
public class EditWinePopupController extends PageController implements ClosableWindow {

    @FXML
    private Button closeButton;

    @FXML
    private Label actionLabel;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField nameField;

    @FXML
    private Label nameErrorLabel;

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
    private final int minWidth = 762;
    private final int minHeight = 486;

    private WineService wineService;


    /**
     * Set the rating value and initialise the slider.
     */
    private void initRating() {
        ratingSlider.setValue(wine.getRating());
        initRatingSlider();

    }

    /**
     * Init rating slider to use integer values. Also bind the rating field
     * to the slider.
     */
    private void initRatingSlider() {
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

    public void init(Stage stage) {
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
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
            ratingSlider.setValue(0);
            initRatingSlider();
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
     * @param  message the error message
     * @param  field the TextField containing the error
     * @param errorLabel the label to show the error message on
     */
    private void fieldError(String message, TextField field, Label errorLabel) {
        field.getStyleClass().add("field_error");
        errorLabel.setText(message);
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


    /**
     * Turns the year field into an int. Returns 0 and shows an error if parsing
     * fails.
     *
     * @return the year
     */
    private int validateYear() {
        int year = 0;
        try {
            year = Integer.parseInt(yearField.getText());
            checkYear(year);
        } catch (NumberFormatException e) {
            fieldError("Year must be a number!", yearField, yearErrorLabel);
        }
        return year;
    }

    /**
     * Turns the price field into an int. Returns 0 and shows an error if parsing
     * fails.
     *
     * @return the price
     */
    private double validatePrice() {
        double price = 0;
        try {
            price = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            fieldError("Price must be a number", priceField, priceErrorLabel);
        }
        return price;
    }

    /**
     * Reset the error state and hide all the error labels.
     */
    private void resetErrors() {
        isWineValid = true;
        priceErrorLabel.setVisible(false);
        yearErrorLabel.setVisible(false);
        nameErrorLabel.setVisible(false);
        yearField.getStyleClass().remove("field_error");
        priceField.getStyleClass().remove("field_error");
        nameField.getStyleClass().remove("field_error");
        vineyardField.getStyleClass().remove("field_error");
    }

    /**
     * Submit the form, perform error validation and edit the wine, updating the
     * database if everything is valid.
     */
    @FXML
    private void submit() {
        resetErrors();
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
        String colour = colourField.getText();
        showErrors(name, price);
        if (isWineValid) {
            if (wine == null) {
                // We are adding a new wine
                try {
                    Wine existingEntry = wineDAO.getWineFromName(name);
                    if (existingEntry != null) {
                        addNotification("Wine already exists!", "#e95958");
                        fieldError("Wine already exists!", nameField, nameErrorLabel);
                    }
                } catch (NotFoundException e) {
                    // Not found is actually the blue sky outcome here
                    wine = new Wine(name, description, year, rating, price, variety, colour, vineyard);
                    wineDAO.add(wine);
                    closeWindow();
                }
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
                closeWindow();
            }
        }
    }

    /**
     * Validate the fields which need validating using WineService and show error
     * messages if any are invalid.
     *
     * @param name the name field (shouldn't be blank)
     * @param price the price field (shouldn't be negative)
     */
    private void showErrors(String name, double price) {
        if (!wineService.validName(name)) {
            fieldError("Name can't be blank!", nameField, nameErrorLabel);
        }
        if (!wineService.validPrice(price)) {
            fieldError("Price can't be negative!", priceField, priceErrorLabel);
        }
    }

    /**
     * Validate the year once we are sure a valid integer was entered.
     *
     * @param year the year
     */
    private void checkYear(int year) {
        if (!wineService.validYear(year)) {
            fieldError("Year can't be in the future or older than 1700", yearField, yearErrorLabel);
        }
    }

    /**
     * Get the minimum height for this window. Use this to set the stage minimum
     * height.
     *
     * @return the min height
     */
    public int getMinHeight() {
        int minHeight = 486;
        return minHeight;
    }

    /**
     * Get the minimum width for this window. Use this to set the stage minimum
     * width.
     *
     * @return the min width
     */
    public int getMinWidth() {
        int minWidth = 762;
        return minWidth;
    }
}
