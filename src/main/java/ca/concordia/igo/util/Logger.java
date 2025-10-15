package ca.concordia.igo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Enhanced logging utility for iGo system with file output support.
 * Logs are written to both console and log file for analysis.
 */
public class Logger {
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String LOG_FILE = "igo-application.log";
    private static PrintWriter fileWriter;

    static {
        try {
            fileWriter = new PrintWriter(new FileWriter(LOG_FILE, true), true);
        } catch (IOException e) {
            System.err.println("Failed to initialize log file: " + e.getMessage());
        }
    }

    public static void info(String message) {
        log("INFO", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    public static void error(String message, Exception e) {
        log("ERROR", message + ": " + e.getMessage());
        if (fileWriter != null) {
            e.printStackTrace(fileWriter);
        }
        e.printStackTrace();
    }

    public static void debug(String message) {
        log("DEBUG", message);
    }

    public static void warn(String message) {
        log("WARN", message);
    }

    public static void transaction(String transactionId, String message) {
        log("TRANSACTION", "[" + transactionId + "] " + message);
    }

    public static void performance(String operation, long durationMs) {
        log("PERFORMANCE", operation + " completed in " + durationMs + "ms");
    }

    public static void userAction(String action, String details) {
        log("USER_ACTION", action + " - " + details);
    }

    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String logMessage = String.format("[%s] %s - %s", timestamp, level, message);
        System.out.println(logMessage);
        if (fileWriter != null) {
            fileWriter.println(logMessage);
        }
    }

    public static void close() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}
