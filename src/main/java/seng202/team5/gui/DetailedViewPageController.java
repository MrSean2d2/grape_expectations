package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import seng202.team5.models.Wine;
import seng202.team5.services.WineService;

import java.awt.*;

public class DetailedViewPageController extends PageController {

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
    private Label favouriteLabel;

    @FXML
    public void initialize() {
        Wine selectedWine = WineService.getInstance().getSelectedWine();
        if (selectedWine != null) {
            nameLabel.setText("Detailed Wine View: " + selectedWine.getName());
            priceLabel.setText("Price: $" + selectedWine.getPrice());
            yearLabel.setText("Year: " + selectedWine.getYear());
            ratingLabel.setText(selectedWine.getRating() + " stars");
            favouriteLabel.setText(selectedWine.isFavourite() ? "Yes" : "No");
        }
    }
}
