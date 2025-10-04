package ca.concordia.igo.service;

import java.util.HashMap;
import java.util.Map;

public class MaintenanceService {
    private static final String ADMIN_PIN = "9999";
    private boolean systemOnline = true;
    private boolean printerAvailable = true;
    private final boolean nfcReaderOk = true;

    public boolean authenticate(String pin) {
        return ADMIN_PIN.equals(pin);
    }

    public Map<String, String> getDiagnostics() {
        Map<String, String> status = new HashMap<>();
        status.put("System", systemOnline ? "ONLINE" : "OFFLINE");
        status.put("Printer", printerAvailable ? "OK" : "ERROR");
        status.put("NFC Reader", nfcReaderOk ? "OK" : "ERROR");
        status.put("Network", systemOnline ? "CONNECTED" : "DISCONNECTED");
        return status;
    }

    public void togglePrinter() {
        printerAvailable = !printerAvailable;
    }

    public void toggleNetwork() {
        systemOnline = !systemOnline;
    }

    public boolean isPrinterAvailable() {
        return printerAvailable;
    }

    public boolean isNetworkAvailable() {
        return systemOnline;
    }
}
