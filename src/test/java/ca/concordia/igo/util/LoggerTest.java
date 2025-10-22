package ca.concordia.igo.util;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerTest {

    @Test
    void infoAndWarnIncludeLevelInOutput() {
        String infoOutput = capture(Logger::info, "Info test");
        String warnOutput = capture(Logger::warn, "Warn test");

        assertTrue(infoOutput.contains("INFO - Info test"));
        assertTrue(warnOutput.contains("WARN - Warn test"));
    }

    @Test
    void performanceAndTransactionIncludeDetails() {
        String perfOutput = capture(message -> Logger.performance("Unit Test", 42L), null);
        String txnOutput = capture(message -> Logger.transaction("txn-1", "Completed"), null);

        assertTrue(perfOutput.contains("PERFORMANCE - Unit Test completed in 42ms"));
        assertTrue(txnOutput.contains("TRANSACTION - [txn-1] Completed"));
    }

    private String capture(LoggerAction action, String message) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream interceptor = new PrintStream(buffer);
        System.setOut(interceptor);
        try {
            action.log(message);
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString();
    }

    @FunctionalInterface
    private interface LoggerAction {
        void log(String message);
    }
}
