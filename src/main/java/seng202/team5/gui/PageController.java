package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.core.config.Node;

/**
 * Controller for swapping between pages.
 */
public class PageController {
    private HeaderController headerController;
    @FXML
    private StackPane pageContainer;

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
        try {
            headerController.loadPage(fxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
