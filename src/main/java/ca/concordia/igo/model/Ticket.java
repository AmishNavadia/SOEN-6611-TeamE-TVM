package ca.concordia.igo.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a transit ticket.
 * <p>
 * Immutable once created. Each ticket has a unique ID, captures the fare details
 * (origin, destination, trip type), and has a validity period.
 * </p>
 * Tickets are time-limited - use isValid() to check if a ticket can still be used.
 */
public class Ticket {
    private final String ticketId;
    private final Fare fare;
    private final double amount;
    private final LocalDateTime issuedAt;
    private final LocalDateTime validUntil;

    /**
     * Creates a new ticket with auto-generated ID.
     * <p>
     * The ticket is issued immediately (issuedAt = now) and remains valid
     * until the specified time. Validity period depends on trip type:
     * - Single/Return trips: typically valid for same day
     * - Day Pass: valid until end of day
     * </p>
     * @param fare the fare details (origin, destination, trip type)
     * @param amount the ticket price in dollars
     * @param validUntil when this ticket expires
     * @throws NullPointerException if fare is null
     */
    public Ticket(Fare fare, double amount, LocalDateTime validUntil) {
        this.ticketId = UUID.randomUUID().toString();
        this.fare = Objects.requireNonNull(fare);
        this.amount = amount;
        this.issuedAt = LocalDateTime.now();
        this.validUntil = validUntil;
    }

    /**
     * Returns the unique ticket ID.
     * Used for validation and tracking.
     *
     * @return ticket ID
     */
    public String getTicketId() {
        return ticketId;
    }

    /**
     * Returns the fare details for this ticket.
     *
     * @return fare including origin, destination, and trip type
     */
    public Fare getFare() {
        return fare;
    }

    /**
     * Returns the ticket price.
     *
     * @return amount paid in dollars
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns when the ticket was purchased.
     *
     * @return issue timestamp
     */
    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    /**
     * Returns when the ticket expires.
     *
     * @return expiry timestamp
     */
    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    /**
     * Checks if this ticket can still be used.
     * <p>
     * A ticket is valid if the current time is before the expiry time.
     * This does NOT check if the ticket has already been used.
     * </p>
     * @return true if the ticket hasn't expired yet, false otherwise
     */
    public boolean isValid() {
        return LocalDateTime.now().isBefore(validUntil);
    }
}

