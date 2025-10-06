package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;

/**
 * Exception thrown when a PRESTO card has insufficient balance for a transaction.
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
