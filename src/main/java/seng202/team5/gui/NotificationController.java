package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

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
     * Set the notification's alert colour.
     *
     * @param col the new hex colour
     */
    public void setColourBand(String col) {
        // Append colour onto the end of the existing style
        String currentStyle = colourBand.getStyle();
        String newStyle = currentStyle + "; -fx-background-color: " + col + ";";
        colourBand.setStyle(newStyle);
    }

    /**
     * Set the notification's text message.
     *
     * @param text the new text
     */
    public void setText(String text) {
        notificationLabel.setText(text);
    }
}