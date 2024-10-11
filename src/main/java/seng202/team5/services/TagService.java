package seng202.team5.services;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import seng202.team5.gui.EditTagPopupController;
import seng202.team5.gui.HeaderController;
import seng202.team5.gui.MainWindow;
import seng202.team5.models.Tag;
import seng202.team5.repository.TagsDAO;

/**
 * Service Class to manage wine actions.
 */
public class TagService {
    private Tag selectedTag;
    private Tag createdTag;
    private final TagsDAO tagsDAO;
    private static TagService instance;

    public TagService() {
        tagsDAO = new TagsDAO();
    }

    /**
     * Returns the singleton WineService instance.
     *
     * @return instance of wine service class
     */
    public static TagService getInstance() {
        if (instance == null) {
            instance = new TagService();
        }
        return instance;
    }

    /**
     * Sets selectedTag to the tag clicked on in DashboardPage.
     *
     * @param tag the selected tag
     */
    public void setSelectedTag(Tag tag) {
        this.selectedTag = tag;
    }

    /**
     * Returns the tag selected in DashboardPage.
     *
     * @return selectedTag
     */
    public Tag getSelectedTag() {
        return selectedTag;
    }

    /**
     * Sets createdTag to the created tag.
     *
     * @param tag the tag that was just created
     */
    public void setCreatedTag(Tag tag) {
        this.createdTag = tag;
    }

    /**
     * Returns the most recently createdTag.
     *
     * @return createTag
     */
    public Tag getCreatedTag() {
        return createdTag;
    }

    /**
     * Check if a tag with this name already exists.
     *
     * @return if the tag already exists
     */
    public boolean checkTagExists(String tagName, int userId) {
        // Get a list of the user's tags
        List<Tag> userCreatedTags = tagsDAO.getFromUser(userId);

        for (Tag userTag : userCreatedTags) {
            // Using custom "get and check"
            if (userTag.getName().equals(tagName)) {
                // Tag exists so return true
                return true;
            }
        }

        // Tag doesn't exist
        return false;
    }

    /**
     * Create a new tag popup. If the selected tag is null,
     * it will prompt the user to create a new tag.
     *
     * @param ownerWindow the window that owns the popup
     * @param headerController the header controller to use
     * @throws IOException if the page can't be loaded
     */
    public void showEditTagPopup(Window ownerWindow,
                                 HeaderController headerController) throws IOException {
        FXMLLoader editWineLoader = new FXMLLoader(getClass()
                .getResource("/fxml/EditTagPopup.fxml"));

        Parent root = editWineLoader.load();
        EditTagPopupController controller = editWineLoader.getController();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        controller.init(stage);
        controller.setHeaderController(headerController);

        Tag selectedTag = getSelectedTag();

        if (selectedTag != null) {
            stage.setTitle(String.format("Edit tag %s", selectedTag.getName()));
        } else {
            stage.setTitle("Create new Tag");
        }
        String styleSheetUrl = MainWindow.styleSheet;
        scene.getStylesheets().add(styleSheetUrl);
        stage.initOwner(ownerWindow);
        stage.initModality(Modality.WINDOW_MODAL);

        // Show the popup and wait for it to be closed
        stage.showAndWait();
    }
}
