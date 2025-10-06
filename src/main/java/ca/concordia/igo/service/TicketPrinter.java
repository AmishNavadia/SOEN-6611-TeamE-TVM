package ca.concordia.igo.service;

import ca.concordia.igo.exception.PrinterUnavailableException;
import ca.concordia.igo.model.Ticket;
import ca.concordia.igo.util.Language;

import java.time.format.DateTimeFormatter;

/**
 * Service for printing transit ticket receipts.
 * <p>
 * Formats and prints receipts in English or French with all ticket details.
 * Printer status can be toggled for testing error handling.
 * </p>
 * Receipt includes:
 * - Ticket ID (abbreviated)
 * - Route (origin → destination, trip type)
 * - Amount paid
 * - Issue timestamp
 * - Validity period
 * In production, this would interface with actual thermal printer hardware.
 * Currently, returns formatted string for display/testing.
 */
public class TicketPrinter {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private boolean printerAvailable = true; // Default: available

    public boolean isPrinterAvailable() {
        return printerAvailable;
    }

    /**
     * Set printer availability status.
     * Used by maintenance service to simulate printer failures
     * for testing error handling.
     *
     * @param available true if printer is working, false to simulate failure
     */
    public void setPrinterAvailable(boolean available) {
        this.printerAvailable = available;
    }

    /**
     * Print a ticket receipt in the specified language.
     * <p>
     * Formats the ticket information as a receipt with borders and
     * bilingual support (English/French).
     * </p>
     *
     * The receipt is 40 characters wide to fit standard thermal printers.
     * Ticket ID is abbreviated (first 8 chars) to save space.
     *
     * @param ticket the ticket to print
     * @param lang language for the receipt (Language.EN or Language.FR)
     * @return formatted receipt as a string
     * @throws PrinterUnavailableException if printer is offline/unavailable
     */
    public String printTicket(Ticket ticket, Language lang) throws PrinterUnavailableException {
        if (!printerAvailable) {
            throw new PrinterUnavailableException("Printer is offline");
        }

        String receipt = "=".repeat(40) + "\n" +
                (lang == Language.FR ?
                        "       PRESTO - BILLET DE TRANSPORT\n" :
                        "       PRESTO - TRANSIT TICKET\n") +
                "=".repeat(40) + "\n\n" +
                String.format("%-20s %s\n",
                        lang == Language.FR ? "Numéro:" : "Ticket ID:",
                        ticket.getTicketId().substring(0, 8)) +
                String.format("%-20s %s\n",
                        lang == Language.FR ? "Trajet:" : "Route:",
                        ticket.getFare().toString()) +
                String.format("%-20s $%.2f\n",
                        lang == Language.FR ? "Montant:" : "Amount:",
                        ticket.getAmount()) +
                String.format("%-20s %s\n",
                        lang == Language.FR ? "Émis:" : "Issued:",
                        ticket.getIssuedAt().format(DATE_FORMAT)) +
                String.format("%-20s %s\n",
                        lang == Language.FR ? "Valide jusqu'à:" : "Valid until:",
                        ticket.getValidUntil().format(DATE_FORMAT)) +
                "\n" + "=".repeat(40) + "\n" +
                (lang == Language.FR ?
                        "    Merci d'utiliser PRESTO!\n" :
                        "    Thank you for using PRESTO!\n") +
                "=".repeat(40) + "\n";

        return receipt;
    }
}

