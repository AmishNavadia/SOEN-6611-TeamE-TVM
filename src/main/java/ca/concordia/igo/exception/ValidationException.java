package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;

/**
 * Exception thrown when user input or data fails validation.
 * <p>
 * This is a general-purpose validation exception for situations like:
 * - Invalid card number format
 * - Out-of-range amounts
 * - Invalid zone combinations
 * - Missing required fields
 * - Business rule violations
 * </p>
 * Unlike other exceptions, this one doesn't hide the technical message
 * from users - it's meant for validation errors that users can fix
 * (e.g., "Card number must be 10 digits").
 * For security-sensitive validation (authentication, authorization),
 * use more specific exception types that don't expose system details.
 */
public class ValidationException extends IGoException {
    /**
     * Creates a validation exception with a descriptive message.
     * <p>
     * The message should clearly explain what validation failed and
     * ideally how to fix it. This message will be shown to users,
     * so keep it friendly and actionable.
     * </p>
     * Good messages:
     * - "Card number must be 10 digits"
     * - "Recharge amount must be between $5 and $1000"
     * - "Origin and destination cannot be the same zone"
     * Avoid technical jargon:
     * - Not: "Regex validation failed for field cardNumber"
     * - Instead: "Card number format is invalid"
     *
     * @param message user-friendly validation error message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Returns the validation error message for display.
     * <p>
     * Unlike other exceptions, this simply prefixes the original message
     * with "Validation error": rather than replacing it entirely.
     * </p>
     * This works because validation messages are already user-friendly
     * and should be shown to help users correct their input.
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
