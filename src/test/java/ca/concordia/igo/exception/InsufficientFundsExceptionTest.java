package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InsufficientFundsExceptionTest {

    @Test
    void formatsTechnicalAndUserMessages() {
        InsufficientFundsException exception = new InsufficientFundsException(10.0, 4.5);

        assertTrue(exception.getMessage().contains("required 10.00"));
        assertEquals("Insufficient funds. Required: $10.00, Available: $4.50",
                exception.getUserMessage(Language.EN));
        assertEquals("Fonds insuffisants. Requis: 10.00$, Disponible: 4.50$",
                exception.getUserMessage(Language.FR));
    }
}
