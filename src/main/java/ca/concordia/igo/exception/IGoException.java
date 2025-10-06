package ca.concordia.igo.exception;

/**
 * Base exception for all iGo system exceptions.
 * <p>
 * All custom exceptions in the iGo kiosk system extend this class.
 * This provides a common exception type that can be caught to handle
 * any application-specific errors separately from standard Java exceptions.
 * Benefits:
 * - Catch all iGo exceptions with a single catch block if needed
 * - Distinguish between application errors and system errors (NullPointerException, etc.)
 * - Consistent exception handling across the application
 * </p>
 * All subclasses should implement getUserMessage(Language) to provide
 * user-friendly error messages in English and French.
 */
public class IGoException extends Exception {

    /**
     * Creates an exception with a technical message.
     * <p>
     * The message should describe what went wrong for developers/logs.
     * Use getUserMessage() in subclasses for customer-facing messages.
     * </p>
     * @param message technical description of the error
     */

    public IGoException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a message and underlying cause.
     * <p>
     * Use this when wrapping lower-level exceptions (IOException, SQLException, etc.)
     * to preserve the full error context for debugging.
     * </p>
     *
     * @param message technical description of the error
     * @param cause the underlying exception that caused this error
     */
    public IGoException(String message, Throwable cause) {
        super(message, cause);
    }
}
