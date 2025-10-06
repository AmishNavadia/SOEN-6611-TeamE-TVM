package ca.concordia.igo.service;

import ca.concordia.igo.model.Transaction;
import ca.concordia.igo.util.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for storing and retrieving transaction records.
 * <p>
 * Provides persistence and querying for all transactions in the system.
 * Used for:
 * - Transaction history and audit trails
 * - Reporting and analytics
 * - Troubleshooting payment issues
 * - Compliance and record-keeping
 * </p>
 *
 * Note: This is an in-memory implementation for demo purposes.
 * Production would use a database (PostgresSQL, MongoDB, etc.)
 * with proper indexing and backup strategies.
 */
public class TransactionRepository {
    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * Log a transaction to the repository.
     * <p>
     * Call this after creating any transaction to ensure it's
     * saved for history and reporting.
     * </p>
     * Logs are written regardless of transaction status - we store
     * failed transactions for troubleshooting and fraud detection.
     *
     * @param t the transaction to log
     */
    public void log(Transaction t) {
        transactions.add(t);
        Logger.info("Transaction logged: " + t.getTransactionId());
    }

    /**
     * Get the most recent transactions.
     * <p>
     * Returns transactions sorted by timestamp, newest first.
     * Useful for displaying transaction history in admin/maintenance screens.
     * </p>
     * @param limit maximum number of transactions to return
     * @return list of recent transactions, the newest first (empty list if none)
     */
    public List<Transaction> getRecent(int limit) {
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
