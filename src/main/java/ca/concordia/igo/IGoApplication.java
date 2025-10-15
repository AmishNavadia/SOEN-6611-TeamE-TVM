package ca.concordia.igo;

import ca.concordia.igo.service.FareCalculator;
import ca.concordia.igo.service.MaintenanceService;
import ca.concordia.igo.service.PaymentService;
import ca.concordia.igo.service.PrestoCardService;
import ca.concordia.igo.service.SessionManager;
import ca.concordia.igo.service.TicketPrinter;
import ca.concordia.igo.service.TransactionRepository;
import ca.concordia.igo.ui.DialogHelper;
import ca.concordia.igo.ui.UIComponents;
import ca.concordia.igo.ui.screens.BalanceScreen;
import ca.concordia.igo.ui.screens.BuyTicketScreen;
import ca.concordia.igo.ui.screens.LanguageScreen;
import ca.concordia.igo.ui.screens.MainMenuScreen;
import ca.concordia.igo.ui.screens.MaintenanceDashboardScreen;
import ca.concordia.igo.ui.screens.MaintenanceLoginScreen;
import ca.concordia.igo.ui.screens.RechargeScreen;
import ca.concordia.igo.util.Language;
import ca.concordia.igo.util.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import static ca.concordia.igo.ui.ThemeConstants.BACKGROUND_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.DARK_TEXT;
import static ca.concordia.igo.ui.ThemeConstants.PRIMARY_COLOR;

/**
 * Main JavaFX application responsible for wiring services and managing screen
 * navigation.
 * Implements a service-oriented architecture with manual dependency injection.
 * Manages application state including language preference and UI transitions.
 * Provides centralized access to all business services for screen components.
 */
public class IGoApplication extends Application {

    // Service layer - singleton instances for business logic
    private final FareCalculator fareCalculator = new FareCalculator();
    private final PaymentService paymentService = new PaymentService();
    private final PrestoCardService prestoService = new PrestoCardService();
    private final TicketPrinter ticketPrinter = new TicketPrinter();
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final MaintenanceService maintenanceService = new MaintenanceService();
    private final SessionManager sessionManager = new SessionManager();

    // UI state management
    private Language currentLanguage = Language.EN;
    private BorderPane mainContainer;
    private StackPane rootContainer;
    private DialogHelper dialogHelper;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the JavaFX application with UI containers and service wiring.
     * Sets up the primary stage with fixed dimensions and custom stylesheet.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("iGo - PRESTO Ticket Vending Machine");

        // Main content container
        mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // Root container for overlay support
        rootContainer = new StackPane(mainContainer);
        rootContainer.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // Initialize dialog helper with necessary dependencies
        dialogHelper = new DialogHelper(rootContainer, this::getCurrentLanguage, this::t, this::fadeTransition);

        showLanguageScreen();

        Scene scene = new Scene(rootContainer, 1024, 768);
        scene.getStylesheets().add(getStylesheet());

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        Logger.info("Application initialized successfully");
        primaryStage.show();
    }

    // Service accessors for screen components
    public FareCalculator getFareCalculator() {
        return fareCalculator;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public PrestoCardService getPrestoService() {
        return prestoService;
    }

    public TicketPrinter getTicketPrinter() {
        return ticketPrinter;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public MaintenanceService getMaintenanceService() {
        return maintenanceService;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public DialogHelper getDialogHelper() {
        return dialogHelper;
    }


    /**
     * Navigates to a new screen by replacing the main content area.
     */
    public void navigateTo(Node view) {
        Logger.userAction("SCREEN_NAVIGATION", "Navigating to: " +
                view.getClass().getSimpleName());
        mainContainer.setCenter(view);
    }

    // Language management
    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(Language language) {
        currentLanguage = language;
    }

    // Screen navigation methods
    public void showLanguageScreen() {
        new LanguageScreen(this).show();
    }

    public void showMainMenu() {
        new MainMenuScreen(this).show();
    }

    public void showBuyTicketScreen() {
        new BuyTicketScreen(this).show();
    }

    public void showRechargeScreen() {
        new RechargeScreen(this).show();
    }

    public void showBalanceScreen() {
        new BalanceScreen(this).show();
    }

    public void showMaintenanceLoginScreen() {
        new MaintenanceLoginScreen(this).show();
    }

    public void showMaintenanceDashboard() {
        new MaintenanceDashboardScreen(this).show();
    }
    
    /**
     * Starts a timed session with automatic timeout handling.
     * Returns to main menu after timeout with error message.
     */
    public void startTimedSession(BorderPane layout) {
        sessionManager.startSession(() -> {
            sessionManager.endSession();
            showStyledError(t("session.timeout"));
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> fadeTransition(layout, this::showMainMenu));
            pause.play();
        });
    }

    /**
     * Parses currency amount from user input, handling various formats.
     * Removes dollar signs and commas before parsing.
     *
     * @return parsed amount or null if invalid
     */
    public Double parseAmount(String raw) {
        if (raw == null) {
            return null;
        }
        String sanitized = raw.replace("$", "").replace(",", "").trim();
        if (sanitized.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(sanitized);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Creates the application footer with help text and language switcher.
     */
    public HBox createFooter() {
        return UIComponents.createFooter(
                t("footer.help"),
                this::getLanguageSwitchLabel,
                () -> {
                    currentLanguage = currentLanguage == Language.EN ? Language.FR : Language.EN;
                    fadeTransition(mainContainer, this::showMainMenu);
                }
        );
    }
    
    /**
     * Returns the language switcher button label in current language.
     */
    private String getLanguageSwitchLabel() {
        if (currentLanguage == Language.FR) {
            return "Langue : Français | English";
        }
        return "Language: English | Français";
    }

    // Dialog and overlay methods delegating to DialogHelper
    public void showProcessingOverlay(BorderPane layout, String message) {
        dialogHelper.showProcessingOverlay(layout, message);
    }

    public void showStyledSuccess(String title, String receipt, BorderPane layout) {
        dialogHelper.showStyledSuccess(title, receipt, layout, this::showMainMenu);
    }

    public void showStyledError(String message) {
        dialogHelper.showStyledError(message);
    }

    public void showOverlayMessage(String titleText,
                                   String message,
                                   javafx.scene.paint.Color accentColor,
                                   Duration duration,
                                   double maxWidth) {
        dialogHelper.showOverlayMessage(titleText, message, accentColor, duration, maxWidth);
    }

    /**
     * Performs a fade transition between screens.
     * Fades out current content, executes callback, then fades in new content.
     */
    public void fadeTransition(Region node, Runnable onFinish) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            onFinish.run();
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), mainContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    /**
     * Generates inline CSS stylesheet for custom UI components.
     * Defines styles for combo boxes and text field focus states.
     */
    private String getStylesheet() {
        return "data:text/css," + String.join(" ",
                ".button:hover { -fx-cursor: hand; }",
                ".text-field:focused { -fx-border-color: " + PRIMARY_COLOR + "; }",
                ".combo-box.igo-combo { -fx-font-size: 16px; -fx-pref-width: 350px; -fx-background-color: white; " +
                        "-fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: transparent; " +
                        "-fx-padding: 6 16 6 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0, 0, 2); }",
                ".combo-box.igo-combo:focused, .combo-box.igo-combo:showing { -fx-border-color: " + PRIMARY_COLOR + "; " +
                        "-fx-border-width: 2; -fx-effect: dropshadow(gaussian, rgba(0,166,81,0.35), 18, 0, 0, 4); }",
                ".combo-box.igo-combo .list-cell { -fx-text-fill: " + DARK_TEXT + "; -fx-background-color: transparent; }",
                ".combo-box.igo-combo .arrow-button { -fx-background-color: transparent; -fx-padding: 0 10 0 0; }",
                ".combo-box.igo-combo .arrow-button:hover { -fx-background-color: rgba(0,0,0,0.05); -fx-background-radius: 8; }",
                ".combo-box-popup .list-view { -fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 16, 0, 0, 4); -fx-padding: 4; }",
                ".combo-box-popup .list-view .list-cell { -fx-padding: 10 16; -fx-font-size: 15px; -fx-background-color: transparent; }",
                ".combo-box-popup .list-view .list-cell:filled:selected, .combo-box-popup .list-view .list-cell:filled:hover { " +
                        "-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; }"
        );
    }
    
    /**
     * Translates text keys to localized strings based on current language.
     * Provides bilingual support for English and French.
     *
     * @param key the translation key
     * @return localized string
     */
    public String t(String key) {
        return switch (key) {
            // Maintenance screen translations
            case "maint.title" -> currentLanguage == Language.FR ? "Mode Maintenance" : "Maintenance Mode";
            case "maint.access" -> currentLanguage == Language.FR ? "Accès Technicien" : "Technician Access";
            case "maint.pin" -> currentLanguage == Language.FR ? "Code PIN" : "PIN Code";
            case "maint.login" -> currentLanguage == Language.FR ? "Connexion" : "Login";
            case "maint.badpin" -> currentLanguage == Language.FR ? "PIN incorrect" : "Incorrect PIN";
            case "maint.dashboard" -> currentLanguage == Language.FR
                    ? "Tableau de Bord Maintenance" : "Maintenance Dashboard";
            case "maint.system" -> currentLanguage == Language.FR ? "État du Système" : "System Status";
            case "maint.recent" -> currentLanguage == Language.FR ? "Transactions Récentes" : "Recent Transactions";
            case "maint.togglePrinter" -> currentLanguage == Language.FR ? "Basculer Imprimante" : "Toggle Printer";
            case "maint.toggleNetwork" -> currentLanguage == Language.FR ? "Basculer Réseau" : "Toggle Network";

            // Welcome screen translations
            case "welcome.title" -> "Welcome / Bienvenue";
            case "welcome.instruction" -> "Please select your language\nVeuillez sélectionner votre langue";
            case "lang.english" -> "English";
            case "lang.french" -> "Français";

            // Main menu translations
            case "menu.title" -> currentLanguage == Language.FR ? "Menu Principal" : "Main Menu";
            case "menu.buy" -> currentLanguage == Language.FR ? "Acheter un Billet" : "Buy Ticket";
            case "menu.buy.desc" -> currentLanguage == Language.FR
                    ? "Billets simples, aller-retour et laissez-passer"
                    : "Single, return and day pass tickets";
            case "menu.recharge" -> currentLanguage == Language.FR ? "Recharger PRESTO" : "Recharge PRESTO";
            case "menu.recharge.desc" -> currentLanguage == Language.FR
                    ? "Ajoutez des fonds à votre carte"
                    : "Add funds to your card";
            case "menu.balance" -> currentLanguage == Language.FR ? "Vérifier le Solde" : "Check Balance";
            case "menu.balance.desc" -> currentLanguage == Language.FR
                    ? "Consultez le solde de votre carte"
                    : "View your card balance";
            case "menu.maintenance" -> "Maintenance";
            case "menu.maintenance.desc" -> currentLanguage == Language.FR
                    ? "Accès technicien uniquement"
                    : "Technician access only";
            
            // Session management
            case "session.timeout" -> currentLanguage == Language.FR
                    ? "Session expirée. Retour au menu principal."
                    : "Session timeout. Returning to main menu.";

            // Recharge screen translations
            case "recharge.title" -> currentLanguage == Language.FR ? "Recharger PRESTO" : "Recharge PRESTO";
            case "recharge.tapCard" -> currentLanguage == Language.FR ? "Tapez votre carte PRESTO"
                    : "Tap your PRESTO card";
            case "recharge.cardNumber" -> currentLanguage == Language.FR ? "Numéro de carte" : "Card number";
            case "recharge.amount" -> currentLanguage == Language.FR ? "Montant à Recharger" : "Recharge Amount";
            case "recharge.recharge" -> currentLanguage == Language.FR ? "Recharger" : "Recharge";
            case "recharge.processing" -> currentLanguage == Language.FR ? "Traitement..." : "Processing...";
            case "recharge.success" -> currentLanguage == Language.FR ? "Rechargement réussi!" : "Recharge successful!";
            case "recharge.invalidAmount" -> currentLanguage == Language.FR ? "Montant invalide" : "Invalid amount";
            case "recharge.invalidCard" -> currentLanguage == Language.FR ? "Carte invalide" : "Invalid card";
            case "recharge.error" -> currentLanguage == Language.FR ? "Erreur de rechargement" : "Recharge error";
            case "recharge.newBalance" -> currentLanguage == Language.FR
                    ? "Nouveau solde: $%.2f" : "New balance: $%.2f";

            // Balance screen translations
            case "balance.title" -> currentLanguage == Language.FR ? "Vérifier le Solde" : "Check Balance";
            case "balance.tap" -> currentLanguage == Language.FR ? "Tapez votre carte PRESTO"
                    : "Tap your PRESTO card";
            case "balance.cardNumber" -> currentLanguage == Language.FR ? "Numéro de carte" : "Card number";
            case "balance.check" -> currentLanguage == Language.FR ? "Vérifier" : "Check";
            case "balance.error" -> currentLanguage == Language.FR ? "Erreur lors de la vérification"
                    : "Check error";

            // Purchase screen translations
            case "purchase.processing" -> currentLanguage == Language.FR ? "Traitement..." : "Processing...";
            case "purchase.success" -> currentLanguage == Language.FR ? "Billet acheté avec succès!"
                    : "Ticket purchased successfully!";
            case "purchase.error" -> currentLanguage == Language.FR ? "Erreur lors de l'achat"
                    : "Purchase error";
            
            // Common UI translations
            case "footer.help" -> currentLanguage == Language.FR
                    ? "Besoin d'aide? Appuyez sur le bouton d'assistance"
                    : "Need help? Press the assistance button";
            case "error.title" -> currentLanguage == Language.FR ? "Erreur" : "Error";
            case "common.back" -> currentLanguage == Language.FR ? "Retour" : "Back";

            // Default fallback
            default -> key;
        };
    }
}
