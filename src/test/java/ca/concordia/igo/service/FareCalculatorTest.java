package ca.concordia.igo.service;

import ca.concordia.igo.model.Fare;
import ca.concordia.igo.model.TripType;
import ca.concordia.igo.model.Zone;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FareCalculatorTest {

    private final FareCalculator calculator = new FareCalculator();

    @Test
    void calculatesSingleFareWithinSameZone() {
        Fare fare = new Fare(Zone.ZONE_1, Zone.ZONE_1, TripType.SINGLE);

        assertEquals(3.25, calculator.calculateAmount(fare), 1e-6);
    }

    @Test
    void calculatesSingleFareAcrossMultipleZones() {
        Fare fare = new Fare(Zone.ZONE_1, Zone.ZONE_3, TripType.SINGLE);

        double expected = 3.25 + (2 * 3.25 * 1.15);
        assertEquals(expected, calculator.calculateAmount(fare), 1e-6);
    }

    @Test
    void calculatesReturnTripWithDiscount() {
        Fare fare = new Fare(Zone.ZONE_2, Zone.ZONE_4, TripType.RETURN);
        double base = 3.25 + (2 * 3.25 * 1.15);

        assertEquals(base * 1.8, calculator.calculateAmount(fare), 1e-6);
    }

    @Test
    void calculatesFlatRatesForPasses() {
        Fare dayPass = new Fare(Zone.ZONE_1, Zone.ZONE_2, TripType.DAY_PASS);
        Fare monthly = new Fare(Zone.ZONE_1, Zone.ZONE_4, TripType.MONTHLY_PASS);

        assertEquals(13.50, calculator.calculateAmount(dayPass), 1e-6);
        assertEquals(150.00, calculator.calculateAmount(monthly), 1e-6);
    }
}
