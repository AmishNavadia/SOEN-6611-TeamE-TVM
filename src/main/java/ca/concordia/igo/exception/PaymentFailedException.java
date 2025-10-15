package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;
import ca.concordia.igo.util.Logger;

/**
 * Exception thrown when payment processing fails.
 */
public class PaymentFailedException extends IGoException {
    private final String transactionId;

    /**
     * Creates a payment failure exception.
     *
     * @param transactionId the ID of the failed transaction (for tracking/support)
     * @param message technical reason for failure (e.g., "Card declined", "Network timeout")
     */
    public PaymentFailedException(String transactionId, String message) {
        super(message);
        this.transactionId = transactionId;
        // LOG 8: Payment failure exception
        Logger.error("PaymentFailedException - Transaction: " + transactionId +
                ", Reason: " + message);
    }

    /**
     * Returns the transaction ID for the failed payment.
     * This ID can be:
     * - Displayed to the user for support calls
     * - Used to look up the failure in transaction logs
     * - Sent to payment processor for investigation
     *
     * @return the unique transaction identifier
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Returns a generic user-friendly error message.
     * It suggests recovery actions: try again or use different payment method.
     * For detailed failure info, check the technical message in logs using
     * the transaction ID.
     *
     * @param lang the language for the message (EN or FR)
     * @return generic payment failure message with suggested actions
     */
    public String getUserMessage(Language lang) {
        return lang == Language.FR ?
                "Le paiement a échoué. Veuillez réessayer ou utiliser un autre mode de paiement." :
                "Payment failed. Please try again or use another payment method.";
    }
}

