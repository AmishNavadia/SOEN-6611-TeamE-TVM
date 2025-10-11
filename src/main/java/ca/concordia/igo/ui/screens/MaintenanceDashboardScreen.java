package ca.concordia.igo.ui.screens;

import ca.concordia.igo.IGoApplication;
import ca.concordia.igo.model.Transaction;
import ca.concordia.igo.ui.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static ca.concordia.igo.ui.ThemeConstants.BACKGROUND_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.ERROR_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.SECONDARY_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.SUCCESS_COLOR;

/**
 * Technician dashboard displaying system diagnostics and transaction logs.
 * Shows real-time status of printer, network, and NFC reader.
 * Allows toggling of hardware components for testing error handling.
 */
public class MaintenanceDashboardScreen {

    private final IGoApplication app;

    public MaintenanceDashboardScreen(IGoApplication app) {
        this.app = app;
    }

    /**
     * Displays the maintenance dashboard with current system status.
     */
    public void show() {
        BorderPane layout = buildView();
        app.navigateTo(layout);
    }

    /**
     * Constructs the dashboard layout with diagnostics and controls.
     */
    private BorderPane buildView() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        layout.setTop(UIComponents.createHeader(app.t("maint.dashboard")));

        VBox content = new VBox(25);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30));
        
        // System diagnostics display
        VBox diagnosticsBox = new VBox(15);
        diagnosticsBox.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; " +
                        "-fx-padding: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        diagnosticsBox.setMaxWidth(700);

        Label diagTitle = new Label(app.t("maint.system"));
        diagTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        GridPane diagGrid = new GridPane();
        diagGrid.setHgap(20);
        diagGrid.setVgap(10);

        // Populate diagnostics grid with component status
        Map<String, String> diagnostics = app.getMaintenanceService().getDiagnostics();
        int row = 0;
        for (Map.Entry<String, String> entry : diagnostics.entrySet()) {
            Label keyLabel = new Label(entry.getKey() + ":");
            keyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            Label valueLabel = new Label(entry.getValue());
            valueLabel.setFont(Font.font("Arial", 14));
            boolean ok = entry.getValue().contains("OK") || entry.getValue().contains("ONLINE");
            valueLabel.setTextFill(ok ? Color.web(SUCCESS_COLOR) : Color.web(ERROR_COLOR));

            diagGrid.add(keyLabel, 0, row);
            diagGrid.add(valueLabel, 1, row);
            row++;
        }

        diagnosticsBox.getChildren().addAll(diagTitle, diagGrid);

        // Transaction log display
        VBox transactionsBox = new VBox(15);
        transactionsBox.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; " +
                        "-fx-padding: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        transactionsBox.setMaxWidth(700);

        Label txTitle = new Label(app.t("maint.recent"));
        txTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TextArea txArea = new TextArea();
        txArea.setEditable(false);
        txArea.setPrefRowCount(8);
        txArea.setFont(Font.font("Courier New", 12));
        
        // Load recent transactions into log
        List<Transaction> recent = app.getTransactionRepository().getRecent(10);
        StringBuilder txLog = new StringBuilder();
        DateTimeFormatter hhmmss = DateTimeFormatter.ofPattern("HH:mm:ss");
        for (Transaction tx : recent) {
            txLog.append(String.format("[%s] %s - $%.2f - %s%n",
                    tx.getTimestamp().format(hhmmss),
                    tx.getType(),
                    tx.getAmount(),
                    tx.getStatus()));
        }
        txArea.setText(txLog.toString());

        transactionsBox.getChildren().addAll(txTitle, txArea);

        // Control buttons for toggling hardware
        HBox controlBox = new HBox(15);
        controlBox.setAlignment(Pos.CENTER);

        Button togglePrinterBtn = UIComponents.createActionButton(app.t("maint.togglePrinter"), SECONDARY_COLOR, false);
        togglePrinterBtn.setOnAction(e -> onTogglePrinter(layout));

        Button toggleNetworkBtn = UIComponents.createActionButton(app.t("maint.toggleNetwork"), SECONDARY_COLOR, false);
        toggleNetworkBtn.setOnAction(e -> onToggleNetwork(layout));

        Button backBtn = UIComponents.createActionButton(app.t("common.back"), ERROR_COLOR, false);
        backBtn.setOnAction(e -> app.fadeTransition(layout, app::showMainMenu));

        controlBox.getChildren().addAll(togglePrinterBtn, toggleNetworkBtn, backBtn);

        content.getChildren().addAll(diagnosticsBox, transactionsBox, controlBox);
        layout.setCenter(content);
        layout.setBottom(app.createFooter());

        return layout;
    }
    
    /**
     * Toggles printer availability and refreshes dashboard.
     */
    private void onTogglePrinter(BorderPane layout) {
        app.getMaintenanceService().togglePrinter();
        app.getTicketPrinter().setPrinterAvailable(app.getMaintenanceService().isPrinterAvailable());
        app.fadeTransition(layout, app::showMaintenanceDashboard);
    }

    /**
     * Toggles network connectivity and refreshes dashboard.
     */
    private void onToggleNetwork(BorderPane layout) {
        app.getMaintenanceService().toggleNetwork();
        app.getPrestoService().setNetworkAvailable(app.getMaintenanceService().isNetworkAvailable());
        app.fadeTransition(layout, app::showMaintenanceDashboard);
    }
}
