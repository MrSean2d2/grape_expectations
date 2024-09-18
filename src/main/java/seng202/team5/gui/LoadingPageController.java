package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

/**
 * controller for the loading page
 * contains a spinning gif.
 */
public class LoadingPageController {

    @FXML
    private BorderPane spinnerLocation;


    /**
     * Initialize the loading page.
     */
    @FXML
    public void initialize() {

        Image gifImage = new Image(getClass().getResourceAsStream("/images/animation.gif"));

        // Create an ImageView to display the GIF
        ImageView gifView = new ImageView(gifImage);

        // Optionally, set some properties (e.g., fit width/height)
        gifView.setFitWidth(100);
        gifView.setPreserveRatio(true);
        gifView.setStyle("-fx-border-radius: 100");

        spinnerLocation.setCenter(gifView);
    }
}
