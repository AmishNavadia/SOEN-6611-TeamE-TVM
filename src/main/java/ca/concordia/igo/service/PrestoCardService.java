package ca.concordia.igo.service;

import ca.concordia.igo.exception.InvalidCardException;
import ca.concordia.igo.model.PrestoCard;
import ca.concordia.igo.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing PRESTO cards.
 * <p>
 * Handles card operations:
 * - Reading and validating cards
 * - Recharging card balances
 * - Checking balance sufficiency for fares
 * </p>
 *
 * Cards are validated when read - checking for expiry, active status,
 * and existence in the system.
 * <p>
 * When the network is unavailable, recharge operations are queued for
 * later synchronization with the central PRESTO system.
 * </p>
 * Note: This service includes a pre-populated card database for demo.
 * Production would integrate with actual PRESTO backend APIs.
 */
public class PrestoCardService {
    private final Map<String, PrestoCard> cardDatabase = new HashMap<>();
    private final List<String> pendingRecharges = new ArrayList<>();  // ADD THIS
    private boolean networkAvailable = true;  // ADD THIS


    /**
     * Initialize service with demo cards.
     */
    public PrestoCardService() {
        cardDatabase.put("1234567890", new PrestoCard("1234567890", 25.00));
        cardDatabase.put("9876543210", new PrestoCard("9876543210", 5.50));
        cardDatabase.put("5555555555", new PrestoCard("5555555555", 0.00));
    }

    public boolean isNetworkAvailable() {
        return networkAvailable;
    }

    /**
     * Set network availability status.
     * <p>
     * Used by maintenance service to simulate network outages.
     * When the network is unavailable, recharges are queued.
     * </p>
     *
     * @param available true if the network is up, false if down
     */
    public void setNetworkAvailable(boolean available) {
        this.networkAvailable = available;
    }

    /**
     * Read and validate a PRESTO card.
     * <p>
     * Performs multiple validation checks:
     * 1. Card exists in system
     * 2. The Card is not expired
     * 3. Card is active (not reported lost/stolen)
     * </p>
     *
     * This should be called before any card operations to ensure
     * the card can be used.
     *
     * @param cardNumber the card number to read
     * @return the validated PrestoCard object
     * @throws InvalidCardException if card fails any validation check
     */
    public PrestoCard readCard(String cardNumber) throws InvalidCardException {
        PrestoCard card = cardDatabase.get(cardNumber);

        if (card == null) {
            throw new InvalidCardException(
                    InvalidCardException.ErrorCode.CARD_NOT_DETECTED,
                    "Card not found: " + cardNumber
            );
        }

        if (card.isExpired()) {
            throw new InvalidCardException(
                    InvalidCardException.ErrorCode.CARD_EXPIRED,
                    "Card expired: " + cardNumber
            );
        }

        if (!card.isActive()) {
            throw new InvalidCardException(
                    InvalidCardException.ErrorCode.CARD_INACTIVE,
                    "Card inactive: " + cardNumber
            );
        }

        return card;
    }

    /**
     * Recharge a PRESTO card with funds.
     * <p>
     * Validates the recharge amount and adds it to the card balance.
     * Offline handling: When network is unavailable, the recharge is
     * queued for later sync. The balance is still updated locally so
     * the card can be used immediately.
     * </p>
     * Limits:
     * - Minimum: $0.01
     * - Maximum: $1000.00 per transaction
     *
     * @param card the card to recharge
     * @param amount dollars to add to balance
     * @throws IllegalArgumentException if the amount is invalid
     * @throws InvalidCardException if the card is not valid
     */
    public void rechargeCard(PrestoCard card, double amount) throws InvalidCardException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Recharge amount must be positive");
        }

        if (amount > 1000) {
            throw new IllegalArgumentException("Maximum recharge amount is $1000");
        }

        card.addBalance(amount);

        // NEW: Check network and queue if offline
        if (!networkAvailable) {
            pendingRecharges.add(card.getCardNumber() + ":" + amount);
            Logger.info("Recharge queued for sync: " + card.getCardNumber());
        }

    }

    /**
     * Check if a card has enough balances for a fare.
     * <p>
     * Call this before deducting balance to avoid insufficient
     * balance errors. Provides better user experience by checking
     * balance before attempting payment.
     * </p>
     *
     * @param card the card to check
     * @param required the fare amount needed
     * @return true if card balance >= required amount
     */
    public boolean hasSufficientBalance(PrestoCard card, double required) {
        return card.getBalance() >= required;
    }
}

