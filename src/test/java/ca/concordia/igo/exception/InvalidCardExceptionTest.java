package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidCardExceptionTest {

    @Test
    void returnsLocalizedMessagesForEachErrorCode() {
        InvalidCardException notDetected = new InvalidCardException(
                InvalidCardException.ErrorCode.CARD_NOT_DETECTED,
                "Card not detected"
        );
        InvalidCardException expired = new InvalidCardException(
                InvalidCardException.ErrorCode.CARD_EXPIRED,
                "Expired"
        );
        InvalidCardException inactive = new InvalidCardException(
                InvalidCardException.ErrorCode.CARD_INACTIVE,
                "Inactive"
        );
        InvalidCardException readError = new InvalidCardException(
                InvalidCardException.ErrorCode.CARD_READ_ERROR,
                "Read error"
        );

        assertEquals("Card not detected. Please tap again.",
                notDetected.getUserMessage(Language.EN));
        assertEquals("Carte non détectée. Veuillez taper à nouveau.",
                notDetected.getUserMessage(Language.FR));

        assertEquals("Card expired. Please use another card.",
                expired.getUserMessage(Language.EN));
        assertEquals("Carte expirée. Veuillez utiliser une autre carte.",
                expired.getUserMessage(Language.FR));

        assertEquals("Card inactive. Please contact customer service.",
                inactive.getUserMessage(Language.EN));
        assertEquals("Carte inactive. Veuillez contacter le service client.",
                inactive.getUserMessage(Language.FR));

        assertEquals("Error reading card.", readError.getUserMessage(Language.EN));
        assertEquals("Erreur lors de la lecture de la carte.",
                readError.getUserMessage(Language.FR));
    }
}
