package seng202.team5.gui;

import javafx.scene.control.TextField;

/**
 * An interface for gui controllers which implement error messages for form
 * fields.
 *
 * @author Sean Reitsma
 */
public interface HasFormErrors {


    /**
     * Show a field error for the specified field, with an error message.
     *
     * @param field   the field containing the error
     * @param message the error message
     */
    void fieldError(TextField field, String message);

    /**
     * Show a field error for the specified field, without an error message.
     *
     * @param field the field containing the error
     */
    void fieldError(TextField field);

    /**
     * Reset the error status of a given field.
     *
     * @param field the field to reset
     */
    void resetFieldError(TextField field);
}
