package ca.concordia.igo.service;

import ca.concordia.igo.exception.PaymentFailedException;
import ca.concordia.igo.model.PaymentMethod;
import ca.concordia.igo.model.Transaction;
import ca.concordia.igo.model.TransactionStatus;
import ca.concordia.igo.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    @Test
    void processPaymentCompletesOnSuccess() throws Exception {
        PaymentService service = new PaymentService();
        setRandom(service, new StubRandom(0, 0.1));
        Transaction transaction = new Transaction(
                TransactionType.TICKET_PURCHASE,
                12.5,
                PaymentMethod.CREDIT_CARD
        );

        assertTrue(service.processPayment(transaction));
        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
        assertNotNull(service.getApprovalTimestamp(transaction.getTransactionId()));
    }

    @Test
    void processPaymentFailsWhenGatewayDeclines() throws Exception {
        PaymentService service = new PaymentService();
        setRandom(service, new StubRandom(0, 0.99));
        Transaction transaction = new Transaction(
                TransactionType.CARD_RECHARGE,
                40.0,
                PaymentMethod.DEBIT_CARD
        );

        PaymentFailedException ex = assertThrows(
                PaymentFailedException.class,
                () -> service.processPayment(transaction)
        );
        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
        assertTrue(ex.getMessage().contains("Payment declined"));
    }

    @Test
    void cancelTransactionConvertsAuthorizedToCancelled() throws Exception {
        PaymentService service = new PaymentService();
        Transaction transaction = new Transaction(
                TransactionType.MAINTENANCE,
                0.0,
                PaymentMethod.CASH
        );
        transaction.setStatus(TransactionStatus.AUTHORIZED);
        getActiveTransactions(service).put(transaction.getTransactionId(), transaction);

        service.cancelTransaction(transaction.getTransactionId());

        assertEquals(TransactionStatus.CANCELLED, transaction.getStatus());
    }

    private static void setRandom(PaymentService service, Random random) throws Exception {
        Field field = PaymentService.class.getDeclaredField("random");
        field.setAccessible(true);
        field.set(service, random);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Transaction> getActiveTransactions(PaymentService service) throws Exception {
        Field field = PaymentService.class.getDeclaredField("activeTransactions");
        field.setAccessible(true);
        return (Map<String, Transaction>) field.get(service);
    }

    private static class StubRandom extends Random {
        private final int nextIntValue;
        private final double nextDoubleValue;

        StubRandom(int nextIntValue, double nextDoubleValue) {
            this.nextIntValue = nextIntValue;
            this.nextDoubleValue = nextDoubleValue;
        }

        @Override
        public int nextInt(int bound) {
            return nextIntValue;
        }

        @Override
        public double nextDouble() {
            return nextDoubleValue;
        }
    }
}
