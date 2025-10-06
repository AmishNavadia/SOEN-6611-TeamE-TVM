package ca.concordia.igo.model;

import java.time.Duration;

/**
 * Types of trips available for purchase.
 * <p>
 * Each trip type affects pricing and validity rules:
 * - SINGLE: One-way trip from origin to destination
 * - RETURN: Round trip, typically discounted from buying two singles
 * - DAY_PASS: Unlimited travel for one day within specified zones
 * - MONTHLY_PASS: Unlimited travel for one month within specified zones
 * </p>
 *
 *
 */
public enum TripType {
    SINGLE("Single Trip", Duration.ofHours(2)),
    RETURN("Return Trip", Duration.ofHours(2)),
    DAY_PASS("Day Pass", Duration.ofDays(1)),
    MONTHLY_PASS("Monthly Pass", Duration.ofDays(30));

    private final String displayName;
    private final Duration validityDuration; // How long the trip is valid after activation

    TripType(String displayName, Duration validityDuration) {
        this.displayName = displayName;
        this.validityDuration = validityDuration;
    }

    /**
     * Returns the duration for which this trip type is valid after activation.
     *
     * @return validity duration (e.g., 2 hours for SINGLE)
     */
    public Duration getValidityDuration() {
        return validityDuration;
    }


    /**
     * Returns the user-friendly name for this trip type.
     * Used for display in UI and receipts.
     *
     * @return display name (e.g., "Return Trip")
     */
    public String getDisplayName() {
        return displayName;
    }
}
