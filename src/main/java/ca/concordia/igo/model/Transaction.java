package ca.concordia.igo.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a transaction in the system.
 */
public class Transaction {
    private final String transactionId;
    private final TransactionType type;
    private final double amount;
    private final PaymentMethod paymentMethod;
    private final LocalDateTime timestamp;
    private TransactionStatus status;
    private String errorMessage;

    public Transaction(TransactionType type, double amount, PaymentMethod paymentMethod) {
        this.transactionId = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

