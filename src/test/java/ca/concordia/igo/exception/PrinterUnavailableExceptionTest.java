package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrinterUnavailableExceptionTest {

    @Test
    void providesFriendlyMessageInBothLanguages() {
        PrinterUnavailableException exception =
                new PrinterUnavailableException("Printer offline");

        assertTrue(exception.getMessage().contains("Printer offline"));
        assertEquals("Printer unavailable. Transaction saved. Please contact staff.",
                exception.getUserMessage(Language.EN));
        assertEquals("Imprimante hors service. Transaction enregistrée. Veuillez contacter le personnel.",
                exception.getUserMessage(Language.FR));
    }
}
