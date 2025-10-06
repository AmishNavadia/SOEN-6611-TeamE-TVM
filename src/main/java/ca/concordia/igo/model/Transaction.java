package ca.concordia.igo.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a transaction in the system.
 * Transactions are mutable - their status can change as they're processed.
 * This allows tracking of transaction lifecycle from creation to completion.
 */
public class Transaction {
    private final String transactionId;
    private final TransactionType type;
    private final double amount;
    private final PaymentMethod paymentMethod;
    private final LocalDateTime timestamp;
    private TransactionStatus status;
    private String errorMessage;

    /**
     * Creates a new transaction with PENDING status.
     *
     * @param type the type of transaction being performed
     * @param amount the transaction amount in dollars
     * @param paymentMethod how the customer is paying
     */
    public Transaction(TransactionType type, double amount, PaymentMethod paymentMethod) {
        this.transactionId = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }

    /**
     * Returns the unique transaction ID.
     * Auto-generated UUID, used for tracking and auditing.
     *
     * @return transaction ID
     */
    public String getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    /**
     * Returns the transaction amount in dollars.
     *
     * @return amount (can be 0 for balance checks)
     */
    public double getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Returns when the transaction was created.
     *
     * @return creation timestamp (not the completion time)
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    /**
     * Updates the transaction status.
     *
     * @param status the new status
     */
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    /**
     * Returns error details if the transaction failed.
     *
     * @return error message, or null if no error occurred
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets an error message for failed transactions.
     * @param errorMessage description of what went wrong
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

