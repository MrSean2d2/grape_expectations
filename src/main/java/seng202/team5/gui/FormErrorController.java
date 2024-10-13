package seng202.team5.gui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * A super class for page controllers which can handle form errors.
 *
 * @author Sean Reitsma
 */
public class FormErrorController extends PageController {
    /**
     * Show a field error for the specified field, with an error message.
     *
     * @param field the field containing the error
     * @param errorLabel the label to display
     * @param message the error message
     */
    protected void fieldError(TextField field, Label errorLabel, String message) {
        fieldError(field);
        errorLabel.setVisible(true);
        errorLabel.setText(message);
    }

    /**
     * Show a field error for the specified field, without an error message.
     *
     * @param field the field containing the error
     */
    protected void fieldError(TextField field) {
        field.getStyleClass().add("field_error");
    }

    /**
     * Reset the error status of a given field.
     *
     * @param field the field to reset
     */
    protected void resetFieldError(TextField field) {
        field.getStyleClass().remove("field_error");
    }
}
