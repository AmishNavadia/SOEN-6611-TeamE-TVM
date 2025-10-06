package ca.concordia.igo.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a transit zone for fare calculation.
 */
public record Zone(String zoneId, String zoneName) {

    // Predefined zones for the GO Transit system
    // Zone numbers generally increase with distance from Toronto

    public static final Zone ZONE_1 = new Zone("Z1", "Toronto");
    public static final Zone ZONE_2 = new Zone("Z2", "Mississauga");
    public static final Zone ZONE_3 = new Zone("Z3", "Oakville");
    public static final Zone ZONE_4 = new Zone("Z4", "Hamilton");

    /**
     * Compares zones based on zone ID only.
     *
     * @param o the object to compare with
     * @return true if the zone IDs match, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return Objects.equals(zoneId, zone.zoneId);
    }

    /**
     * Returns hash code based on zone ID only.
     *
     * @return hash code of the zone ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(zoneId);
    }

    /**
     * Returns the zone's display name.
     *
     * @return the zone name (e.g., "Toronto", "Hamilton")
     */
    @NotNull
    @Override
    public String toString() {
        return zoneName;
    }
}
