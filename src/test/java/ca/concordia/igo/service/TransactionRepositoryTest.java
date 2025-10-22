package ca.concordia.igo.service;

import ca.concordia.igo.model.PaymentMethod;
import ca.concordia.igo.model.Transaction;
import ca.concordia.igo.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionRepositoryTest {

    @Test
    void getRecentReturnsTransactionsInDescendingOrder() throws InterruptedException {
        TransactionRepository repository = new TransactionRepository();
        Transaction first = new Transaction(TransactionType.BALANCE_CHECK, 0.0, PaymentMethod.CASH);
        Thread.sleep(5);
        Transaction second = new Transaction(TransactionType.CARD_RECHARGE, 20.0, PaymentMethod.DEBIT_CARD);
        Thread.sleep(5);
        Transaction third = new Transaction(TransactionType.TICKET_PURCHASE, 7.5, PaymentMethod.CREDIT_CARD);

        repository.log(first);
        repository.log(second);
        repository.log(third);

        List<Transaction> recent = repository.getRecent(2);

        assertEquals(2, recent.size());
        assertEquals(third, recent.get(0));
        assertEquals(second, recent.get(1));
    }
}
