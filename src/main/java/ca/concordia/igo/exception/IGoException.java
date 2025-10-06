package ca.concordia.igo.exception;

/**
 * Base exception for all iGo system exceptions.
 * All subclasses should implement getUserMessage(Language) to provide
 * user-friendly error messages in English and French.
 */
public class IGoException extends Exception {

    /**
     * Creates an exception with a technical message.
     * @param message technical description of the error
     */
    public IGoException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a message and underlying cause.
     *
     * @param message technical description of the error
     * @param cause the underlying exception that caused this error
     */
    public IGoException(String message, Throwable cause) {
        super(message, cause);
    }
}
