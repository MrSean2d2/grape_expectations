package seng202.team5.gui;

import java.io.IOException;
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
}
