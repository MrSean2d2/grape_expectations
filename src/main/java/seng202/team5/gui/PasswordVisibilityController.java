package seng202.team5.gui;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PasswordVisibilityController extends FormErrorController {
    /**
     * Set the visibility status of the text field passed, and inversely set the
     * visibility of the password field passed.
     *
     * @param visible true to set textField visible and passField not visible
     * @param passField the password field
     * @param textField the plain text field
     */
    protected void setVisible(boolean visible, PasswordField passField,
                            TextField textField) {
        textField.setVisible(visible);
        textField.setManaged(visible);
        passField.setVisible(!visible);
        passField.setManaged(!visible);
    }
}
