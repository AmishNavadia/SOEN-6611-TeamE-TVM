package ca.concordia.igo.service;

import ca.concordia.igo.exception.PaymentFailedException;
import ca.concordia.igo.model.PaymentMethod;
import ca.concordia.igo.model.Transaction;
import ca.concordia.igo.model.TransactionStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Service for processing payments.
 */
public class PaymentService {
    private final Map<String, Transaction> activeTransactions = new HashMap<>();
    private final Random random = new Random();

    /**
     * Process a payment transaction.
     */
    public boolean processPayment(Transaction transaction) throws PaymentFailedException {
        activeTransactions.put(transaction.getTransactionId(), transaction);

        try {
            transaction.setStatus(TransactionStatus.AUTHORIZED);

            boolean success = simulatePayment(transaction.getPaymentMethod());

            if (success) {
                transaction.setStatus(TransactionStatus.COMPLETED);
                return true;
            } else {
                transaction.setStatus(TransactionStatus.FAILED);
                throw new PaymentFailedException(
                        transaction.getTransactionId(),
                        "Payment declined by provider"
                );
            }
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setErrorMessage(e.getMessage());
            throw new PaymentFailedException(
                    transaction.getTransactionId(),
                    e.getMessage()
            );
        }
    }

    /**
     * Cancel a transaction and rollback.
     */
    public void cancelTransaction(String transactionId) {
        Transaction transaction = activeTransactions.get(transactionId);
        if (transaction != null && transaction.getStatus() == TransactionStatus.AUTHORIZED) {
            transaction.setStatus(TransactionStatus.CANCELLED);
        }
    }

    /**
     * Simulate payment processing (95% success rate).
     */
    private boolean simulatePayment(PaymentMethod method) {
        try {
            Thread.sleep(500 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return random.nextDouble() < 0.95;
    }
}
