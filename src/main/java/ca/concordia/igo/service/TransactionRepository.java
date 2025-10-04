package ca.concordia.igo.service;

import ca.concordia.igo.model.Transaction;
import ca.concordia.igo.util.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionRepository {
    private final List<Transaction> transactions = new ArrayList<>();

    public void log(Transaction t) {
        transactions.add(t);
        Logger.info("Transaction logged: " + t.getTransactionId());
    }

    public List<Transaction> getRecent(int limit) {
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
