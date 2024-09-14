package seng202.team5.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import seng202.team5.models.Wine;
import seng202.team5.services.WineService;


/**
 * Controller for the detailed view page
 *
 * @author Finn Brown
 */
public class DetailedViewPageController {

    @FXML
    private Button backButton;

    @FXML
    private Button favoriteToggleButton;

    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label yearLabel;

    @FXML
    private Label ratingLabel;

    @FXML
    private Label wineDescriptionLabel;

    @FXML
    private TextArea notesTextArea;

    @FXML
    private Button saveNotesButton;


    /**
     * Initializes DetailedViewPage
     */
    @FXML
    public void initialize() {
        Wine selectedWine = WineService.getInstance().getSelectedWine();
        if (selectedWine != null) {
            nameLabel.setText("" + selectedWine.getName());
            priceLabel.setText("Price: $" + selectedWine.getPrice());
            yearLabel.setText("Year: " + selectedWine.getYear());
            ratingLabel.setText("Score: " + selectedWine.getRating());
            wineDescriptionLabel.setText(selectedWine.getDescription());
            updateFavoriteButton(selectedWine.isFavourite());
            if (selectedWine.isFavourite()) {
                favoriteToggleButton.setStyle("-fx-background-color: #ffdd00");
            }
        }
    }

    /**
     * handles the event where the toggle favorite button is pressed
     * @param event
     */
    @FXML
    private void handleToggleFavourite(ActionEvent event) {
        Wine selectedWine = WineService.getInstance().getSelectedWine();
        if (selectedWine != null) {
            selectedWine.toggleFavourite(selectedWine.isFavourite());
            updateFavoriteButton(selectedWine.isFavourite());
        }
    }

    /**
     * Saves the notes that are currently in the text box
     *
     * @param event
     */
    @FXML
    private void handleSaveNotes(ActionEvent event) {
        Wine selectedWine = WineService.getInstance().getSelectedWine();
        if (selectedWine != null) {
            selectedWine.setNotes(notesTextArea.getText());
        }
    }

    /**
     * Closes the page
     *
     * @param event
     */
    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        backButton.getScene().getWindow().hide();
    }

    /**
     * Updates text of the toggle favorite button based on if the wine is favorited or not
     *
     * @param isFavorited
     */
    private void updateFavoriteButton(boolean isFavorited) {
        if (isFavorited) {
            favoriteToggleButton.setText("Unfavorite");
            favoriteToggleButton.setStyle("-fx-background-color: #ffdd00");
        } else {
            favoriteToggleButton.setText("Favorite");
            favoriteToggleButton.setStyle(null);
        }
    }
}
