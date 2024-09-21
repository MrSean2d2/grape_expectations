package seng202.team5.gui;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * controller for the home page.
 */
public class NotificationController extends PageController {

    @FXML
    private AnchorPane colourBand;

    @FXML
    private Label notificationLabel;

    /**
     * Initialize the notification.
     */
    @FXML
    private void initialize() {


    }

    /**
     * Set the notification's alert colour
     *
     * @param col the new hex colour
     */
    public void setColourBand(String col) {
        colourBand.setStyle("-fx-background-radius: 15; -fx-background-color: " + col + ";");
    }

    /**
     * Set the notification's text message
     *
     * @param text the new text
     */
    public void setText(String text) {
        notificationLabel.setText(text);
    }
}