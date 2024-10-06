package seng202.team5.gui;

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
}
