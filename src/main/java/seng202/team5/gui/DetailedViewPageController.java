package seng202.team5.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.models.Review;
import seng202.team5.models.Wine;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.UserService;
import seng202.team5.services.WineService;

import java.util.Objects;


/**
 * Controller for the detailed view page.
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
    private Label logInMessageLabel;

    @FXML
    private Label provinceLabel;

    @FXML
    private Label varietyLabel;

    @FXML
    private Label colourLabel;

    @FXML
    private ImageView wineColourImage;

    @FXML
    private TextArea notesTextArea;

    @FXML
    private Button saveNotesButton;


    @FXML
    private HBox ratingStars;

    @FXML
    private ImageView star1;

    @FXML
    private ImageView star2;

    @FXML
    private ImageView star3;

    @FXML
    private ImageView star4;

    @FXML
    private ImageView star5;

    private int selectedWineId;
    private int userId;

    private final Image emptyStar =
            new Image(getClass().getResourceAsStream("/images/empty_star.png"));
    private final Image filledStar =
            new Image(getClass().getResourceAsStream("/images/filled_star.png"));

    /**
     * Initializes DetailedViewPage.
     */
    @FXML
    private void initialize() {
        Wine selectedWine = WineService.getInstance().getSelectedWine();
        selectedWineId = selectedWine.getId();

        if (selectedWine != null) {
            nameLabel.setText("" + selectedWine.getName());
            priceLabel.setText("Price: $" + selectedWine.getPrice());
            yearLabel.setText("Year: " + selectedWine.getYear());
            ratingLabel.setText("Score: " + selectedWine.getRating());
            wineDescriptionLabel.setText(selectedWine.getDescription());
            provinceLabel.setText("Province: " + selectedWine.getRegion());
            varietyLabel.setText("Variety: " + selectedWine.getWineVariety());
            colourLabel.setText("Colour: " + selectedWine.getWineColour());
            setColourImage(selectedWine);
        }

        if (UserService.getInstance().getCurrentUser() != null) {
            userId = UserService.getInstance().getCurrentUser().getId();
            ReviewDAO reviewDAO = new ReviewDAO();
            Review review = reviewDAO.getWineReview(selectedWineId, userId);

            if (review == null) {
                review = new Review(selectedWineId, userId);
                try {
                    reviewDAO.add(review);
                } catch (DuplicateEntryException e) {
                    e.printStackTrace();
                }
            }

            logInMessageLabel.setText("");
            notesTextArea.setText(review.getNotes());
            updateFavoriteButton(review.isFavourite());
            updateStarDisplay(review.getRating());

            favoriteToggleButton.setDisable(false);
            saveNotesButton.setDisable(false);
            notesTextArea.setEditable(true);
        } else {
            logInMessageLabel.setText("Log in or register to save your notes!");
            favoriteToggleButton.setDisable(true);
            saveNotesButton.setDisable(true);
            notesTextArea.setEditable(false);
        }
    }

    /**
     * sets the image to the corresponding colour:
     * red, white, rose or unknown
     * helps with usability and accessibility
     */
    private void setColourImage(Wine selectedWine) {
        switch (selectedWine.getWineColour()) {
            case "Red" -> wineColourImage.setImage(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/redColourWine.png"))));
            case "White" -> wineColourImage.setImage(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/whiteColourWine.png"))));
            case "Rosé" -> wineColourImage.setImage(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/roseColourWine.png"))));
        }//otherwise keep default image (unknown colour)
    }


    /**
     * handles a user clicking on one of the star icons, changing their review of the wine.
     *
     * @param event mouse event
     */
    @FXML
    private void handleStarClick(javafx.scene.input.MouseEvent event) {
        ImageView clickedStar = (ImageView) event.getSource(); // Get the clicked star
        int clickedStarIndex = Integer.parseInt(
                clickedStar.getId().substring(4)); // Get star number (e.g., star1 -> 1)

        ReviewDAO reviewDAO = new ReviewDAO();
        Review review = reviewDAO.getWineReview(selectedWineId, userId);

        if (review != null) {
            review.setRating(clickedStarIndex);
            updateStarDisplay(clickedStarIndex);
        }
    }

    /**
     * handles the event where the toggle favorite button is pressed.
     *
     * @param event action event
     */
    @FXML
    private void handleToggleFavourite(ActionEvent event) {
        ReviewDAO reviewDAO = new ReviewDAO();
        Review review = reviewDAO.getWineReview(selectedWineId, userId);

        if (UserService.getInstance().getCurrentUser() == null) {
            close();
        } else {
            if (review != null) {
                review.toggleFavourite(review.isFavourite());
                updateFavoriteButton(review.isFavourite());
            }
        }
    }


    /**
     * Saves the notes that are currently in the text box.
     *
     * @param event action event
     */
    @FXML
    private void handleSaveNotes(ActionEvent event) {
        ReviewDAO reviewDAO = new ReviewDAO();
        Review review = reviewDAO.getWineReview(selectedWineId, userId);

        if (UserService.getInstance().getCurrentUser() == null) {
            close();
        } else {
            if (review != null) {
                review.setNotes(notesTextArea.getText());
            }
        }
    }


    /**
     * Closes the page.
     *
     * @param event action event
     */
    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        backButton.getScene().getWindow().hide();
    }

    /**
     * Closes the page.
     */
    private void close() {
        backButton.getScene().getWindow().hide();
    }


    /**
     * Updates the 5-star rating graphic based on the rating of the wine.
     *
     * @param rating of wine
     */
    private void updateStarDisplay(int rating) {
        star1.setImage(rating >= 1 ? filledStar : emptyStar);
        star2.setImage(rating >= 2 ? filledStar : emptyStar);
        star3.setImage(rating >= 3 ? filledStar : emptyStar);
        star4.setImage(rating >= 4 ? filledStar : emptyStar);
        star5.setImage(rating >= 5 ? filledStar : emptyStar);
    }

    /**
     * Updates text of the toggle favorite button based on if the wine is favorited or not.
     *
     * @param isFavorited whether the wine is currently favourited
     */
    private void updateFavoriteButton(boolean isFavorited) {

        if (UserService.getInstance().getCurrentUser() == null) {
            close();
        } else {
            if (isFavorited) {
                favoriteToggleButton.setText("Unfavourite");
                favoriteToggleButton.setStyle("-fx-background-color: #ffdd00");
            } else {
                favoriteToggleButton.setText("Favourite");
                favoriteToggleButton.setStyle(null);
            }
        }
    }
}
