package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;

/**
 * Exception thrown during validation errors.
 */
public class ValidationException extends IGoException {
    public ValidationException(String message) {
        super(message);
    }

    public String getUserMessage(Language lang) {
        return lang == Language.FR ?
                "Erreur de validation: " + getMessage() :
                "Validation error: " + getMessage();
    }
}
