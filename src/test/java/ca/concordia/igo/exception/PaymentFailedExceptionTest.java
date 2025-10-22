package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentFailedExceptionTest {

    @Test
    void exposesTransactionIdAndMessages() {
        PaymentFailedException exception =
                new PaymentFailedException("txn-123", "Gateway timeout");

        assertEquals("txn-123", exception.getTransactionId());
        assertTrue(exception.getMessage().contains("Gateway timeout"));
        assertEquals("Payment failed. Please try again or use another payment method.",
                exception.getUserMessage(Language.EN));
        assertEquals("Le paiement a échoué. Veuillez réessayer ou utiliser un autre mode de paiement.",
                exception.getUserMessage(Language.FR));
    }
}
