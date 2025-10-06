package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;

/**
 * Exception thrown when the receipt printer is unavailable.
 * <p>
 * Printer failures can occur due to:
 * - Printer offline or disconnected
 * - Out of paper
 * - Paper jam
 * - Hardware malfunction
 * - Maintenance mode
 * </p>
 * This is a non-critical error - the transaction still succeeds even if
 * printing fails. The ticket is recorded in the system and the user can:
 * - Get help from staff to retrieve their receipt
 * - Use their payment confirmation as proof of purchase
 * - Access their ticket digitally if applicable
 * The user message emphasizes that the transaction WAS saved to prevent
 * users from attempting to purchase again.
 */
public class PrinterUnavailableException extends IGoException {

    /**
     * Creates a printer unavailable exception.
     *
     * @param message technical details about printer status (e.g., "Printer offline", "Out of paper")
     */
    public PrinterUnavailableException(String message) {
        super(message);
    }

    /**
     * Returns a reassuring message that explains the situation.
     * <p>
     * Key points in the message:
     * 1. Printer is unavailable (sets expectations)
     * 2. Transaction was saved (prevents duplicate purchases)
     * 3. Contact staff for help (provides a recovery path)
     * </p>
     * This prevents panic and double-charging while directing users to
     * staff who can manually print receipts or provide assistance.
     *
     * @param lang the language for the message (EN or FR)
     * @return reassuring message with staff contact instruction
     */
    public String getUserMessage(Language lang) {
        return lang == Language.FR ?
                "Imprimante hors service. Transaction enregistrée. Veuillez contacter le personnel." :
                "Printer unavailable. Transaction saved. Please contact staff.";
    }
}
