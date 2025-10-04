package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;

/**
 * Exception thrown when payment fails.
 */
public class PaymentFailedException extends IGoException {
    private final String transactionId;

    public PaymentFailedException(String transactionId, String message) {
        super(message);
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getUserMessage(Language lang) {
        return lang == Language.FR ?
                "Le paiement a échoué. Veuillez réessayer ou utiliser un autre mode de paiement." :
                "Payment failed. Please try again or use another payment method.";
    }
}

