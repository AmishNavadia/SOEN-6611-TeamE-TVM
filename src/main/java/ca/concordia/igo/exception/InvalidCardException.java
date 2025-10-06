package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;

/**
 * Exception thrown when a PRESTO card fails validation.
 * <p>
 * This exception uses error codes to distinguish between different types
 * of card problems, allowing the UI to display appropriate messages and
 * recovery actions for each situation.
 * </p>
 * Card validation failures:
 * - CARD_NOT_DETECTED: Card wasn't read properly (tap again)
 * - CARD_EXPIRED: Card past expiry date (get new card)
 * - CARD_INACTIVE: Card deactivated/reported lost (contact support)
 * - CARD_READ_ERROR: Hardware/communication problem (try again)
 * The technical message (getMessage()) goes to logs for debugging.
 * The user message (getUserMessage()) goes to the kiosk screen.
 */
public class InvalidCardException extends IGoException {
    private final ErrorCode code;

    /**
     * Creates an invalid card exception with an error code.
     *
     * @param code the specific reason the card is invalid
     * @param technicalMsg detailed error info for logs (e.g., "Card 1234567890 expired on 2023-05-15")
     */
    public InvalidCardException(ErrorCode code, String technicalMsg) {
        super(technicalMsg);
        this.code = code;
    }

    /**
     * Returns a user-friendly error message with recovery instructions.
     * <p>
     * Each error code has a specific message that tells the user:
     * - What went wrong
     * - What they should do to fix it
     * </p>
     *
     * Messages are bilingual (English/French) for accessibility.
     *
     * @param lang the language for the message (EN or FR)
     * @return formatted error message with instructions for user
     */
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

    /**
     * Error codes for different card validation failures.
     * <p>
     * Each code represents a distinct failure mode with different
     * user messaging and recovery paths:
     * </p>
     *
     * CARD_NOT_DETECTED: Physical read failure - usually transient, retry fixes it
     * CARD_EXPIRED: Card past 5-year expiry - permanent, need new card
     * CARD_INACTIVE: Card deactivated (lost/stolen report) - permanent, need new card
     * CARD_READ_ERROR: Generic read failure - could be hardware, software, or card damage
     */
    public enum ErrorCode {
        CARD_NOT_DETECTED,
        CARD_EXPIRED,
        CARD_INACTIVE,
        CARD_READ_ERROR
    }
}

