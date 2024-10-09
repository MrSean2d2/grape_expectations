package seng202.team5.gui;

import javafx.fxml.FXML;
import seng202.team5.services.WineService;

/**
 * controller for the home page.
 */
public class HomePageController extends PageController {

    @FXML
    private void initialize() {
        WineService.getInstance().getWineList();
    }
}