package ca.concordia.igo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void transactionInitialStateIsPending() {
        Transaction transaction = new Transaction(
                TransactionType.TICKET_PURCHASE,
                12.0,
                PaymentMethod.CREDIT_CARD
        );

        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertNotNull(transaction.getTransactionId());
        assertNotNull(transaction.getTimestamp());
        assertEquals(PaymentMethod.CREDIT_CARD, transaction.getPaymentMethod());
        assertEquals(12.0, transaction.getAmount());
    }

    @Test
    void settersUpdateStatusAndError() {
        Transaction transaction = new Transaction(
                TransactionType.CARD_RECHARGE,
                20.0,
                PaymentMethod.DEBIT_CARD
        );

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setErrorMessage("none");

        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
        assertEquals("none", transaction.getErrorMessage());
    }
}
