package ca.concordia.igo.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for system maintenance and diagnostics.
 * <p>
 * Provides admin authentication and system health monitoring.
 * Used by maintenance staff to check kiosk hardware status and
 * simulate hardware failures for testing.
 * </p>
 * This service tracks the status of:
 * - Network connectivity
 * - Receipt printer
 * - NFC card reader
 */
public class MaintenanceService {
    private static final String ADMIN_PIN = "9999";
    private boolean systemOnline = true;
    private boolean printerAvailable = true;
    private final boolean nfcReaderOk = true;

    /**
     * Authenticates maintenance staff using a PIN.
     * <p>
     * WARNING: This is a simple PIN check for demo purposes.
     * Production systems should use proper authentication with
     * encrypted credentials and audit logging.
     * </p>
     * @param pin the PIN to check
     * @return true if PIN matches, false otherwise
     */
    public boolean authenticate(String pin) {
        return ADMIN_PIN.equals(pin);
    }

    /**
     * Returns current status of all system components.
     * <p>
     * Used by the maintenance UI to display system health.
     * Each component shows either OK/ERROR or ONLINE/OFFLINE status.
     * </p>
     * @return map of component names to status strings
     */
    public Map<String, String> getDiagnostics() {
        Map<String, String> status = new HashMap<>();
        status.put("System", systemOnline ? "ONLINE" : "OFFLINE");
        status.put("Printer", printerAvailable ? "OK" : "ERROR");
        status.put("NFC Reader", nfcReaderOk ? "OK" : "ERROR");
        status.put("Network", systemOnline ? "CONNECTED" : "DISCONNECTED");
        return status;
    }

    /**
     * Toggles the printer status for testing.
     * <p>
     * Simulates printer failures to test error handling.
     * When a printer is unavailable, ticket printing should fail gracefully.
     * </p>
     */
    public void togglePrinter() {
        printerAvailable = !printerAvailable;
    }

    /**
     * Toggles the network status for testing.
     * <p>
     * Simulates network outages to test offline mode.
     * When offline, some operations (like card recharges) may be queued.
     * </p>
     */
    public void toggleNetwork() {
        systemOnline = !systemOnline;
    }

    public boolean isPrinterAvailable() {
        return printerAvailable;
    }

    /**
     * Checks if network connectivity is available.
     * <p>
     * Other services should check this before attempting
     * operations that require network access.
     * </p>
     * @return true if network is available, false if simulating offline mode
     */
    public boolean isNetworkAvailable() {
        return systemOnline;
    }
}
