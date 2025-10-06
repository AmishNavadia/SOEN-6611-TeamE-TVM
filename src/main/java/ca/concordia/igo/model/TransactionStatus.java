package ca.concordia.igo.model;

/**
 * Lifecycle states of a transaction.
 * <p>
 * Transactions progress through these states:
 * 1. PENDING - Transaction created, awaiting payment processing
 * 2. AUTHORIZED - Payment authorized, funds reserved
 * 3. COMPLETED - Transaction successful, funds captured
 * 4. FAILED - Transaction failed (payment declined, system error, etc.)
 * 5. CANCELLED - Transaction cancelled by user or system
 * </p>
 * Note: Not all transactions go through all states. For example,
 * a balance check might go directly from PENDING to COMPLETED.
 */
public enum TransactionStatus {
    PENDING,
    AUTHORIZED,
    COMPLETED,
    FAILED,
    CANCELLED
}
