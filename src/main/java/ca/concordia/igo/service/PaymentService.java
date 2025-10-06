package ca.concordia.igo.service;

import ca.concordia.igo.exception.PaymentFailedException;
import ca.concordia.igo.model.PaymentMethod;
import ca.concordia.igo.model.Transaction;
import ca.concordia.igo.model.TransactionStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Service for processing payments through various payment methods.
 * <p>
 * Handles the payment lifecycle:
 * 1. Transaction created (PENDING)
 * 2. Payment authorized (AUTHORIZED)
 * 3. Payment completed or failed (COMPLETED/FAILED)
 * Currently simulates payment processing with a 95% success rate.
 * In production, this would integrate with actual payment gateways
 * (Moneris, Stripe, etc.).
 * </p>
 * Note: Transaction status is updated in-place, allowing callers
 * to track payment progress.
 */
public class PaymentService {
    private final Map<String, Transaction> activeTransactions = new HashMap<>();
    private final Random random = new Random();

    /**
     * Process a payment transaction.
     * <p>
     * Updates transaction status through its lifecycle:
     * PENDING → AUTHORIZED → COMPLETED (success)
     * PENDING → AUTHORIZED → FAILED (decline/error)
     * The transaction object is modified in place - check its status
     * after this method returns.
     * </p>
     *
     * @param transaction the transaction to process
     * @return true if payment succeeded
     * @throws PaymentFailedException if payment is declined or errors occur
     */
    public boolean processPayment(Transaction transaction) throws PaymentFailedException {
        activeTransactions.put(transaction.getTransactionId(), transaction);

        try {
            // Step 1: Authorize payment (reserve funds)
            transaction.setStatus(TransactionStatus.AUTHORIZED);

            // Step 2: Attempt to capture payment
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
     * Cancel a transaction and release reserved funds.
     * <p>
     * Only works for transactions in AUTHORIZED state.
     * Use this when a user cancels mid-payment or if validation
     * fails after authorization.
     * </p>
     *
     * Has no effect if transaction is already completed or failed.
     *
     * @param transactionId the transaction to cancel
     */
    public void cancelTransaction(String transactionId) {
        Transaction transaction = activeTransactions.get(transactionId);
        if (transaction != null && transaction.getStatus() == TransactionStatus.AUTHORIZED) {
            transaction.setStatus(TransactionStatus.CANCELLED);
        }
    }

    /**
     * Simulates payment processing with random delay and 95% success rate.
     * <p>
     * In production, this would be replaced with actual payment gateway API calls:
     * - Credit/Debit: Call Moneris or similar
     * - Contactless: Process tap payment
     * - Cash: Mark as immediate success
     * </p>
     *
     * The simulation includes:
     * - Random delay (500-1500ms) to mimic network latency
     * - 95% success rate to test error handling
     *
     * @param method the payment method being used (currently ignored)
     * @return true if payment succeeds, false if declined
     */
    private boolean simulatePayment(PaymentMethod method) {
        try {
            // Simulate network delay
            Thread.sleep(500 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 95% success rate
        return random.nextDouble() < 0.95;
    }
}
