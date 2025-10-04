package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;

/**
 * Exception thrown when an invalid card is detected.
 */
public class InvalidCardException extends IGoException {
    private final ErrorCode code;

    public InvalidCardException(ErrorCode code, String technicalMsg) {
        super(technicalMsg);
        this.code = code;
    }

    public String getUserMessage(Language lang) {
        switch (code) {
            case CARD_NOT_DETECTED:
                return lang == Language.FR ?
                        "Carte non détectée. Veuillez taper à nouveau." :
                        "Card not detected. Please tap again.";
            case CARD_EXPIRED:
                return lang == Language.FR ?
                        "Carte expirée. Veuillez utiliser une autre carte." :
                        "Card expired. Please use another card.";
            case CARD_INACTIVE:
                return lang == Language.FR ?
                        "Carte inactive. Veuillez contacter le service client." :
                        "Card inactive. Please contact customer service.";
            default:
                return lang == Language.FR ?
                        "Erreur lors de la lecture de la carte." :
                        "Error reading card.";
        }
    }

    public enum ErrorCode {
        CARD_NOT_DETECTED,
        CARD_EXPIRED,
        CARD_INACTIVE,
        CARD_READ_ERROR
    }
}

