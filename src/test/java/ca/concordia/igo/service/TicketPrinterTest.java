package ca.concordia.igo.service;

import ca.concordia.igo.exception.PrinterUnavailableException;
import ca.concordia.igo.model.Fare;
import ca.concordia.igo.model.Ticket;
import ca.concordia.igo.model.TripType;
import ca.concordia.igo.model.Zone;
import ca.concordia.igo.util.Language;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketPrinterTest {

    @Test
    void printTicketReturnsFormattedReceipt() throws PrinterUnavailableException {
        TicketPrinter printer = new TicketPrinter();
        Fare fare = new Fare(Zone.ZONE_1, Zone.ZONE_3, TripType.RETURN);
        Ticket ticket = new Ticket(fare, 12.40, LocalDateTime.now().plusHours(3));

        long approvalTimestamp = System.currentTimeMillis();
        String receipt = printer.printTicket(ticket, Language.EN, approvalTimestamp);

        assertTrue(receipt.contains("PRESTO - TRANSIT TICKET"));
        assertTrue(receipt.contains(ticket.getTicketId().substring(0, 8)));
        assertTrue(receipt.contains("Toronto \u2192 Oakville (Return Trip)"));
        assertTrue(receipt.contains("12.40"));
    }

    @Test
    void printTicketThrowsWhenPrinterUnavailable() {
        TicketPrinter printer = new TicketPrinter();
        printer.setPrinterAvailable(false);
        Fare fare = new Fare(Zone.ZONE_2, Zone.ZONE_4, TripType.DAY_PASS);
        Ticket ticket = new Ticket(fare, 13.50, LocalDateTime.now().plusDays(1));

        assertThrows(PrinterUnavailableException.class, () -> printer.printTicket(ticket, Language.FR));
    }
}
