package ca.concordia.igo.model;

import java.util.Objects;

/**
 * Represents a fare with origin zone, destination zone, and trip type.
 * <p>
 * This is an immutable record used throughout the fare calculation system.
 * All fields are required and validated at construction time.
 * </p>
 */
public record Fare(Zone origin, Zone destination, TripType tripType) {

    /**
     * Creates a new Fare with validation.
     *
     * @param origin the starting zone - cannot be null
     * @param destination the ending zone - cannot be null
     * @param tripType the type of trip (one-way, round-trip, etc.) - cannot be null
     * @throws NullPointerException if any parameter is null
     */
    public Fare(Zone origin, Zone destination, TripType tripType) {
        this.origin = Objects.requireNonNull(origin, "Origin cannot be null");
        this.destination = Objects.requireNonNull(destination, "Destination cannot be null");
        this.tripType = Objects.requireNonNull(tripType, "Trip type cannot be null");
    }

    /**
     * Returns a human-readable representation of the fare.
     *
     * @return formatted string like "Toronto → Hamilton (One Way)"
     */
    @Override
    public String toString() {
        return String.format("%s → %s (%s)",
                origin.zoneName(),
                destination.zoneName(),
                tripType.getDisplayName());
    }
}

