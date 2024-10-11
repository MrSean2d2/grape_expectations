package seng202.team5.gui;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
public class EditWinePopupController extends FormErrorController implements ClosableWindow {

    @FXML
    public Button submitButton;
    @FXML
    private Button closeButton;

    @FXML
    private Button deleteButton;

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

    /**
     * Initialize the window.
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        int minWidth = 776;
        stage.setMinWidth(minWidth);
        int minHeight = 549;
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

        closeButton.setTooltip(new Tooltip("Close window"));
        deleteButton.setTooltip(new Tooltip("Delete wine"));
        submitButton.setTooltip(new Tooltip("Submit changes"));
        yearField.setTooltip(new Tooltip("Year of wine"));
        priceField.setTooltip(new Tooltip("Price of wine"));
        ratingField.setTooltip(new Tooltip("Rating of wine"));

        if (wine != null) {
            initLabels();
            initRating();
            initFields();
        } else {
            ratingSlider.setValue(0);
            initRatingSlider();
            actionLabel.setText("Add wine: ");
            wineLabel.setVisible(false);
            deleteButton.setVisible(false);


        }
        descriptionArea.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= maxChars ? change : null));

        initAutoComplete();

        ratingSlider.setTooltip(new Tooltip("Select wine rating"));


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
     * Delete the selected wine.
     */
    @FXML
    private void deleteWine() {
        if (wine != null) {
            Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confAlert.setTitle("Delete Wine?");
            confAlert.setHeaderText(String.format("Delete %s?", wine.getName()));
            confAlert.setContentText("Are you sure you want to delete this wine?");

            Optional<ButtonType> result = confAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                VineyardDAO vineyardDAO = new VineyardDAO();
                WineDAO wineDAO = new WineDAO(vineyardDAO);
                wineDAO.delete(wine.getId());
                wineService.getWineList().remove(wine);
                closeWindow();
                addNotification("Wine deleted", "#d5e958");
            }
        }
    }

    /**
     * Show a field error and set the current wine information as invalid.
     *
     * @param  message the error message
     * @param  field the TextField containing the error
     * @param errorLabel the label to show the error message on
     */
    @Override
    protected void fieldError(TextField field, Label errorLabel, String message) {
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
    @Override
    protected void fieldError(TextField field) {
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
            fieldError(yearField, yearErrorLabel, "Year must be a number!");
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
            fieldError(priceField, priceErrorLabel, "Price must be a number");
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
                        fieldError(nameField, nameErrorLabel, "Wine already exists!");
                    }
                } catch (NotFoundException e) {
                    // Not found is actually the blue sky outcome here
                    wine = new Wine(name, description, year, rating, price,
                            variety, colour, vineyard);
                    wineDAO.add(wine);
                    addNotification("Wine added", "#d5e958");
                    closeWindow();
                }
            } else {
                if (!Objects.equals(name, wine.getName()) && wineService.checkExistingWine(name)) {
                    addNotification("Wine name is taken!", "#e95958");
                    fieldError(nameField, nameErrorLabel, "Wine name is taken!");
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
                    addNotification("Wine updated", "#d5e958");
                    closeWindow();
                }
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
            fieldError(nameField, nameErrorLabel, "Name can't be blank!");
        }
        if (!wineService.validPrice(price)) {
            fieldError(priceField, priceErrorLabel, "Price can't be negative!");
        }
    }

    /**
     * Validate the year once we are sure a valid integer was entered.
     *
     * @param year the year
     */
    private void checkYear(int year) {
        if (!wineService.validYear(year)) {
            fieldError(yearField, yearErrorLabel, "Year can't be in the future or older than 1700");
        }
    }

}
