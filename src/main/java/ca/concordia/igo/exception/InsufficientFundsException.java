package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;

/**
 * Exception thrown when there are insufficient funds.
 */
public class InsufficientFundsException extends IGoException {
    private final double required;
    private final double available;

    public InsufficientFundsException(double required, double available) {
        super(String.format("Insufficient funds: required %.2f, available %.2f",
                required, available));
        this.required = required;
        this.available = available;
    }

    public String getUserMessage(Language lang) {
        String format = lang == Language.FR ?
                "Fonds insuffisants. Requis: %.2f$, Disponible: %.2f$" :
                "Insufficient funds. Required: $%.2f, Available: $%.2f";
        return String.format(format, required, available);
    }
}
