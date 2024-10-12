package seng202.team5.gui;

import java.io.IOException;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller for swapping between pages.
 */
public class PageController {
    private static final Logger log = LogManager.getLogger(PageController.class);
    private HeaderController headerController;

    /**
     * Initialiser method. This implementation is intentionally left blank. Implementation is left
     * to those child classes which require separate initialisation than provided
     * by the FXML method (such as setting properties of the stage). It is defined
     * here so that openPopup can call it and if it is not required then simply
     * nothing will happen.
     *
     * @param stage the stage for this controller
     */
    public void init(Stage stage) {}

    /**
     * Method to set the header controller reference.
     */
    public void setHeaderController(HeaderController headerController) {
        this.headerController = headerController;
    }

    /**
     * Swap page to page from designated path.
     *
     * @param fxml the path to the file
     */
    public void swapPage(String fxml) {
        if (headerController != null) {
            try {
                headerController.loadPage(fxml);
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    /**
     * Add a notification to the main page.
     */
    public void addNotification(String text, String col) {
        if (headerController != null) {
            headerController.addNotification(text, col);
        }
    }

    /**
     * Return header controller.
     */
    public HeaderController getHeaderController() {
        return this.headerController;
    }

    /**
     * Open the popup to edit a user password.
     *
     * @param closable whether the popup should allow the user to close it
     * @param owner the window opening the popup
     */
    public void openEditPasswordPopup(boolean closable, Window owner) {
        try {
            FXMLLoader editPasswordLoader = new FXMLLoader(
                    getClass().getResource("/fxml/EditPasswordPopup.fxml"));
            Parent root = editPasswordLoader.load();
            EditPasswordPopupController controller = editPasswordLoader.getController();
            Stage stage = new Stage();
            stage.setTitle("Edit password");
            Scene scene = new Scene(root);
            String styleSheetUrl = MainWindow.styleSheet;
            scene.getStylesheets().add(styleSheetUrl);

            controller.init(stage, closable);

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(owner);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialise the stage as a modal window.
     *
     * @param owner The hierarchical owner of the stage
     * @param root the root element of the fxml document
     * @param controller the PageController for this stage
     * @param stage the stage itself
     */
    private void initStageModal(Window owner, Parent root, PageController controller, Stage stage) {
        Scene scene = new Scene(root);
        String styleSheetUrl = MainWindow.styleSheet;
        scene.getStylesheets().add(styleSheetUrl);
        controller.init(stage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Open the popup with the specified fxml file, title, and owner.
     *
     * @param fxml the fxml file of the popup window to load
     * @param title the title of the new stage
     * @param owner the hierarchical owner of the popup window, generally the window
     *              which created the popup
     */
    public void openPopup(String fxml, String title, Window owner) {
        try {
            FXMLLoader popupLoader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = popupLoader.load();
            PageController controller = popupLoader.getController();
            if (headerController != null) {
                controller.setHeaderController(headerController);
            }
            Stage stage = new Stage();
            stage.setTitle(title);
            initStageModal(owner, root, controller, stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Open the popup with the specified fxml file, title, and owner.
     *
     * @param fxml the fxml file of the popup window to load
     * @param titleProperty the Observable title of the new stage
     * @param owner the hierarchical owner of the popup window, generally the window
     *              which created the popup
     */
    public void openPopup(String fxml, ObservableValue<String> titleProperty, Window owner) {
        try {
            FXMLLoader popupLoader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = popupLoader.load();
            PageController controller = popupLoader.getController();
            if (headerController != null) {
                controller.setHeaderController(headerController);
            }
            Stage stage = new Stage();
            stage.titleProperty().bind(titleProperty);
            initStageModal(owner, root, controller, stage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
