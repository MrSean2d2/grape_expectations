package seng202.team5.exceptions;

/**
 * Exception to be thrown in the case of trying to create a singleton instance
 * when one already exists.
 *
 * @author Sean Reitsma
 * @author Morgan English (Advanced JavaFX lab)
 */
public class InstanceAlreadyExistsException extends Exception {
    /**
     * Simple constructor that just passes the message to the parent class.
     *
     * @param message the error message
     */
    public InstanceAlreadyExistsException(String message) {
        super(message);
    }
}
