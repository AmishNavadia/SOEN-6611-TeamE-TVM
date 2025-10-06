package ca.concordia.igo.exception;

import ca.concordia.igo.util.Language;

/**
 * Exception thrown when the printer is unavailable.
 */
public class PrinterUnavailableException extends IGoException {
    public PrinterUnavailableException(String message) {
        super(message);
    }

    public String getUserMessage(Language lang) {
        return lang == Language.FR ?
                "Imprimante hors service. Transaction enregistrée. Veuillez contacter le personnel." :
                "Printer unavailable. Transaction saved. Please contact staff.";
    }
}
