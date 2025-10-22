package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationExceptionTest {

    @Test
    void wrapsValidationMessageWithLanguagePrefix() {
        ValidationException exception = new ValidationException("Invalid postal code");

        assertTrue(exception.getMessage().contains("Invalid postal code"));
        assertEquals("Validation error: Invalid postal code",
                exception.getUserMessage(Language.EN));
        assertEquals("Erreur de validation: Invalid postal code",
                exception.getUserMessage(Language.FR));
    }
}
