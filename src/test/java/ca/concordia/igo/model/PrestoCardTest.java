package ca.concordia.igo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrestoCardTest {

    @Test
    void constructorInitializesDefaults() {
        PrestoCard card = new PrestoCard("1234567890");

        assertEquals("1234567890", card.getCardNumber());
        assertEquals(0.0, card.getBalance());
        assertTrue(card.isActive());
        assertFalse(card.isExpired());
    }

    @Test
    void addBalanceUpdatesBalance() {
        PrestoCard card = new PrestoCard("123", 10.0);
        card.addBalance(5.0);

        assertEquals(15.0, card.getBalance());
    }

    @Test
    void addBalanceRejectsNonPositiveAmount() {
        PrestoCard card = new PrestoCard("123");

        assertThrows(IllegalArgumentException.class, () -> card.addBalance(0));
        assertThrows(IllegalArgumentException.class, () -> card.addBalance(-2.5));
    }

    @Test
    void deductBalanceReducesBalance() {
        PrestoCard card = new PrestoCard("123", 20.0);

        card.deductBalance(12.5);

        assertEquals(7.5, card.getBalance());
    }

    @Test
    void deductBalanceThrowsWhenInsufficient() {
        PrestoCard card = new PrestoCard("123", 5.0);

        assertThrows(IllegalStateException.class, () -> card.deductBalance(6.0));
    }

    @Test
    void deactivateMarksCardInactive() {
        PrestoCard card = new PrestoCard("123");
        card.deactivate();

        assertFalse(card.isActive());
    }

    @Test
    void maskedCardNumberHidesDigits() {
        PrestoCard card = new PrestoCard("1111222233334444");

        assertEquals("************4444", card.getMaskedCardNumber());
        assertEquals("**** **** **** 4444", card.getMaskedCardNumberFormatted());
    }
}
