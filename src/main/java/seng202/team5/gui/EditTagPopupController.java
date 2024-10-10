package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.textfield.TextFields;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.exceptions.NotFoundException;
import seng202.team5.models.Tag;
import seng202.team5.models.User;
import seng202.team5.models.Vineyard;
import seng202.team5.models.Wine;
import seng202.team5.repository.TagsDAO;
import seng202.team5.repository.VineyardDAO;
import seng202.team5.repository.WineDAO;
import seng202.team5.services.*;

import java.util.List;
import java.util.Optional;

/**
 * A controller for the edit wine popup window.
 *
 * @author Sean Reitsma
 */
public class EditTagPopupController extends PageController implements ClosableWindow {

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

    private final int maxChars = 40;
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
     * Initialise the edit wine popup.
     */
    @FXML
    private void initialize() {
        OpenWindowsService.getInstance().addWindow(this);
        tagService = TagService.getInstance();
        tag = tagService.getSelectedTag();
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
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePreview();
        });

        // Check if we're editing a tag
        if (tag != null) {
            initLabels();

            tagColourId = tag.getColour();
            updatePreview();
        } else {
            // Button to delete tag should only be shown if we're editing one
            deleteButton.setVisible(false);
        }
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
     * Show a field error and set the current wine information as invalid.
     *
     * @param field the TextField containing the error
     */
    private void fieldError(TextField field) {
        field.getStyleClass().add("field_error");
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
     * Update the tag preview
     */
    private void updatePreview() {
        tagPreview.getStyleClass().clear();
        tagPreview.getStyleClass().addAll("tag", ColourLookupService.getTagLabelColour(tagColourId));
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
                tag = new Tag(userId, name, tagColourId);
                try {
                    tagsDAO.add(tag);
                    closeWindow();
                } catch (DuplicateEntryException e) {
                    // There was a duplicate entry
                }
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
        if (name.isEmpty()) {
            fieldError("Tag name can't be blank!", nameField, nameErrorLabel);
            isTagValid = false;
        } else {
            if (name.length() > maxChars) {
                fieldError("Tag name is too long!", nameField, nameErrorLabel);
                isTagValid = false;
            }
        }

        if (tagService.checkTagExists(name, userId) && (!name.equals(originalName))) {
            fieldError("A tag with this name already exists!", nameField, nameErrorLabel);
            isTagValid = false;
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
