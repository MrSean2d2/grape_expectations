package seng202.team5.gui;

import static java.lang.Math.max;
import static seng202.team5.services.ColourLookupService.getTagLabelColour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.models.AssignedTag;
import seng202.team5.models.Review;
import seng202.team5.models.Tag;
import seng202.team5.models.Wine;
import seng202.team5.repository.AssignedTagsDAO;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.TagsDAO;
import seng202.team5.services.UserService;
import seng202.team5.services.WineService;

/**
 * Controller for the detailed view page.
 *
 * @author Finn Brown
 */
public class DetailedViewPageController extends PageController {

    private final Image emptyStar =
            new Image(getClass().getResourceAsStream("/images/empty_star.png"));
    private final Image filledStar =
            new Image(getClass().getResourceAsStream("/images/filled_star.png"));
    Label addLabel;
    boolean canAddTag = true;
    TagsDAO tagsDAO;
    AssignedTagsDAO assignedTagsDAO;
    List<Tag> tagsList;
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
    @FXML
    private FlowPane tagBox;
    private int selectedWineId;
    private int userId;
    private PopOver tagPopover;

    /**
     * Initializes DetailedViewPage.
     */
    @FXML
    private void initialize() {
        Wine selectedWine = WineService.getInstance().getSelectedWine();
        selectedWineId = selectedWine.getId();


        if (selectedWine != null) {
            nameLabel.setText(selectedWine.getName());
            priceLabel.setText("Price: $" + selectedWine.getPrice());
            yearLabel.setText("Year: " + selectedWine.getYear());
            ratingLabel.setText("Score: " + selectedWine.getRating());
            wineDescriptionLabel.setText(selectedWine.getDescription());
            provinceLabel.setText("Province: " + selectedWine.getRegion());
            varietyLabel.setText("Variety: " + selectedWine.getWineVariety());
        }

        if (UserService.getInstance().getCurrentUser() != null) {
            userId = UserService.getInstance().getCurrentUser().getId();
            ReviewDAO reviewDAO = new ReviewDAO();
            Review review = reviewDAO.getWineReview(selectedWineId, userId);

            tagsDAO = new TagsDAO();
            tagsList = new ArrayList<>();

            // Load Existing Tags
            assignedTagsDAO = new AssignedTagsDAO();
            List<AssignedTag> assignedTags = assignedTagsDAO.getAllAssigned(selectedWineId, userId);

            addLabel = addBasicTag("+");

            for (AssignedTag tag : assignedTags) {
                // Create a new Tag
                Tag createdTag = tagsDAO.getOne(tag.getTagId());
                addTag(createdTag);
            }

            // Done Loading Tags
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
     * Show a popover to select a new tag
     */
    @FXML
    public void showTagPopover() {
        if (UserService.getInstance().getCurrentUser() != null) {
            if (tagPopover == null || !tagPopover.isShowing()) {
                canAddTag = true;

                try {
                    FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/TagPopover.fxml"));
                    Node content = baseLoader.load();

                    // Create the Popup
                    tagPopover = new PopOver(content);
                    tagPopover.getStyleClass().add("popover");
                    tagPopover.setDetachable(false);
                    tagPopover.setAutoHide(true);
                    tagPopover.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

                    // Set the Popup to close when clicking outside
                    tagPopover.setAutoHide(true);

                    VBox existingBox = ((VBox) content.lookup("#tester"));
                    List<Tag> tags = tagsDAO.getFromUser(userId);
                    List<Label> labels = new ArrayList<>();

                    for (Tag tag : tags) {
                        if (tagsList.contains(tag)) continue;
                        // Set the user ID to be the current user id, override the default
                        //  user ID may be -1, for default tags
                        tag.setUserId(userId);

                        // Add non-existing ones :D
                        Label newTag = new Label(tag.getName());
                        newTag.getStyleClass().add("tag");
                        newTag.getStyleClass().add("max-width");

                        newTag.getStyleClass().add(getTagLabelColour(tag.getColour()));

                        // Add on click
                        newTag.setOnMouseClicked(event -> {
                            if (!(tagsList.contains(tag)) && tagPopover.isShowing()) {
                                addTag(tag);
                                updateTags();
                                closePopOver();
                            }
                        });

                        labels.add(newTag);
                    }
                    existingBox.getChildren().addAll(labels);

                    content.lookup("#closeButton").setOnMouseClicked(event -> closePopOver());

                    // Add a button to confirm selection
                    Button confirmButton = (Button) content.lookup("#submitButton");
                    confirmButton.setOnAction(event -> {
                        try {
                            addTagWithCustomName(content);
                        } catch (DuplicateEntryException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    // Show the Popup below the button
                    tagPopover.show(addLabel);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Close the tag menu.
     */
    private void closePopOver() {
        tagPopover.hide();
    }

    /**
     * Handles a BASIC tag getting added to this wine.
     * The basic tag is used as the "adder" tag
     *
     * @param label the text to display
     */
    public Label addBasicTag(String label) {
        Label newTag = new Label(label);
        newTag.getStyleClass().add("tag");

        // Basic adder
        newTag.setStyle("-fx-font-weight: 700");
        // Click add behaviour
        newTag.setOnMouseClicked(event -> showTagPopover());

        newTag.setCursor(Cursor.HAND);

        int numChildren = tagBox.getChildren().size();
        tagBox.getChildren().add(max(numChildren - 1, 0), newTag); //Add just before the final tag
        updateTags();

        return newTag;
    }

    /**
     * Handles a tag getting added to this wine.
     *
     * @param tag the text to display
     */
    public Label addTag(Tag tag) {
        Label newTag = new Label(tag.getName());
        newTag.getStyleClass().add("tag");
        newTag.getStyleClass().add(getTagLabelColour(tag.getColour()));

        // Remove on click
        newTag.setOnMouseClicked(event -> {
            tagBox.getChildren().remove(newTag);
            tagsList.remove(tag);
            updateTags();
        });

        newTag.setCursor(Cursor.HAND);

        // Click behaviour
        int numChildren = tagBox.getChildren().size();
        tagBox.getChildren().add(max(numChildren - 1, 0), newTag);

        tagsList.add(tag);

        updateTags();
        return newTag;
    }

    /**
     * Handles adding a tag with a custom name (from the custom name field).
     */
    public void addTagWithCustomName(Node content) throws DuplicateEntryException {
        if (canAddTag && tagPopover.isShowing()) {
            String customTag = ((TextField) content.lookup("#addField")).getText();
            customTag = customTag.trim();

            // Name validity checks
            boolean nameIsValid = true;
            if (customTag.isEmpty()) {
                nameIsValid = false;
            } else {
                // Check that this tag doesn't already exist
                List<Tag> userTags = tagsDAO.getFromUser(userId);
                for (Tag tag : userTags) {
                    if (customTag.equals(tag.getName())) {
                        nameIsValid = false;
                        break;
                    }
                }
            }

            // Determine which tag to add
            if (nameIsValid) {
                Random rand = new Random();
                Tag newTag = new Tag(userId, customTag, rand.nextInt(5));

                // Try to add the new tag to the db
                newTag.setTagId(tagsDAO.add(newTag));
                addTag(newTag);
            }

            canAddTag = false;

            closePopOver();
        }
    }

    /**
     * Update the selected tags (Set the visibility of the add button).
     */
    private void updateTags() {
        int numChildren = tagBox.getChildren().size();
        if (addLabel != null) {
            addLabel.setVisible(numChildren <= 5);
        }
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
        close();
    }

    /**
     * Closes the page.
     */
    private void close() {
        try {
            if (UserService.getInstance().getCurrentUser() != null) {
                assignedTagsDAO.deleteFromUserWineId(userId, selectedWineId);
                // Add to assigned tags db
                for (Tag tag : tagsList) {
                    assignedTagsDAO.add(new AssignedTag(tag.getTagId(), userId, selectedWineId));
                }
            }
        } catch (DuplicateEntryException e) {
            throw new RuntimeException(e);
        }
        backButton.getScene().getWindow().hide();
        addNotification("Updated Wine Review", "#d5e958");
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
