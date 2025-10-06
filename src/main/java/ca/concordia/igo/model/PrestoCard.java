package ca.concordia.igo.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a PRESTO card - a reloadable transit card.
 * This is a mutable class - balance changes as the card is used.
 */
public class PrestoCard {
    private final String cardNumber;
    private final LocalDateTime expiryDate;
    private double balance;
    private boolean isActive;

    /**
     * Creates a new PRESTO card with zero balances.
     * @param cardNumber unique card identifier (cannot be null)
     * @throws NullPointerException if cardNumber is null
     */
    public PrestoCard(String cardNumber) {
        this(cardNumber, 0.0);
    }

    /**
     * Creates a new PRESTO card with an initial balance.
     * @param cardNumber unique card identifier (cannot be null)
     * @param initialBalance starting balance in dollars
     * @throws NullPointerException if cardNumber is null
     */
    public PrestoCard(String cardNumber, double initialBalance) {
        this.cardNumber = Objects.requireNonNull(cardNumber, "Card number cannot be null");
        this.balance = initialBalance;
        this.expiryDate = LocalDateTime.now().plusYears(5);
        this.isActive = true;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Returns the current card balance.
     *
     * @return balance in CAD dollars
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Returns when this card expires.
     * PRESTO cards are valid for 5 years from creation.
     * @return expiry date
     */
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    /**
     * Checks if the card is active.
     * Inactive cards cannot be used even if they have balance.
     * Cards are deactivated when reported lost/stolen.
     * @return true if card can be used, false if deactivated
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Adds money to the card balance.
     * Used for card reloads at kiosks, online, or retail locations.
     * @param amount dollars to add (must be positive)
     * @throws IllegalArgumentException if amount is zero or negative
     */
    public void addBalance(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.balance += amount;
    }

    /**
     * Deducts money from the card balance.
     * Called when a fare is paid using this card.
     * @param amount dollars to deduct
     * @throws IllegalStateException if balance is insufficient
     */
    public void deductBalance(double amount) {
        if (amount > balance) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.balance -= amount;
    }

    /**
     * Checks if the card has passed its expiry date.
     * @return true if the card is expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    /**
     * Deactivates this card.
     */
    public void deactivate() {
        this.isActive = false;
    }
}