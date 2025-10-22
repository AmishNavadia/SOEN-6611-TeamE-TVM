package ca.concordia.igo.service;

import ca.concordia.igo.exception.InvalidCardException;
import ca.concordia.igo.model.PrestoCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PrestoCardServiceTest {

    private PrestoCardService service;

    @BeforeEach
    void setUp() {
        service = new PrestoCardService();
    }

    @Test
    void readCardReturnsExistingCard() throws InvalidCardException {
        PrestoCard card = service.readCard("1234567890");

        assertEquals("1234567890", card.getCardNumber());
    }

    @Test
    void readCardThrowsWhenCardMissing() {
        InvalidCardException ex = assertThrows(
                InvalidCardException.class,
                () -> service.readCard("0000000000")
        );
        assertTrue(ex.getMessage().contains("Card not found"));
    }

    @Test
    void readCardThrowsWhenCardInactive() throws InvalidCardException {
        PrestoCard card = service.readCard("9876543210");
        card.deactivate();

        assertThrows(InvalidCardException.class, () -> service.readCard("9876543210"));
    }

    @Test
    void readCardThrowsWhenCardExpired() throws Exception {
        Map<String, PrestoCard> database = getCardDatabase(service);
        PrestoCard expiredCard = new PrestoCard("expired", 0.0) {
            @Override
            public boolean isExpired() {
                return true;
            }
        };
        database.put("expired", expiredCard);

        InvalidCardException ex = assertThrows(
                InvalidCardException.class,
                () -> service.readCard("expired")
        );
        assertTrue(ex.getMessage().contains("Card expired"));
    }

    @Test
    void rechargeCardUpdatesBalance() throws InvalidCardException {
        PrestoCard card = service.readCard("1234567890");
        double previous = card.getBalance();

        service.rechargeCard(card, 20.0);

        assertEquals(previous + 20.0, card.getBalance());
    }

    @Test
    void rechargeCardQueuesWhenOffline() throws Exception {
        PrestoCard card = service.readCard("5555555555");
        service.setNetworkAvailable(false);

        service.rechargeCard(card, 5.0);

        List<String> pending = getPendingRecharges(service);
        assertFalse(pending.isEmpty());
        assertTrue(pending.get(0).contains("5555555555"));
    }

    @Test
    void rechargeCardValidatesAmounts() throws InvalidCardException {
        PrestoCard card = service.readCard("1234567890");

        assertThrows(IllegalArgumentException.class, () -> service.rechargeCard(card, 0));
        assertThrows(IllegalArgumentException.class, () -> service.rechargeCard(card, -5));
        assertThrows(IllegalArgumentException.class, () -> service.rechargeCard(card, 1500));
    }

    @Test
    void hasSufficientBalanceChecksAmount() throws InvalidCardException {
        PrestoCard card = service.readCard("1234567890");

        assertTrue(service.hasSufficientBalance(card, 5));
        assertFalse(service.hasSufficientBalance(card, 100));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, PrestoCard> getCardDatabase(PrestoCardService service) throws Exception {
        Field field = PrestoCardService.class.getDeclaredField("cardDatabase");
        field.setAccessible(true);
        return (Map<String, PrestoCard>) field.get(service);
    }

    @SuppressWarnings("unchecked")
    private static List<String> getPendingRecharges(PrestoCardService service) throws Exception {
        Field field = PrestoCardService.class.getDeclaredField("pendingRecharges");
        field.setAccessible(true);
        return (List<String>) field.get(service);
    }
}
