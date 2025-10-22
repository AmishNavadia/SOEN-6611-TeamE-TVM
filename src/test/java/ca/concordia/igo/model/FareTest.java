package ca.concordia.igo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FareTest {

    @Test
    void toStringFormatsFareDetails() {
        Fare fare = new Fare(Zone.ZONE_1, Zone.ZONE_4, TripType.SINGLE);

        assertEquals("Toronto \u2192 Hamilton (Single Trip)", fare.toString());
    }

    @Test
    void constructorRejectsNullOrigin() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> new Fare(null, Zone.ZONE_1, TripType.SINGLE)
        );
        assertTrue(ex.getMessage().contains("Origin"));
    }

    @Test
    void constructorRejectsNullDestination() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> new Fare(Zone.ZONE_1, null, TripType.SINGLE)
        );
        assertTrue(ex.getMessage().contains("Destination"));
    }

    @Test
    void constructorRejectsNullTripType() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> new Fare(Zone.ZONE_1, Zone.ZONE_2, null)
        );
        assertTrue(ex.getMessage().contains("Trip type"));
    }
}
