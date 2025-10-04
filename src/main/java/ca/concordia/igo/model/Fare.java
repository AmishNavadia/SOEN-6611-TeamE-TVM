package ca.concordia.igo.model;

import java.util.Objects;

/**
 * Represents a fare with origin, destination, and trip type.
 */
public record Fare(Zone origin, Zone destination, TripType tripType) {
    public Fare(Zone origin, Zone destination, TripType tripType) {
        this.origin = Objects.requireNonNull(origin, "Origin cannot be null");
        this.destination = Objects.requireNonNull(destination, "Destination cannot be null");
        this.tripType = Objects.requireNonNull(tripType, "Trip type cannot be null");
    }

    @Override
    public String toString() {
        return String.format("%s → %s (%s)",
                origin.zoneName(),
                destination.zoneName(),
                tripType.getDisplayName());
    }
}

