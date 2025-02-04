package seng202.team5.gui;

import static java.lang.Math.max;
import static seng202.team5.services.ColourLookupService.getTagLabelColour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
import seng202.team5.services.ReviewService;
import seng202.team5.services.TagService;
import seng202.team5.services.UserService;
import seng202.team5.services.WineService;

/**
 * Controller for the detailed view page.
 *
 * @author Finn Brown
 */
public class DetailedViewPageController extends PageController implements ClosableWindow {
    private final Image emptyStar = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/images/empty_star.png")));
    private final Image filledStar = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/images/filled_star.png")));

    private Label addLabel;
    private TagsDAO tagsDAO;
    private AssignedTagsDAO assignedTagsDAO;
    private List<Tag> tagsList;

    private final int maxReviewCharacters = 500;

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
    private Label colourLabel;

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
    private VBox existingTagBox;
    private ReviewDAO reviewDAO;
    private Review review;
    private ReviewService reviewService;


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
        reviewService = new ReviewService();
        backButton.setTooltip(new Tooltip("Close window"));
        Tooltip.install(ratingStars, new Tooltip("Rate the wine"));


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
            nameLabel.textProperty().bind(selectedWine.nameProperty());
            priceLabel.textProperty().bind(selectedWine.priceProperty().asString("Price: $%.2f"));
            yearLabel.textProperty().bind(selectedWine.yearProperty().asString("Year: %d"));
            ratingLabel.textProperty().bind(selectedWine.ratingValueProperty()
                    .asString("Score: %d/100"));
            wineDescriptionLabel.textProperty().bind(selectedWine.descriptionProperty());
            provinceLabel.textProperty().bind(selectedWine.vineyardProperty().map(
                    v -> String.format("Region: %s", v.getRegion())));

            varietyLabel.textProperty().bind(selectedWine.wineVarietyProperty().map(
                    v -> v
            ));

            colourLabel.textProperty().bind(selectedWine.colourProperty().map(
                    v -> String.format("Colour / Variety: %s - ", v)
            ));

            selectedWine.colourProperty().addListener(
                    (observableValue, s, t1) -> setColourImage(selectedWine));

            vineyardLabel.textProperty().bind(selectedWine.vineyardProperty()
                    .map(v -> String.format("Vineyard: %s", v.getName())));

            setColourImage(selectedWine);
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
            addLabel.setTooltip(new Tooltip("Add a tag"));

            // Create a new tag for each assigned tag
            for (AssignedTag tag : assignedTags) {
                Tag createdTag = tagsDAO.getOne(tag.getTagId());
                addTag(createdTag);
            }

            // Done Loading Tags
            logInMessageLabel.setVisible(false);
            logInMessageLabel.setManaged(false);
            addTagLabel.setVisible(false);
            addTagLabel.setManaged(false);

            noteTextArea.setDisable(false);
            noteTextArea.setTextFormatter(new TextFormatter<String>(change -> {
                if (change.getControlNewText().length() > maxReviewCharacters) {
                    return null;
                }
                return change;
            }));
            ratingStars.setDisable(false);

            if (review != null) {
                noteTextArea.setText(review.getNotes());
                updateStarDisplay(review.getRating());
            }
        } else {
            logInMessageLabel.setVisible(true);
            addTagLabel.setVisible(true);
            addTagLabel.setManaged(true);
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
            editWineButton.getStyleClass().addAll("button", "detailed_view");
            editWineButton.setOnAction(this::editWine);
            editWineButton.setTooltip(new Tooltip("Edit wine details"));
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
        openPopup("/fxml/EditWinePopup.fxml",
                selectedWine.nameProperty().map(n -> String.format("Edit Wine: %s", n)),
                backButton.getScene().getWindow());
    }

    /**
     * Show a popover to select a new tag.
     */
    @FXML
    public void showTagPopover() {
        if (UserService.getInstance().getCurrentUser() != null) {
            if (tagPopover == null || !tagPopover.isShowing()) {

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
                    existingTagBox = (VBox) baseLoader.getNamespace().get("existingBox");
                    addTagLabels();
                    content.lookup("#closeButton").setOnMouseClicked(event -> closePopOver());
                    Tooltip.install(content.lookup("#closeButton"),
                            new Tooltip("Close tag pop up"));

                    // Add a button to confirm selection
                    Button confirmButton = (Button) content.lookup("#createTagButton");
                    confirmButton.setTooltip(new Tooltip("Create a new tag"));
                    confirmButton.setOnAction(event -> {
                        TagService.getInstance().setSelectedTag(null);
                        openPopup("/fxml/EditTagPopup.fxml", "Create new Tag",
                                backButton.getScene().getWindow());

                        // Clear and refresh
                        Tag createdTag = TagService.getInstance().getCreatedTag();
                        if (createdTag != null) {
                            addTag(createdTag);
                            TagService.getInstance().setCreatedTag(null);

                            updateTags();
                        }

                        // Close the popover
                        closePopOver();
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
     * Add the tag labels to the popover.
     */
    public void addTagLabels() {
        List<Tag> tags = tagsDAO.getFromUser(userId);
        List<Label> labels = new ArrayList<>();

        existingTagBox.getChildren().clear();

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
            newTag.setTooltip(new Tooltip("Add " + tag.getName() + " tag"));
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

        // Add labels to the box
        existingTagBox.getChildren().addAll(labels);
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
     * @return reference to the created tag label
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

        review = reviewService.createReviewIfNotExists(review, selectedWineId, userId);

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
                        review = reviewService.createReviewIfNotExists(review,
                                selectedWineId, userId);
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
