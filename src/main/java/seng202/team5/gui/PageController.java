package seng202.team5.gui;

/**
 * Controller for swapping between pages.
 */
public class PageController {
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
        try {
            headerController.loadPage(fxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
