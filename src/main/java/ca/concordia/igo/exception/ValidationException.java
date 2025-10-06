package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;

/**
 * Exception thrown when user input or data fails validation.
 */
public class ValidationException extends IGoException {
    /**
     * Creates a validation exception with a descriptive message.
     *
     * @param message user-friendly validation error message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Returns the validation error message for display.
     *
     * @param lang the language for the prefix (EN or FR)
     * @return validation error with the language-appropriate prefix
     */
    public String getUserMessage(Language lang) {
        return lang == Language.FR ?
                "Erreur de validation: " + getMessage() :
                "Validation error: " + getMessage();
    }
}
