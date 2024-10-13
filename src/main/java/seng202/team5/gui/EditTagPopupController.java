package seng202.team5.gui;

import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import seng202.team5.models.Tag;
import seng202.team5.repository.TagsDAO;
import seng202.team5.services.ColourLookupService;
import seng202.team5.services.OpenWindowsService;
import seng202.team5.services.TagService;
import seng202.team5.services.UserService;

/**
 * A controller for the edit tag popup window.
 *
 * @author Martyn Gascoigne
 * @author Sean Reitsma
 */
public class EditTagPopupController extends FormErrorController implements ClosableWindow {

    @FXML
    public Button submitButton;

    @FXML
    private Button closeButton;

    @FXML
    private FlowPane tagColourBox;

    @FXML
    private Label nameErrorLabel;

    @FXML
    private TextField nameField;

    @FXML
    private Label tagLabel;

    @FXML
    private Label tagPreview;

    @FXML
    private Button deleteButton;

    private Tag tag;
    private boolean isTagValid = true;
    private TagService tagService;
    private TagsDAO tagsDAO;
    private String originalName;

    private int tagColourId;

    /**
     * Init the labels with information about the current wine.
     */
    private void initLabels() {
        String tagName = tag.getName();

        tagLabel.setText("Edit tag: " + tagName);
        nameField.setText(tagName);

        // Keep track of the original name
        originalName = tagName;
    }


    /**
     * Initialize the window.
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        int minWidth = 762;
        stage.setMinWidth(minWidth);
        int minHeight = 486;
        stage.setMinHeight(minHeight);
    }

    /**
     * Initialise the edit wine popup.
     */
    @FXML
    private void initialize() {
        OpenWindowsService.getInstance().addWindow(this);
        tagService = TagService.getInstance();
        tag = tagService.getSelectedTag();

        closeButton.setTooltip(new Tooltip("Close Window"));
        deleteButton.setTooltip(new Tooltip("Delete tag"));
        submitButton.setTooltip(new Tooltip("Submit changes"));

        tagsDAO = new TagsDAO();

        // Default colour options
        for (int i = 0; i < 6; i++) {
            String colourName = ColourLookupService.getTagLabelColour(i);
            int index = i;
            Label colourLabel = new Label(colourName.substring(0, 1).toUpperCase()
                    + colourName.substring(1));
            colourLabel.getStyleClass().addAll("tag", colourName);
            tagColourBox.getChildren().add(colourLabel);

            colourLabel.setOnMouseClicked(mouseEvent -> {
                tagColourId = index;
                updatePreview();
            });
        }

        // On name change, update the preview
        nameField.textProperty().addListener((observable, oldValue, newValue) -> updatePreview());

        // Check if we're editing a tag
        if (tag != null) {
            initLabels();
            tagColourId = tag.getColour();
        } else {
            // Button to delete tag should only be shown if we're editing one
            deleteButton.setVisible(false);
        }

        updatePreview();
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
        isTagValid = false;
    }

    /**
     * Reset the error state and hide all the error labels.
     */
    private void resetErrors() {
        isTagValid = true;
        nameErrorLabel.setVisible(false);
        nameField.getStyleClass().remove("field_error");
    }

    /**
     * Update the tag preview.
     */
    private void updatePreview() {
        tagPreview.getStyleClass().clear();
        tagPreview.getStyleClass().addAll("tag",
                ColourLookupService.getTagLabelColour(tagColourId));
        tagPreview.setText(nameField.getText());
    }

    /**
     * Submit the form, perform error validation and edit the wine, updating the
     * database if everything is valid.
     */
    @FXML
    private void submit() {
        resetErrors();
        String name = nameField.getText();
        int userId = UserService.getInstance().getCurrentUser().getId();
        showErrors(name, userId);

        // Check validity of the tag
        if (isTagValid) {
            if (tag == null) {
                tag = tagService.createTag(userId, name, tagColourId);
                closeWindow();
            } else {
                tag.setName(name);
                tag.setColour(tagColourId);
                tagsDAO.update(tag);
                closeWindow();
            }
        }
    }

    /**
     * Delete the tag we're editing.
     * We can only do this if we're editing a tag.
     */
    @FXML
    private void delete() {
        // Check if tag exists
        if (tag != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Tag?");
            alert.setHeaderText("Are you sure you want to delete this tag?");
            alert.setContentText("This action is irreversible!");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Close and delete
                tagsDAO.delete(tag.getTagId());
                closeWindow();
            }
        }
    }


    /**
     * Validate the fields which need validating and show error
     * messages if any are invalid.
     *
     * @param name the name field (shouldn't be blank)
     * @param userId the userID of who's editing the tag
     */
    private void showErrors(String name, int userId) {
        String nameError = tagService.checkName(name);
        if (nameError != null) {
            fieldError(nameError, nameField, nameErrorLabel);
            isTagValid = false;
        }

        if (tagService.checkTagExists(name, userId) && (!name.equals(originalName))) {
            fieldError("A tag with this name already exists!",
                    nameField, nameErrorLabel);
            isTagValid = false;
        }
    }
}
