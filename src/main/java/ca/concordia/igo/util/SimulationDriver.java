package ca.concordia.igo.util;

import ca.concordia.igo.exception.IGoException;
import ca.concordia.igo.model.*;
import ca.concordia.igo.service.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulation driver for testing system throughput and performance.
 * Runs automated transactions to measure system efficiency.
 *
 * Requirements:
 * - Throughput ≥ 100 transactions/hour
 * - Measure end-to-end transaction time
 * - Log all operations
 * - Generate a performance report
 */
public class SimulationDriver {

    // Services
    private final FareCalculator fareCalculator;
    private final PaymentService paymentService;
    private final PrestoCardService prestoService;
    private final TicketPrinter ticketPrinter;
    private final TransactionRepository transactionRepository;

    // Statistics
    private final List<TransactionResult> results = new ArrayList<>();
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);

    // Configuration
    private static final int TOTAL_TRANSACTIONS = 100;
    private static final Random random = new Random();

    public SimulationDriver() {
        this.fareCalculator = new FareCalculator();
        this.paymentService = new PaymentService();
        this.prestoService = new PrestoCardService();
        this.ticketPrinter = new TicketPrinter();
        this.transactionRepository = new TransactionRepository();

        Logger.info("═══════════════════════════════════════════════════════");
        Logger.info("SIMULATION DRIVER INITIALIZED");
        Logger.info("Target: " + TOTAL_TRANSACTIONS + " transactions");
        Logger.info("═══════════════════════════════════════════════════════");
    }

    /**
     * Main simulation execution method
     */
    public void runSimulation() {
        Logger.info("\n╔════════════════════════════════════════════════════╗");
        Logger.info("║     STARTING 100-TRANSACTION SIMULATION TEST      ║");
        Logger.info("╚════════════════════════════════════════════════════╝\n");

        LocalDateTime startTime = LocalDateTime.now();
        long startMillis = System.currentTimeMillis();

        // Run 100 transactions
        for (int i = 1; i <= TOTAL_TRANSACTIONS; i++) {
            runSingleTransaction(i);

            // Progress indicator every 10 transactions
            if (i % 10 == 0) {
                Logger.info(String.format("Progress: %d/%d transactions completed (%.1f%%)",
                        i, TOTAL_TRANSACTIONS, (i * 100.0 / TOTAL_TRANSACTIONS)));
            }
        }

        LocalDateTime endTime = LocalDateTime.now();
        long endMillis = System.currentTimeMillis();

        // Generate a report
        generatePerformanceReport(startTime, endTime, startMillis, endMillis);
    }

    /**
     * Executes a single randomized transaction
     */
    private void runSingleTransaction(int transactionNumber) {
        Logger.info("\n─────────────────────────────────────────────────────");
        Logger.info("Transaction #" + transactionNumber + " - START");

        long txStartTime = System.currentTimeMillis();
        TransactionType type = selectRandomTransactionType();

        try {
            switch (type) {
                case TICKET_PURCHASE:
                    simulateTicketPurchase(transactionNumber);
                    break;
                case CARD_RECHARGE:
                    simulateCardRecharge(transactionNumber);
                    break;
                case BALANCE_CHECK:
                    simulateBalanceCheck(transactionNumber);
                    break;
                default:
                    Logger.warn("Unknown transaction type: " + type);
            }

            long txEndTime = System.currentTimeMillis();
            long duration = txEndTime - txStartTime;

            successCount.incrementAndGet();
            results.add(new TransactionResult(transactionNumber, type, true, duration));

            Logger.info("Transaction #" + transactionNumber + " - SUCCESS ✓ (Duration: " + duration + "ms)");

        } catch (Exception e) {
            long txEndTime = System.currentTimeMillis();
            long duration = txEndTime - txStartTime;

            failureCount.incrementAndGet();
            results.add(new TransactionResult(transactionNumber, type, false, duration));

            Logger.error("Transaction #" + transactionNumber + " - FAILED ✗ (Duration: " + duration + "ms)", e);
        }
    }

    /**
     * Simulates a ticket purchase transaction
     */
    private void simulateTicketPurchase(int txNumber) throws Exception {
        Logger.info("  Type: TICKET_PURCHASE");

        // Random fare selection
        Zone origin = selectRandomZone();
        Zone destination = selectRandomZone();
        TripType tripType = selectRandomTripType();
        PaymentMethod paymentMethod = selectRandomPaymentMethod();

        Logger.info("  Route: " + origin + " → " + destination);
        Logger.info("  Trip Type: " + tripType.getDisplayName());
        Logger.info("  Payment: " + paymentMethod.getDisplayName());

        // Calculate fare
        Fare fare = new Fare(origin, destination, tripType);
        double amount = fareCalculator.calculateAmount(fare);
        Logger.info("  Amount: $" + String.format("%.2f", amount));

        // Create transaction
        Transaction transaction = new Transaction(TransactionType.TICKET_PURCHASE, amount, paymentMethod);

        // Process payment
        boolean success = paymentService.processPayment(transaction);
        if (!success) {
            throw new IGoException("Payment processing failed");
        }

        // Generate ticket
        LocalDateTime validUntil = LocalDateTime.now().plus(tripType.getValidityDuration());
        Ticket ticket = new Ticket(fare, amount, validUntil);

        // Print ticket
        Long approvalTime = paymentService.getApprovalTimestamp(transaction.getTransactionId());
        String receipt = ticketPrinter.printTicket(ticket, Language.EN, approvalTime);

        // Log transaction
        transactionRepository.log(transaction);

        Logger.info("  Ticket ID: " + ticket.getTicketId().substring(0, 8));
    }

    /**
     * Simulates a card recharge transaction
     */
    private void simulateCardRecharge(int txNumber) throws Exception {
        Logger.info("  Type: CARD_RECHARGE");

        // Get or create test card
        String cardNumber = "TEST" + String.format("%06d", txNumber);
        PrestoCard card;
        try {
            card = prestoService.readCard(cardNumber);
        } catch (Exception e) {
            // Create new card if not found
            card = new PrestoCard(cardNumber, random.nextDouble() * 50);
        }

        Logger.info("  Card: " + cardNumber);
        Logger.info("  Old Balance: $" + String.format("%.2f", card.getBalance()));

        // Random recharge amount
        double amount = selectRandomRechargeAmount();
        PaymentMethod paymentMethod = selectRandomPaymentMethod();

        Logger.info("  Recharge Amount: $" + String.format("%.2f", amount));
        Logger.info("  Payment: " + paymentMethod.getDisplayName());

        // Create transaction
        Transaction transaction = new Transaction(TransactionType.CARD_RECHARGE, amount, paymentMethod);

        // Process payment
        boolean success = paymentService.processPayment(transaction);
        if (!success) {
            throw new IGoException("Payment processing failed");
        }

        // Recharge card
        prestoService.rechargeCard(card, amount);

        // Log transaction
        transactionRepository.log(transaction);

        Logger.info("  New Balance: $" + String.format("%.2f", card.getBalance()));
    }

    /**
     * Simulates a balance check transaction
     */
    private void simulateBalanceCheck(int txNumber) throws Exception {
        Logger.info("  Type: BALANCE_CHECK");

        // Get or create test card
        String cardNumber = "TEST" + String.format("%06d", random.nextInt(100));
        PrestoCard card;
        try {
            card = prestoService.readCard(cardNumber);
        } catch (Exception e) {
            // Create new card if not found
            card = new PrestoCard(cardNumber, random.nextDouble() * 100);
        }

        Logger.info("  Card: " + cardNumber);
        Logger.info("  Balance: $" + String.format("%.2f", card.getBalance()));

        // Create transaction (balance check is free)
        Transaction transaction = new Transaction(TransactionType.BALANCE_CHECK, 0.0, PaymentMethod.CONTACTLESS);
        transaction.setStatus(TransactionStatus.COMPLETED);

        // Log transaction
        transactionRepository.log(transaction);
    }

    /**
     * Generates a comprehensive performance report
     */
    private void generatePerformanceReport(LocalDateTime startTime, LocalDateTime endTime,
                                           long startMillis, long endMillis) {
        long totalDurationMs = endMillis - startMillis;
        double totalDurationSeconds = totalDurationMs / 1000.0;
        double totalDurationMinutes = totalDurationSeconds / 60.0;
        double totalDurationHours = totalDurationMinutes / 60.0;

        // Calculate throughput
        double throughputPerHour = TOTAL_TRANSACTIONS / totalDurationHours;
        double throughputPerMinute = TOTAL_TRANSACTIONS / totalDurationMinutes;
        double throughputPerSecond = TOTAL_TRANSACTIONS / totalDurationSeconds;

        // Calculate statistics
        OptionalDouble avgDuration = results.stream()
                .mapToLong(TransactionResult::getDurationMs)
                .average();

        long minDuration = results.stream()
                .mapToLong(TransactionResult::getDurationMs)
                .min()
                .orElse(0);

        long maxDuration = results.stream()
                .mapToLong(TransactionResult::getDurationMs)
                .max()
                .orElse(0);

        // Transaction type breakdown
        Map<TransactionType, Long> typeBreakdown = new HashMap<>();
        for (TransactionResult result : results) {
            typeBreakdown.merge(result.getType(), 1L, Long::sum);
        }

        // Print report
        Logger.info("\n");
        Logger.info("╔════════════════════════════════════════════════════════════════╗");
        Logger.info("║              SIMULATION PERFORMANCE REPORT                     ║");
        Logger.info("╚════════════════════════════════════════════════════════════════╝");
        Logger.info("");
        Logger.info("EXECUTION SUMMARY");
        Logger.info("─────────────────────────────────────────────────────────────────");
        Logger.info("  Start Time:           " + startTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Logger.info("  End Time:             " + endTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Logger.info("  Total Duration:       " + String.format("%.2f", totalDurationSeconds) + " seconds (" +
                String.format("%.2f", totalDurationMinutes) + " minutes)");
        Logger.info("");
        Logger.info("TRANSACTION STATISTICS");
        Logger.info("─────────────────────────────────────────────────────────────────");
        Logger.info("  Total Transactions:   " + TOTAL_TRANSACTIONS);
        Logger.info("  Successful:           " + successCount.get() + " ✓");
        Logger.info("  Failed:               " + failureCount.get() + " ✗");
        Logger.info("  Success Rate:         " + String.format("%.1f", (successCount.get() * 100.0 / TOTAL_TRANSACTIONS)) + "%");
        Logger.info("");
        Logger.info("THROUGHPUT METRICS");
        Logger.info("─────────────────────────────────────────────────────────────────");
        Logger.info("  Throughput (per hour):    " + String.format("%.2f", throughputPerHour) + " tx/hr");
        Logger.info("  Throughput (per minute):  " + String.format("%.2f", throughputPerMinute) + " tx/min");
        Logger.info("  Throughput (per second):  " + String.format("%.2f", throughputPerSecond) + " tx/sec");
        Logger.info("");

        // Requirement check
        boolean meetsRequirement = throughputPerHour >= 100;
        Logger.info("REQUIREMENT CHECK");
        Logger.info("─────────────────────────────────────────────────────────────────");
        Logger.info("  Required Throughput:  ≥ 100 tx/hr");
        Logger.info("  Actual Throughput:    " + String.format("%.2f", throughputPerHour) + " tx/hr");
        Logger.info("  Status:               " + (meetsRequirement ? "✓ PASSED" : "✗ FAILED"));
        Logger.info("");

        Logger.info("RESPONSE TIME STATISTICS");
        Logger.info("─────────────────────────────────────────────────────────────────");
        Logger.info("  Average Duration:     " + String.format("%.2f", avgDuration.orElse(0)) + " ms");
        Logger.info("  Minimum Duration:     " + minDuration + " ms");
        Logger.info("  Maximum Duration:     " + maxDuration + " ms");
        Logger.info("");

        Logger.info("TRANSACTION TYPE BREAKDOWN");
        Logger.info("─────────────────────────────────────────────────────────────────");
        for (Map.Entry<TransactionType, Long> entry : typeBreakdown.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / TOTAL_TRANSACTIONS;
            Logger.info(String.format("  %-20s %3d (%.1f%%)",
                    entry.getKey(), entry.getValue(), percentage));
        }
        Logger.info("");
        Logger.info("╚════════════════════════════════════════════════════════════════╝");
        Logger.info("");

        // Save detailed results to file (optional)
        saveDetailedResults();
    }

    /**
     * Saves detailed transaction results to a file
     */
    private void saveDetailedResults() {
        try {
            String filename = "simulation_results_" +
                    LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                    ".csv";

            java.io.PrintWriter writer = new java.io.PrintWriter(filename);
            writer.println("Transaction#,Type,Success,Duration(ms)");

            for (TransactionResult result : results) {
                writer.println(String.format("%d,%s,%b,%d",
                        result.getTransactionNumber(),
                        result.getType(),
                        result.isSuccess(),
                        result.getDurationMs()));
            }

            writer.close();
            Logger.info("Detailed results saved to: " + filename);

        } catch (Exception e) {
            Logger.error("Failed to save detailed results", e);
        }
    }

    // Helper methods for random selection

    private TransactionType selectRandomTransactionType() {
        // Weighted distribution: 60% purchases, 30% recharges, 10% balance checks
        double rand = random.nextDouble();
        if (rand < 0.6) return TransactionType.TICKET_PURCHASE;
        if (rand < 0.9) return TransactionType.CARD_RECHARGE;
        return TransactionType.BALANCE_CHECK;
    }

    private Zone selectRandomZone() {
        Zone[] zones = {Zone.ZONE_1, Zone.ZONE_2, Zone.ZONE_3, Zone.ZONE_4};
        return zones[random.nextInt(zones.length)];
    }

    private TripType selectRandomTripType() {
        TripType[] types = TripType.values();
        return types[random.nextInt(types.length)];
    }

    private PaymentMethod selectRandomPaymentMethod() {
        PaymentMethod[] methods = PaymentMethod.values();
        return methods[random.nextInt(methods.length)];
    }

    private double selectRandomRechargeAmount() {
        double[] amounts = {10.0, 20.0, 50.0, 100.0};
        return amounts[random.nextInt(amounts.length)];
    }

    /**
     * Inner class to store transaction results
     */
    private static class TransactionResult {
        private final int transactionNumber;
        private final TransactionType type;
        private final boolean success;
        private final long durationMs;

        public TransactionResult(int transactionNumber, TransactionType type,
                                 boolean success, long durationMs) {
            this.transactionNumber = transactionNumber;
            this.type = type;
            this.success = success;
            this.durationMs = durationMs;
        }

        public int getTransactionNumber() { return transactionNumber; }
        public TransactionType getType() { return type; }
        public boolean isSuccess() { return success; }
        public long getDurationMs() { return durationMs; }
    }

    /**
     * Main method to run the simulation
     */
    public static void main(String[] args) {
        System.out.println("\n" +
                "╔══════════════════════════════════════════════════════════════╗\n" +
                "║         iGo SIMULATION DRIVER - THROUGHPUT TEST              ║\n" +
                "║                  100 Transaction Loop                        ║\n" +
                "╚══════════════════════════════════════════════════════════════╝\n");

        SimulationDriver driver = new SimulationDriver();
        driver.runSimulation();

        System.out.println("\nSimulation completed successfully!");
        System.out.println("Check the logs for detailed performance metrics\n");
    }
}
