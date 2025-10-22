package ca.concordia.igo.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @Test
    void ticketStoresProvidedDetails() {
        Fare fare = new Fare(Zone.ZONE_1, Zone.ZONE_2, TripType.RETURN);
        LocalDateTime validUntil = LocalDateTime.now().plusHours(2);
        Ticket ticket = new Ticket(fare, 8.75, validUntil);

        assertEquals(fare, ticket.getFare());
        assertEquals(8.75, ticket.getAmount());
        assertNotNull(ticket.getTicketId());
        assertNotNull(ticket.getIssuedAt());
        assertEquals(validUntil, ticket.getValidUntil());
    }

    @Test
    void isValidReturnsFalseWhenExpired() {
        Fare fare = new Fare(Zone.ZONE_2, Zone.ZONE_3, TripType.SINGLE);
        Ticket ticket = new Ticket(fare, 5.25, LocalDateTime.now().minusMinutes(1));

        assertFalse(ticket.isValid());
    }

    @Test
    void isValidReturnsTrueWhenActive() {
        Fare fare = new Fare(Zone.ZONE_3, Zone.ZONE_4, TripType.DAY_PASS);
        Ticket ticket = new Ticket(fare, 15.00, LocalDateTime.now().plusHours(1));

        assertTrue(ticket.isValid());
    }
}
