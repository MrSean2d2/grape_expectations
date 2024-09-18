package seng202.team5.exceptions;

/**
 * NotFoundException to be thrown if content is not found in database.
 *
 * @author Martyn Gascoigne
 */
public class NotFoundException extends Exception {
    /**
     * Simple constructor that passes to parent Exception class.
     *
     * @param message error message
     */
    public NotFoundException(String message) {
        super(message);
    }
}
