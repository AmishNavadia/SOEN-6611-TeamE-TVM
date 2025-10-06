package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;

/**
 * Exception thrown when a PRESTO card has insufficient balance for a transaction.
 * <p>
 * This exception captures both the required amount and available balance,
 * allowing the UI to display helpful information to the user about:
 * - How much money they need
 * - How much they currently have
 * - How much more they need to add
 * Typical scenario:
 * User tries to buy a $5.00 ticket but only has $3.00 on their card.
 * The exception stores both values so the UI can suggest adding $2.00.
 * </p>
 * This is a recoverable error - user can add funds and retry.
 */
public class InsufficientFundsException extends IGoException {
    private final double required;
    private final double available;

    /**
     * Creates an insufficient funds exception with amount details.
     *
     * @param required the amount needed for the transaction
     * @param available the current card balance
     */
    public InsufficientFundsException(double required, double available) {
        super(String.format("Insufficient funds: required %.2f, available %.2f",
                required, available));
        this.required = required;
        this.available = available;
    }

    /**
     * Returns a user-friendly error message in the specified language.
     * <p>
     * The message includes both the required and available amounts, so
     * users know exactly how much more money they need to add.
     * </p>
     * Example (English): "Insufficient funds. Required: $5.00, Available: $3.00"
     * Example (French): "Fonds insuffisants. Requis: 5.00$, Disponible: 3.00$"
     *
     * @param lang the language for the message (EN or FR)
     * @return formatted error message for display to user
     */
    public String getUserMessage(Language lang) {
        String format = lang == Language.FR ?
                "Fonds insuffisants. Requis: %.2f$, Disponible: %.2f$" :
                "Insufficient funds. Required: $%.2f, Available: $%.2f";
        return String.format(format, required, available);
    }
}
