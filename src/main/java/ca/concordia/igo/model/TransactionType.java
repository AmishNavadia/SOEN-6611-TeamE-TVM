package ca.concordia.igo.model;

/**
 * Types of transactions that can occur in the system.
 * <p>
 * Used for transaction logging, reporting, and processing logic.
 * Different transaction types may have different validation rules
 * and processing workflows.
 * </p>
 */
public enum TransactionType {
    /** Purchase of a new ticket */
    TICKET_PURCHASE,

    /** Adding money to a PRESTO card */
    CARD_RECHARGE,

    /** Checking card balance (no money movement) */
    BALANCE_CHECK,

    /** System maintenance operations (refunds, adjustments, etc.) */
    MAINTENANCE
}
