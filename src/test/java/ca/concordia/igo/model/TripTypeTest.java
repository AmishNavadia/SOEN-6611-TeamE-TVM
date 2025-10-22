package ca.concordia.igo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TripTypeTest {

    @Test
    void tripTypesExposeDisplayNamesAndDurations() {
        assertEquals("Single Trip", TripType.SINGLE.getDisplayName());
        assertEquals("Return Trip", TripType.RETURN.getDisplayName());
        assertEquals(2, TripType.SINGLE.getValidityDuration().toHours());
        assertEquals(1, TripType.DAY_PASS.getValidityDuration().toDays());
        assertEquals(30, TripType.MONTHLY_PASS.getValidityDuration().toDays());
    }
}
