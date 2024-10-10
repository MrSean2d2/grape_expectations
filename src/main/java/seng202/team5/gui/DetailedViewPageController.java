package seng202.team5.gui;

import static java.lang.Math.max;
import static seng202.team5.services.ColourLookupService.getTagLabelColour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PopOver;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.models.AssignedTag;
import seng202.team5.models.Review;
import seng202.team5.models.Tag;
import seng202.team5.models.Wine;
import seng202.team5.repository.AssignedTagsDAO;
import seng202.team5.repository.ReviewDAO;
import seng202.team5.repository.TagsDAO;
import seng202.team5.services.OpenWindowsService;
import seng202.team5.services.UserService;
import seng202.team5.services.WineService;

/**
 * Controller for the detailed view page.
 *
 * @author Finn Brown
 */
public class DetailedViewPageController extends PageController implements ClosableWindow {
    private static final Logger log = LogManager.getLogger(DetailedViewPageController.class);
    private final Image emptyStar = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/images/empty_star.png")));
    private final Image filledStar = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/images/filled_star.png")));

    Label addLabel;
    boolean canAddTag = true;
    TagsDAO tagsDAO;
    AssignedTagsDAO assignedTagsDAO;
    List<Tag> tagsList;

    @FXML
    private Label addTagLabel;

    @FXML
    private Button backButton;

    @FXML
    private HBox headerButtonContainer;

    @FXML
    private Label logInMessageLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private TextArea noteTextArea;

    @FXML
    private Label priceLabel;

    @FXML
    private Label provinceLabel;

    @FXML
    private Label ratingLabel;

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

    @FXML
    private Label varietyLabel;

    @FXML
    private Label vineyardLabel;

    @FXML
    private ImageView wineColourImage;

    @FXML
    private Label wineDescriptionLabel;

    @FXML
    private Label yearLabel;

    private int selectedWineId;
    private int userId;
    private PopOver tagPopover;
    private ReviewDAO reviewDAO;
    private Review review;


    /**
     * Initialise the stage. This is called by the class which loads the
     * DetailedViewPage and initialises the onCloseWindowRequest.
     *
     * @param stage the current fxml stage
     */
    public void init(Stage stage) {
        stage.setOnCloseRequest(windowEvent -> closeWindow());
    }

    /**
     * Initializes DetailedViewPage.
     */
    @FXML
    private void initialize() {
        OpenWindowsService.getInstance().addWindow(this);

        Wine selectedWine = WineService.getInstance().getSelectedWine();
        selectedWineId = selectedWine.getId();
        reviewDAO = new ReviewDAO();
        review = null;

        initWineInfo(selectedWine);
        initUserReviews();
        initAdminActions();
    }

    /**
     * Init all the labels with the wine information of the selected wine.
     *
     * @param selectedWine the selected wine object
     */
    private void initWineInfo(Wine selectedWine) {
        if (selectedWine != null) {
            nameLabel.setText(selectedWine.getName());
            priceLabel.setText("Price: $" + selectedWine.getPrice());
            yearLabel.setText("Year: " + selectedWine.getYear());
            ratingLabel.setText("Score: " + selectedWine.getRating());
            wineDescriptionLabel.setText(selectedWine.getDescription());
            provinceLabel.setText("Province: " + selectedWine.getRegion());
            varietyLabel.setText("Variety: "
                    + selectedWine.getWineColour()
                    + " - "
                    + selectedWine.getWineVariety());
            setColourImage(selectedWine);
            vineyardLabel.setText("Vineyard: " + selectedWine.getVineyard().getName());
        }
    }

    /**
     * Initialise user specific review info and tags if applicable.
     */
    private void initUserReviews() {
        if (UserService.getInstance().getCurrentUser() != null) {
            // Get the user ID
            userId = UserService.getInstance().getCurrentUser().getId();

            // Setup review stuff
            review = reviewDAO.getWineReview(selectedWineId, userId);

            tagsDAO = new TagsDAO();
            tagsList = new ArrayList<>();

            // Load Existing Tags
            assignedTagsDAO = new AssignedTagsDAO();
            List<AssignedTag> assignedTags = assignedTagsDAO.getAllAssigned(selectedWineId, userId);

            // Add "adder" tag
            addLabel = addBasicTag("+");

            // Create a new tag for each assigned tag
            for (AssignedTag tag : assignedTags) {
                Tag createdTag = tagsDAO.getOne(tag.getTagId());
                addTag(createdTag);
            }

            // Done Loading Tags
            logInMessageLabel.setVisible(false);
            logInMessageLabel.setManaged(false);
            addTagLabel.setVisible(false);

            noteTextArea.setDisable(false);
            ratingStars.setDisable(false);

            if (review != null) {
                noteTextArea.setText(review.getNotes());
                updateStarDisplay(review.getRating());
            }
        } else {
            logInMessageLabel.setVisible(true);
            addTagLabel.setVisible(true);
            noteTextArea.setDisable(true);
            ratingStars.setDisable(true);
        }
    }

    /**
     * sets the image to the corresponding colour.
     * red, white, rose or unknown
     * helps with usability and accessibility
     */
    private void setColourImage(Wine selectedWine) {
        switch (selectedWine.getWineColour()) {
            case "Red" -> wineColourImage.setImage(
                    new Image(Objects.requireNonNull(
                            this.getClass().getResourceAsStream("/images/redColourWine.png"))));
            case "White" -> wineColourImage.setImage(
                    new Image(Objects.requireNonNull(
                            this.getClass().getResourceAsStream("/images/whiteColourWine.png"))));
            case "Rosé" -> wineColourImage.setImage(
                    new Image(Objects.requireNonNull(
                            this.getClass().getResourceAsStream("/images/roseColourWine.png"))));
            default -> wineColourImage.setImage(
                    new Image(Objects.requireNonNull(
                            this.getClass().getResourceAsStream("/images/unknownColourWine.png"))));
        }
    }

    /**
     * Initialise the button for the admin to edit a wine.
     */
    private void initAdminActions() {
        if (UserService.getInstance().getCurrentUser() != null
                && UserService.getInstance().getCurrentUser().getIsAdmin()) {
            Button editWineButton = new Button("Edit Wine");
            editWineButton.getStyleClass().add("detailed_view");
            editWineButton.setOnAction(this::editWine);
            headerButtonContainer.getChildren().add(editWineButton);
        }
    }

    /**
     * Called when the admin clicks the edit wine button.
     *
     * @param event the ActionEvent associated with the button click
     */
    private void editWine(ActionEvent event) {
        Wine selectedWine = WineService.getInstance().getSelectedWine();
        try {
            FXMLLoader editWineLoader = new FXMLLoader(getClass()
                    .getResource("/fxml/EditWinePopup.fxml"));
            Parent root = editWineLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMinHeight(486);
            stage.setMinWidth(762);
            stage.setTitle(String.format("Edit wine %s", selectedWine.getName()));
            String styleSheetUrl = MainWindow.styleSheet;
            scene.getStylesheets().add(styleSheetUrl);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();
            initWineInfo(selectedWine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Show a popover to select a new tag.
     */
    @FXML
    public void showTagPopover() {
        if (UserService.getInstance().getCurrentUser() != null) {
            if (tagPopover == null || !tagPopover.isShowing()) {
                canAddTag = true;

                try {
                    FXMLLoader baseLoader = new FXMLLoader(
                            getClass().getResource("/fxml/TagPopover.fxml"));
                    Node content = baseLoader.load();

                    // Create the Popup
                    tagPopover = new PopOver(content);
                    tagPopover.getStyleClass().add("popover");
                    tagPopover.setDetachable(false);
                    tagPopover.setAutoHide(true);
                    tagPopover.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

                    // Set the Popup to close when clicking outside
                    tagPopover.setAutoHide(true);

                    VBox existingBox = (VBox) baseLoader.getNamespace().get("existingBox");
                    List<Tag> tags = tagsDAO.getFromUser(userId);
                    List<Label> labels = new ArrayList<>();

                    for (Tag tag : tags) {
                        /*
                         Can't use .contains because tag ID will be
                         different (with default ones) : (
                         */
                        boolean found = false;
                        for (Tag existingTag : tagsList) {
                            if (existingTag.getTagId() == tag.getTagId()) {
                                found = true;
                                break;
                            }
                        }

                        // Skip if it was found
                        if (found) {
                            continue;
                        }

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
    public void addTag(Tag tag) {
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
                Tag newTag = new Tag(userId, customTag, rand.nextInt(6));

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
     * Handles creating a new review if the review doesn't exist - called if a user edits anything
     * and a review doesn't already exist.
     */
    private void createReviewIfNotExists() {
        if (review == null && UserService.getInstance().getCurrentUser() != null) {
            review = new Review(selectedWineId, userId);
            try {
                reviewDAO.add(review);
            } catch (DuplicateEntryException e) {
                log.error(e);
            }
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

        createReviewIfNotExists();

        if (review != null) {
            review.setRating(clickedStarIndex);
            updateStarDisplay(clickedStarIndex);
        }
    }

    /**
     * Closes the page.
     */
    @FXML
    @Override
    public void closeWindow() {
        OpenWindowsService.getInstance().closeWindow(this);
        close();
    }


    /**
     * Closes the page and updates the review if necessary.
     */
    public void close() {
        try {
            if (UserService.getInstance().getCurrentUser() != null) {
                if (assignedTagsDAO != null) {

                    assignedTagsDAO.deleteFromUserWineId(userId, selectedWineId);

                    // Add review to this wine
                    if (!tagsList.isEmpty() || !noteTextArea.getText().isEmpty()) {
                        createReviewIfNotExists();
                    }

                    // Add to assigned tags db
                    for (Tag tag : tagsList) {
                        assignedTagsDAO.add(new AssignedTag(tag.getTagId(),
                                userId, selectedWineId));
                    }

                    // Set the review text
                    if (review != null) {
                        review.setNotes(noteTextArea.getText());
                    }
                }
            }
        } catch (DuplicateEntryException e) {
            throw new RuntimeException(e);
        }
        backButton.getScene().getWindow().hide();

        // Show update message if it was updated
        if (review != null) {
            addNotification("Updated Wine Review", "#d5e958");
        }
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
}
