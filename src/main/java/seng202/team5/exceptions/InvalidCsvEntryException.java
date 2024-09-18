package seng202.team5.exceptions;

/**
 * A custom exception to be thrown in the case an invalid entry in the CSV file
 * is encountered when loading.
 *
 * @author Sean Reitsma
 */
public class InvalidCsvEntryException extends Exception {
    /**
     * Basic constructor which simply calls the superconstructor.
     *
     * @param message the error message
     */
    public InvalidCsvEntryException(String message) {
        super(message);
    }
}
