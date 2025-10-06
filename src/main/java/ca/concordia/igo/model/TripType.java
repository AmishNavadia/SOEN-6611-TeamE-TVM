package ca.concordia.igo.model;

/**
 * Types of trips available for purchase.
 * <p>
 * Each trip type affects pricing and validity rules:
 * - SINGLE: One-way trip from origin to destination
 * - RETURN: Round trip, typically discounted from buying two singles
 * - DAY_PASS: Unlimited travel for one day within specified zones
 * <
 */
public enum TripType {
    SINGLE("Single Trip"),
    RETURN("Return Trip"),
    DAY_PASS("Day Pass"),
    MONTHLY_PASS("Monthly Pass");

    private final String displayName;

    TripType(String displayName) {
        this.displayName = displayName;
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
