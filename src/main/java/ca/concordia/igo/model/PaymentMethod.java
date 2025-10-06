package ca.concordia.igo.model;

/**
 * Payment methods accepted by the system.
 * <p>
 * Used for transaction processing and validation.
 * Different payment methods may have different processing fees,
 * limits, and validation requirements.
 * </p>
 */
public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    CONTACTLESS("Contactless"), // Tap to pay (Apple Pay, Google Pay, etc.)
    CASH("Cash");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-friendly name for this payment method.
     * Used for display in UI and receipts.
     *
     * @return display name (e.g., "Credit Card")
     */
    public String getDisplayName() {
        return displayName;
    }
}
