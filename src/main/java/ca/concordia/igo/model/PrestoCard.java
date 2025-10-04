package ca.concordia.igo.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a PRESTO card.
 */
public class PrestoCard {
    private final String cardNumber;
    private final LocalDateTime expiryDate;
    private double balance;
    private boolean isActive;

    public PrestoCard(String cardNumber) {
        this(cardNumber, 0.0);
    }

    public PrestoCard(String cardNumber, double initialBalance) {
        this.cardNumber = Objects.requireNonNull(cardNumber, "Card number cannot be null");
        this.balance = initialBalance;
        this.expiryDate = LocalDateTime.now().plusYears(5);
        this.isActive = true;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public double getBalance() {
        return balance;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void addBalance(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.balance += amount;
    }

    public void deductBalance(double amount) {
        if (amount > balance) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.balance -= amount;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public void deactivate() {
        this.isActive = false;
    }
}