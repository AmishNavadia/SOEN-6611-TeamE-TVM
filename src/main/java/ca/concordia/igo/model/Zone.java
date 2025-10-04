package ca.concordia.igo.model;

import java.util.Objects;

/**
 * Represents a transit zone for fare calculation.
 */
public record Zone(String zoneId, String zoneName) {
    public static final Zone ZONE_1 = new Zone("Z1", "Toronto");
    public static final Zone ZONE_2 = new Zone("Z2", "Mississauga");
    public static final Zone ZONE_3 = new Zone("Z3", "Oakville");
    public static final Zone ZONE_4 = new Zone("Z4", "Hamilton");

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return Objects.equals(zoneId, zone.zoneId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zoneId);
    }

    @Override
    public String toString() {
        return zoneName;
    }
}
