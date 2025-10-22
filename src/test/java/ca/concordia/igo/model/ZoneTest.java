package ca.concordia.igo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZoneTest {

    @Test
    void zonesCompareByIdentifier() {
        Zone zoneA = new Zone("Z9", "Custom");
        Zone zoneB = new Zone("Z9", "Other Name");
        Zone zoneC = new Zone("Z10", "Custom");

        assertEquals(zoneA, zoneB);
        assertEquals(zoneA.hashCode(), zoneB.hashCode());
        assertNotEquals(zoneA, zoneC);
    }

    @Test
    void toStringReturnsZoneName() {
        Zone zone = new Zone("Z5", "Testville");

        assertEquals("Testville", zone.toString());
    }
}
