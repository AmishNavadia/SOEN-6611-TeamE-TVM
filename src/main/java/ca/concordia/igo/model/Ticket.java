package ca.concordia.igo.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a transit ticket.
 */
public class Ticket {
    private final String ticketId;
    private final Fare fare;
    private final double amount;
    private final LocalDateTime issuedAt;
    private final LocalDateTime validUntil;

    public Ticket(Fare fare, double amount, LocalDateTime validUntil) {
        this.ticketId = UUID.randomUUID().toString();
        this.fare = Objects.requireNonNull(fare);
        this.amount = amount;
        this.issuedAt = LocalDateTime.now();
        this.validUntil = validUntil;
    }

    public String getTicketId() {
        return ticketId;
    }

    public Fare getFare() {
        return fare;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public boolean isValid() {
        return LocalDateTime.now().isBefore(validUntil);
    }
}

