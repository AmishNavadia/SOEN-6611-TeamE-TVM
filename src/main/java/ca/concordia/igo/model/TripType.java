package ca.concordia.igo.model;

public enum TripType {
    SINGLE("Single Trip"),
    RETURN("Return Trip"),
    DAY_PASS("Day Pass");

    private final String displayName;

    TripType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
