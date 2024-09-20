package seng202.team5.exceptions;

/**
 * NotFoundException to be thrown if content is not found in database.
 *
 * @author Martyn Gascoigne
 */
public class InvalidUserIdException extends Exception {
    /**
     * Simple constructor that passes to parent Exception class.
     *
     * @param message error message
     */
    public InvalidUserIdException(String message) {
        super(message);
    }
}
