package ca.concordia.igo.service;

import ca.concordia.igo.exception.PrinterUnavailableException;
import ca.concordia.igo.model.Ticket;
import ca.concordia.igo.util.Language;

import java.time.format.DateTimeFormatter;

public class TicketPrinter {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private boolean printerAvailable = true; // Default: available

    public boolean isPrinterAvailable() {
        return printerAvailable;
    }

    public void setPrinterAvailable(boolean available) {
        this.printerAvailable = available;
    }

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

