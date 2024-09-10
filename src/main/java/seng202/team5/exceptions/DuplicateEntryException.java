package seng202.team5.exceptions;

/**
 * NotFoundException to be thrown if duplicate entries in database
 * @author Martyn Gascoigne
 */
public class DuplicateEntryException extends Exception {
    /**
     * Simple constructor that passes to parent Exception class
     * @param message error message
     */
    public DuplicateEntryException(String message) {
        super(message);
    }
}
