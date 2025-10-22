package ca.concordia.igo.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MaintenanceServiceTest {

    @Test
    void authenticateValidatesPin() {
        MaintenanceService service = new MaintenanceService();

        assertTrue(service.authenticate("9999"));
        assertFalse(service.authenticate("1234"));
    }

    @Test
    void togglePrinterUpdatesStatusAndDiagnostics() {
        MaintenanceService service = new MaintenanceService();
        assertTrue(service.isPrinterAvailable());

        service.togglePrinter();

        assertFalse(service.isPrinterAvailable());
        Map<String, String> diagnostics = service.getDiagnostics();
        assertEquals("ERROR", diagnostics.get("Printer"));
    }

    @Test
    void toggleNetworkUpdatesStatusAndDiagnostics() {
        MaintenanceService service = new MaintenanceService();
        assertTrue(service.isNetworkAvailable());

        service.toggleNetwork();

        assertFalse(service.isNetworkAvailable());
        Map<String, String> diagnostics = service.getDiagnostics();
        assertEquals("OFFLINE", diagnostics.get("System"));
        assertEquals("DISCONNECTED", diagnostics.get("Network"));
    }
}
