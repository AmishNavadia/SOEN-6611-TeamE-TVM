package ca.concordia.igo.exception;

/**
 * Base exception for all iGo exceptions.
 */
public class IGoException extends Exception {
    public IGoException(String message) {
        super(message);
    }

    public IGoException(String message, Throwable cause) {
        super(message, cause);
    }
}
