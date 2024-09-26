package seng202.team5.exceptions;

/**
 * NotFoundException to be thrown if duplicate entries in database.
 *
 * @author Martyn Gascoigne
 */
public class PasswordIncorrectException extends Exception {
    /**
     * Simple constructor that passes to parent Exception class.
     *
     * @param message error message
     */
    public PasswordIncorrectException(String message) {
        super(message);
    }
}
