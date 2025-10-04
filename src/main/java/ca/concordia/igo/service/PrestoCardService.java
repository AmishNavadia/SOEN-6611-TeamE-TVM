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
 */
public class PrestoCardService {
    private final Map<String, PrestoCard> cardDatabase = new HashMap<>();
    private final List<String> pendingRecharges = new ArrayList<>();  // ADD THIS
    private boolean networkAvailable = true;  // ADD THIS


    public PrestoCardService() {
        cardDatabase.put("1234567890", new PrestoCard("1234567890", 25.00));
        cardDatabase.put("9876543210", new PrestoCard("9876543210", 5.50));
        cardDatabase.put("5555555555", new PrestoCard("5555555555", 0.00));
    }

    public boolean isNetworkAvailable() {
        return networkAvailable;
    }

    public void setNetworkAvailable(boolean available) {
        this.networkAvailable = available;
    }

    /**
     * Read a PRESTO card by card number.
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
     * Recharge a PRESTO card.
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
     * Check if card has sufficient balance.
     */
    public boolean hasSufficientBalance(PrestoCard card, double required) {
        return card.getBalance() >= required;
    }
}

