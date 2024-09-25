package seng202.team5.gui;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
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
public class EditWinePopupController {

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
    private TextField varietyField;

    @FXML
    private TextField vineyardField;

    @FXML
    private Label wineLabel;

    @FXML
    private TextField yearField;

    @FXML
    private Label yearErrorLabel;

    private final int maxChars = 500;
    private Wine wine;

    @FXML
    private void initialize() {
        wine = WineService.getInstance().getSelectedWine();
        if (wine != null) {
            actionLabel.setText("Edit wine: ");
            wineLabel.setText(wine.getName());
            nameField.setText(wine.getName());
            yearField.setText(String.valueOf(wine.getYear()));
            ratingSlider.setValue(wine.getRating());
            priceField.setText(String.valueOf(wine.getPrice()));
            varietyField.setText(wine.getWineVariety());
            vineyardField.setText(wine.getVineyard().getName());
            descriptionArea.setText(wine.getDescription());
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

    }

    @FXML
    private void close() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private void yearError() {
        yearField.getStyleClass().add("field_error");
        yearErrorLabel.setText("Invalid year!");
        yearErrorLabel.setVisible(true);
    }

    private void priceError() {
        priceField.getStyleClass().add("field_error");
        priceErrorLabel.setText("Invalid price!");
        priceErrorLabel.setVisible(true);
    }

    @FXML
    private void submit(ActionEvent event) {
        priceErrorLabel.setVisible(false);
        yearErrorLabel.setVisible(false);
        String name = nameField.getText();
        int year = 0;
        double price = 0;
        try {
            year = Integer.parseInt(yearField.getText());
        } catch (NumberFormatException e) {
            yearError();
        }
        int rating = Math.toIntExact(Math.round(ratingSlider.getValue()));
        try {
            price = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            priceError();
        }
        String variety = varietyField.getText();
        Vineyard vineyard = new Vineyard(vineyardField.getText(), "Unknown Region");
        String description = descriptionArea.getText();
        if (wine == null) {
            wine = new Wine(name, description, year, rating, price, variety, "Unknown", vineyard);
        } else {
            wine.setName(name);
            wine.setDescription(description);
            wine.setYear(year);
            wine.setRating(rating);
            wine.setVariety(variety);
            wine.setColour("Unknown");
            wine.setVineyard(vineyard);
        }
    }

}
