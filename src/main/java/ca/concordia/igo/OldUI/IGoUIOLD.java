package ca.concordia.igo.OldUI;
//package ca.concordia.igo;
//
//import ca.concordia.igo.exception.InvalidCardException;
//import ca.concordia.igo.exception.PaymentFailedException;
//import ca.concordia.igo.model.*;
//import ca.concordia.igo.service.*;
//import ca.concordia.igo.util.Language;
//import ca.concordia.igo.util.Logger;
//import javafx.animation.FadeTransition;
//import javafx.animation.PauseTransition;
//import javafx.animation.ScaleTransition;
//import javafx.animation.TranslateTransition;
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.event.Event;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Node;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.effect.DropShadow;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
//import javafx.scene.text.TextAlignment;
//import javafx.stage.Stage;
//import javafx.util.Duration;
//
//import java.net.URLEncoder;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Map;
//
///**
// * Enhanced JavaFX application for iGo TVM system with beautiful, modern UI.
// * Optimized for presentation and demonstration purposes.
// */
//public class IGoUIOLD extends Application {
//        // Color Scheme - Modern TVM Design
//        private static final String PRIMARY_COLOR = "#00A651"; // GO Transit Green
//        private static final String SECONDARY_COLOR = "#003DA5"; // PRESTO Blue
//        private static final String ACCENT_COLOR = "#FFB81C"; // Warning Yellow
//        private static final String BACKGROUND_COLOR = "#F5F5F5"; // Light Grey
//        private static final String DARK_TEXT = "#2C3E50"; // Dark Blue-Grey
//        private static final String SUCCESS_COLOR = "#27AE60"; // Green
//        private static final String ERROR_COLOR = "#E74C3C"; // Red
//        private final FareCalculator fareCalculator = new FareCalculator();
//        private final PaymentService paymentService = new PaymentService();
//        private final PrestoCardService prestoService = new PrestoCardService();
//        private final TicketPrinter ticketPrinter = new TicketPrinter();
//        // Add to existing fields
//        private final TransactionRepository transactionRepository = new TransactionRepository();
//        private final MaintenanceService maintenanceService = new MaintenanceService();
//        private final SessionManager sessionManager = new SessionManager();
//        private Language currentLanguage = Language.EN;
//        private BorderPane mainContainer;
//        private StackPane rootContainer;
//
//        public static void main(String[] args) {
//                launch(args);
//        }
//
//        // ========================================================================
//        // MAINTENANCE SCREEN (USE CASE 6)
//        // ========================================================================
//
//        @Override
//        public void start(Stage primaryStage) {
//            primaryStage.setTitle("iGo - PRESTO Ticket Vending Machine");
//
//                mainContainer = new BorderPane();
//                mainContainer.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
//
//                rootContainer = new StackPane(mainContainer);
//                rootContainer.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
//
//                showLanguageSelection();
//
//                Scene scene = new Scene(rootContainer, 1024, 768);
//                scene.getStylesheets().add(getStylesheet());
//
//                primaryStage.setScene(scene);
//                primaryStage.setResizable(false);
//                primaryStage.show();
//        }
//
//        private void showMaintenanceLogin() {
//                BorderPane layout = new BorderPane();
//                layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
//
//                VBox header = createHeader(
//                                currentLanguage == Language.FR ? "Mode Maintenance" : "Maintenance Mode");
//                layout.setTop(header);
//
//                VBox content = new VBox(30);
//                content.setAlignment(Pos.CENTER);
//                content.setPadding(new Insets(60));
//
//                Label lockIcon = new Label("🔒");
//                lockIcon.setFont(Font.font(72));
//
//                Label titleLabel = new Label(
//                                currentLanguage == Language.FR ? "Accès Technicien" : "Technician Access");
//                titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
//                titleLabel.setTextFill(Color.web(DARK_TEXT));
//
//                PasswordField pinField = new PasswordField();
//                pinField.setPromptText(currentLanguage == Language.FR ? "Code PIN" : "PIN Code");
//                pinField.setMaxWidth(300);
//                pinField.setFont(Font.font("Arial", 18));
//                pinField.setStyle("-fx-background-radius: 8; -fx-border-color: #ccc; " +
//                                "-fx-border-radius: 8; -fx-padding: 12;");
//
//                Label errorLabel = new Label("");
//                errorLabel.setTextFill(Color.web(ERROR_COLOR));
//                errorLabel.setVisible(false);
//
//                HBox buttonBox = new HBox(20);
//                buttonBox.setAlignment(Pos.CENTER);
//
//                Button loginBtn = createActionButton(
//                                currentLanguage == Language.FR ? "Connexion" : "Login",
//                                PRIMARY_COLOR,
//                                true);
//                loginBtn.setOnAction(e -> {
//                        if (maintenanceService.authenticate(pinField.getText())) {
//                                fadeTransition(layout, this::showMaintenanceDashboard);
//                        } else {
//                                errorLabel.setText(
//                                                currentLanguage == Language.FR ? "PIN incorrect" : "Incorrect PIN");
//                                errorLabel.setVisible(true);
//                                pinField.clear();
//                        }
//                });
//
//                Button backBtn = createActionButton(
//                                currentLanguage == Language.FR ? "Retour" : "Back",
//                                ERROR_COLOR,
//                                false);
//                backBtn.setOnAction(e -> fadeTransition(layout, this::showMainMenu));
//
//                buttonBox.getChildren().addAll(loginBtn, backBtn);
//
//                content.getChildren().addAll(lockIcon, titleLabel, pinField, errorLabel, buttonBox);
//                layout.setCenter(content);
//
//                HBox footer = createFooter();
//                layout.setBottom(footer);
//
//                mainContainer.setCenter(layout);
//        }
//        // ========================================================================
//        // LANGUAGE SELECTION SCREEN
//        // ========================================================================
//
//        private void showMaintenanceDashboard() {
//                BorderPane layout = new BorderPane();
//                layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
//
//                VBox header = createHeader(
//                                currentLanguage == Language.FR ? "Tableau de Bord Maintenance"
//                                                : "Maintenance Dashboard");
//                layout.setTop(header);
//
//                VBox content = new VBox(25);
//                content.setAlignment(Pos.TOP_CENTER);
//                content.setPadding(new Insets(30));
//
//                // System Diagnostics Panel
//                VBox diagnosticsBox = new VBox(15);
//                diagnosticsBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
//                                "-fx-padding: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
//                diagnosticsBox.setMaxWidth(700);
//
//                Label diagTitle = new Label(
//                                currentLanguage == Language.FR ? "État du Système" : "System Status");
//                diagTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
//
//                GridPane diagGrid = new GridPane();
//                diagGrid.setHgap(20);
//                diagGrid.setVgap(10);
//
//                Map<String, String> diagnostics = maintenanceService.getDiagnostics();
//                int row = 0;
//                for (Map.Entry<String, String> entry : diagnostics.entrySet()) {
//                        Label keyLabel = new Label(entry.getKey() + ":");
//                        keyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
//
//                        Label valueLabel = new Label(entry.getValue());
//                        valueLabel.setFont(Font.font("Arial", 14));
//                        valueLabel.setTextFill(entry.getValue().contains("OK") || entry.getValue().contains("ONLINE")
//                                        ? Color.web(SUCCESS_COLOR)
//                                        : Color.web(ERROR_COLOR));
//
//                        diagGrid.add(keyLabel, 0, row);
//                        diagGrid.add(valueLabel, 1, row);
//                        row++;
//                }
//
//                diagnosticsBox.getChildren().addAll(diagTitle, diagGrid);
//
//                // Recent Transactions Panel
//                VBox transactionsBox = new VBox(15);
//                transactionsBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
//                                "-fx-padding: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
//                transactionsBox.setMaxWidth(700);
//
//                Label txTitle = new Label(
//                                currentLanguage == Language.FR ? "Transactions Récentes" : "Recent Transactions");
//                txTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
//
//                TextArea txArea = new TextArea();
//                txArea.setEditable(false);
//                txArea.setPrefRowCount(8);
//                txArea.setFont(Font.font("Courier New", 12));
//
//                List<Transaction> recent = transactionRepository.getRecent(10);
//                StringBuilder txLog = new StringBuilder();
//                for (Transaction tx : recent) {
//                        txLog.append(String.format("[%s] %s - $%.2f - %s\n",
//                                        tx.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
//                                        tx.getType(),
//                                        tx.getAmount(),
//                                        tx.getStatus()));
//                }
//                txArea.setText(txLog.toString());
//
//                transactionsBox.getChildren().addAll(txTitle, txArea);
//
//                // Control Buttons
//                HBox controlBox = new HBox(15);
//                controlBox.setAlignment(Pos.CENTER);
//
//                Button togglePrinterBtn = createActionButton(
//                                currentLanguage == Language.FR ? "Basculer Imprimante" : "Toggle Printer",
//                                SECONDARY_COLOR,
//                                false);
//                togglePrinterBtn.setOnAction(e -> {
//                        maintenanceService.togglePrinter();
//                        ticketPrinter.setPrinterAvailable(maintenanceService.isPrinterAvailable());
//                        fadeTransition(layout, this::showMaintenanceDashboard);
//                });
//
//                Button toggleNetworkBtn = createActionButton(
//                                currentLanguage == Language.FR ? "Basculer Réseau" : "Toggle Network",
//                                SECONDARY_COLOR,
//                                false);
//                toggleNetworkBtn.setOnAction(e -> {
//                        maintenanceService.toggleNetwork();
//                        prestoService.setNetworkAvailable(maintenanceService.isNetworkAvailable());
//                        fadeTransition(layout, this::showMaintenanceDashboard);
//                });
//
//                Button backBtn = createActionButton(
//                                currentLanguage == Language.FR ? "Retour" : "Back",
//                                ERROR_COLOR,
//                                false);
//                backBtn.setOnAction(e -> fadeTransition(layout, this::showMainMenu));
//
//                controlBox.getChildren().addAll(togglePrinterBtn, toggleNetworkBtn, backBtn);
//
//                content.getChildren().addAll(diagnosticsBox, transactionsBox, controlBox);
//                layout.setCenter(content);
//
//                HBox footer = createFooter();
//                layout.setBottom(footer);
//
//                mainContainer.setCenter(layout);
//        }
//
//        // ========================================================================
//        // MAIN MENU SCREEN
//        // ========================================================================
//
//        private void showLanguageSelection() {
//                VBox content = new VBox(28);
//                content.setAlignment(Pos.CENTER);
//                content.setPadding(new Insets(36));
//                content.setStyle("-fx-background-color: linear-gradient(to bottom, " +
//                                SECONDARY_COLOR + ", " + PRIMARY_COLOR + ");");
//
//                // Logo/Title Area
//                VBox logoBox = createLogoBox();
//
//                // Welcome Message
//                Label welcomeLabel = new Label("Welcome / Bienvenue");
//                welcomeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 28));
//                welcomeLabel.setTextFill(Color.WHITE);
//                welcomeLabel.setStyle("-fx-opacity: 0.9;");
//
//                // Language Selection
//                HBox languageButtons = new HBox(30);
//                languageButtons.setAlignment(Pos.CENTER);
//
//                Button englishBtn = createLanguageButton("English");
//                englishBtn.setOnAction(e -> {
//                        currentLanguage = Language.EN;
//                        fadeTransition(content, this::showMainMenu);
//                });
//
//                Button frenchBtn = createLanguageButton("Français");
//                frenchBtn.setOnAction(e -> {
//                        currentLanguage = Language.FR;
//                        fadeTransition(content, this::showMainMenu);
//                });
//
//                languageButtons.getChildren().addAll(englishBtn, frenchBtn);
//
//                // Instruction Text
//                Label instructionLabel = new Label("Please select your language\nVeuillez sélectionner votre langue");
//                instructionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//                instructionLabel.setTextFill(Color.WHITE);
//                instructionLabel.setStyle("-fx-text-alignment: center;");
//                instructionLabel.setWrapText(true);
//
//                content.getChildren().addAll(logoBox, welcomeLabel, languageButtons, instructionLabel);
//
//                mainContainer.setCenter(content);
//
//                // Fade-in animation
//                FadeTransition fadeIn = new FadeTransition(Duration.millis(800), content);
//                fadeIn.setFromValue(0.0);
//                fadeIn.setToValue(1.0);
//                fadeIn.play();
//        }
//
//        // ========================================================================
//        // BUY TICKET SCREEN
//        // ========================================================================
//
//        private void showMainMenu() {
//                BorderPane layout = new BorderPane();
//                layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
//
//                // Header
//                VBox header = createHeader(
//                                currentLanguage == Language.FR ? "Menu Principal" : "Main Menu");
//                layout.setTop(header);
//
//                // Main Content
//                VBox content = new VBox(25);
//                content.setAlignment(Pos.CENTER);
//                content.setPadding(new Insets(40));
//
//                // Menu Options
//                Node ticketIcon = createMenuImage("/images/ticket.png", 96, 64);
//                Button buyTicketBtn = createMenuButton(
//                                currentLanguage == Language.FR ? "Acheter un Billet" : "Buy Ticket",
//                                currentLanguage == Language.FR ? "Billets simples, aller-retour et laissez-passer"
//                                                : "Single, return and day pass tickets",
//                                ticketIcon);
//                buyTicketBtn.setOnAction(e -> fadeTransition(layout, this::showBuyTicketScreen));
//
//                Node rechargeIcon = createMenuImage("/images/presto-card.png", 96, 64);
//                Button rechargeBtn = createMenuButton(
//                                currentLanguage == Language.FR ? "Recharger PRESTO" : "Recharge PRESTO",
//                                currentLanguage == Language.FR ? "Ajoutez des fonds à votre carte"
//                                                : "Add funds to your card",
//                                rechargeIcon);
//                rechargeBtn.setOnAction(e -> fadeTransition(layout, this::showRechargeScreen));
//
//                Button checkBalanceBtn = createMenuButton(
//                                currentLanguage == Language.FR ? "Vérifier le Solde" : "Check Balance",
//                                currentLanguage == Language.FR ? "Consultez le solde de votre carte"
//                                                : "View your card balance",
//                                "💰");
//                Button maintenanceBtn = createMenuButton(
//                                "Maintenance",
//                                currentLanguage == Language.FR ? "Accès technicien uniquement"
//                                                : "Technician access only",
//                                "🔧");
//                checkBalanceBtn.setOnAction(e -> fadeTransition(layout, this::showCheckBalanceScreen));
//
//                content.getChildren().addAll(buyTicketBtn, rechargeBtn, checkBalanceBtn, maintenanceBtn);
//                layout.setCenter(content);
//
//                maintenanceBtn.setOnAction(e -> fadeTransition(layout, this::showMaintenanceLogin));
//
//                // Footer
//                HBox footer = createFooter();
//                layout.setBottom(footer);
//
//                mainContainer.setCenter(layout);
//
//                // Slide-in animation
//                TranslateTransition slide = new TranslateTransition(Duration.millis(400), content);
//                slide.setFromX(100);
//                slide.setToX(0);
//                slide.play();
//        }
//
//        // ========================================================================
//        // RECHARGE SCREEN
//        // ========================================================================
//
//        private void showBuyTicketScreen() {
//                BorderPane layout = new BorderPane();
//                layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
//
//                VBox header = createHeader(
//                                currentLanguage == Language.FR ? "Acheter un Billet" : "Buy Ticket");
//                layout.setTop(header);
//
//                // START SESSION - 30 second timeout
//                sessionManager.startSession(() -> {
//                        sessionManager.endSession();
//                        showStyledError(
//                                        currentLanguage == Language.FR ? "Session expirée. Retour au menu principal."
//                                                        : "Session timeout. Returning to main menu.",
//                                        layout);
//                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
//                        pause.setOnFinished(e -> fadeTransition(layout, this::showMainMenu));
//                        pause.play();
//                });
//
//                VBox content = new VBox(20);
//                content.setAlignment(Pos.TOP_CENTER);
//                content.setPadding(new Insets(30));
//
//                // Instructions
//                Label instructionLabel = new Label(
//                                currentLanguage == Language.FR ? "Sélectionnez votre trajet et mode de paiement"
//                                                : "Select your trip and payment method");
//                instructionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//                instructionLabel.setTextFill(Color.web(DARK_TEXT));
//
//                // Form Container
//                VBox formBox = new VBox(15);
//                formBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
//                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
//                formBox.setPadding(new Insets(25));
//                formBox.setMaxWidth(600);
//
//                // Origin Selection
//                HBox originBox = createFormField(
//                                currentLanguage == Language.FR ? "Origine :" : "Origin:",
//                                null);
//                ComboBox<Zone> originCombo = createStyledComboBox();
//                originCombo.getItems().addAll(Zone.ZONE_1, Zone.ZONE_2, Zone.ZONE_3, Zone.ZONE_4);
//                originCombo.setValue(Zone.ZONE_1);
//                originBox.getChildren().add(originCombo);
//
//                // Destination Selection
//                HBox destBox = createFormField(
//                                currentLanguage == Language.FR ? "Destination :" : "Destination:",
//                                null);
//                ComboBox<Zone> destCombo = createStyledComboBox();
//                destCombo.getItems().addAll(Zone.ZONE_1, Zone.ZONE_2, Zone.ZONE_3, Zone.ZONE_4);
//                destCombo.setValue(Zone.ZONE_3);
//                destBox.getChildren().add(destCombo);
//
//                // Trip Type Selection
//                HBox tripBox = createFormField(
//                                currentLanguage == Language.FR ? "Type de Trajet :" : "Trip Type:",
//                                null);
//                ComboBox<TripType> tripCombo = createStyledComboBox();
//                tripCombo.getItems().addAll(TripType.values());
//                tripCombo.setValue(TripType.SINGLE);
//                tripBox.getChildren().add(tripCombo);
//
//                // Payment Method Selection
//                HBox paymentBox = createFormField(
//                                currentLanguage == Language.FR ? "Paiement :" : "Payment:",
//                                null);
//                ComboBox<PaymentMethod> paymentCombo = createStyledComboBox();
//                paymentCombo.getItems().addAll(PaymentMethod.values());
//                paymentCombo.setValue(PaymentMethod.CREDIT_CARD);
//                paymentBox.getChildren().add(paymentCombo);
//
//                formBox.getChildren().addAll(originBox, destBox, tripBox, paymentBox);
//
//                // Amount Display
//                Label amountLabel = new Label("$0.00");
//                amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
//                amountLabel.setTextFill(Color.web(PRIMARY_COLOR));
//                amountLabel.setStyle("-fx-background-color: white; -fx-padding: 20; " +
//                                "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
//
//                // Update amount when selections change
//                Runnable updateAmount = () -> {
//                        try {
//                                Fare fare = new Fare(originCombo.getValue(), destCombo.getValue(),
//                                                tripCombo.getValue());
//                                double amount = fareCalculator.calculateAmount(fare);
//                                amountLabel.setText(String.format("$%.2f", amount));
//
//                                // Pulse animation
//                                ScaleTransition pulse = new ScaleTransition(Duration.millis(200), amountLabel);
//                                pulse.setFromX(1.0);
//                                pulse.setFromY(1.0);
//                                pulse.setToX(1.1);
//                                pulse.setToY(1.1);
//                                pulse.setAutoReverse(true);
//                                pulse.setCycleCount(2);
//                                pulse.play();
//                        } catch (Exception e) {
//                                Logger.error("Error calculating fare", e);
//                        }
//                };
//
//                // SESSION RESET on all interactions
//                originCombo.setOnAction(e -> {
//                        sessionManager.resetTimer();
//                        updateAmount.run();
//                });
//
//                destCombo.setOnAction(e -> {
//                        sessionManager.resetTimer();
//                        updateAmount.run();
//                });
//
//                tripCombo.setOnAction(e -> {
//                        sessionManager.resetTimer();
//                        updateAmount.run();
//                });
//
//                paymentCombo.setOnAction(e -> sessionManager.resetTimer());
//
//                updateAmount.run();
//
//                // Action Buttons
//                HBox buttonBox = new HBox(20);
//                buttonBox.setAlignment(Pos.CENTER);
//
//                Button purchaseBtn = createActionButton(
//                                currentLanguage == Language.FR ? "Acheter" : "Purchase",
//                                SUCCESS_COLOR,
//                                true);
//                purchaseBtn.setOnAction(e -> {
//                        sessionManager.resetTimer();
//                        handlePurchase(
//                                        originCombo.getValue(),
//                                        destCombo.getValue(),
//                                        tripCombo.getValue(),
//                                        paymentCombo.getValue(),
//                                        layout);
//                });
//
//                Button cancelBtn = createActionButton(
//                                currentLanguage == Language.FR ? "Annuler" : "Cancel",
//                                ERROR_COLOR,
//                                false);
//                cancelBtn.setOnAction(e -> {
//                        sessionManager.endSession();
//                        fadeTransition(layout, this::showMainMenu);
//                });
//
//                buttonBox.getChildren().addAll(purchaseBtn, cancelBtn);
//
//                content.getChildren().addAll(instructionLabel, formBox, amountLabel, buttonBox);
//                layout.setCenter(content);
//
//                HBox footer = createFooter();
//                layout.setBottom(footer);
//
//                mainContainer.setCenter(layout);
//        }
//
//        // ========================================================================
//        // CHECK BALANCE SCREEN
//        // ========================================================================
//
//        private void showRechargeScreen() {
//                BorderPane layout = new BorderPane();
//                layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
//
//                VBox header = createHeader(
//                                currentLanguage == Language.FR ? "Recharger PRESTO" : "Recharge PRESTO");
//                layout.setTop(header);
//
//                // START SESSION - 30 second timeout
//                sessionManager.startSession(() -> {
//                        sessionManager.endSession();
//                        showStyledError(
//                                        currentLanguage == Language.FR ? "Session expirée. Retour au menu principal."
//                                                        : "Session timeout. Returning to main menu.",
//                                        layout);
//                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
//                        pause.setOnFinished(e -> fadeTransition(layout, this::showMainMenu));
//                        pause.play();
//                });
//
//                VBox content = new VBox(24);
//                content.setAlignment(Pos.CENTER);
//                content.setPadding(new Insets(32));
//
//                // Card Tap Instruction
//                VBox tapBox = new VBox(12);
//                tapBox.setAlignment(Pos.CENTER);
//                tapBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
//                                "-fx-padding: 28; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 3);");
//                tapBox.setMaxWidth(500);
//
//                ImageView tapIcon = createMenuImage("/images/presto-card.png", 180, 110);
//                tapIcon.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.25)));
//
//                Label tapLabel = new Label(
//                                currentLanguage == Language.FR ? "Tapez votre carte PRESTO" : "Tap your PRESTO card");
//                tapLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//                tapLabel.setTextFill(Color.web(DARK_TEXT));
//
//                TextField cardField = createStyledTextField(
//                                currentLanguage == Language.FR ? "Numéro de carte" : "Card number");
//                cardField.setMaxWidth(350);
//
//                // SESSION RESET on typing
//                cardField.setOnKeyTyped(e -> sessionManager.resetTimer());
//
//                tapBox.getChildren().addAll(tapIcon, tapLabel, cardField);
//
//                // Amount Selection
//                VBox amountBox = new VBox(12);
//                amountBox.setAlignment(Pos.CENTER);
//                amountBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
//                                "-fx-padding: 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
//                amountBox.setMaxWidth(500);
//
//                Label amountLabel = new Label(
//                                currentLanguage == Language.FR ? "Montant à Recharger" : "Recharge Amount");
//                amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//                amountLabel.setTextFill(Color.web(DARK_TEXT));
//
//                // Quick Amount Buttons
//                HBox quickAmounts = new HBox(15);
//                quickAmounts.setAlignment(Pos.CENTER);
//
//                TextField amountField = createStyledTextField("$");
//                amountField.setMaxWidth(200);
//
//                // SESSION RESET on typing
//                amountField.setOnKeyTyped(e -> sessionManager.resetTimer());
//
//                for (double amount : new double[] { 10, 20, 50, 100 }) {
//                        Button quickBtn = createQuickAmountButton(String.format("$%.0f", amount));
//                        quickBtn.setOnAction(e -> {
//                                sessionManager.resetTimer();
//                                amountField.setText(String.format("%.2f", amount));
//                        });
//                        quickAmounts.getChildren().add(quickBtn);
//                }
//
//                // Payment Method
//                ComboBox<PaymentMethod> paymentCombo = createStyledComboBox();
//                paymentCombo.getItems().addAll(PaymentMethod.values());
//                paymentCombo.setValue(PaymentMethod.CREDIT_CARD);
//                paymentCombo.setMaxWidth(350);
//
//                // SESSION RESET on selection
//                paymentCombo.setOnAction(e -> sessionManager.resetTimer());
//
//                amountBox.getChildren().addAll(amountLabel, quickAmounts, amountField, paymentCombo);
//
//                // Action Buttons
//                HBox buttonBox = new HBox(20);
//                buttonBox.setAlignment(Pos.CENTER);
//
//                Button rechargeBtn = createActionButton(
//                                currentLanguage == Language.FR ? "Recharger" : "Recharge",
//                                SUCCESS_COLOR,
//                                true);
//                rechargeBtn.setOnAction(e -> {
//                        sessionManager.resetTimer();
//                        handleRecharge(
//                                        cardField.getText(),
//                                        amountField.getText(),
//                                        paymentCombo.getValue(),
//                                        layout);
//                });
//
//                Button backBtn = createActionButton(
//                                currentLanguage == Language.FR ? "Retour" : "Back",
//                                ERROR_COLOR,
//                                false);
//                backBtn.setOnAction(e -> {
//                        sessionManager.endSession();
//                        fadeTransition(layout, this::showMainMenu);
//                });
//
//                buttonBox.getChildren().addAll(rechargeBtn, backBtn);
//
//                content.getChildren().addAll(tapBox, amountBox, buttonBox);
//                layout.setCenter(content);
//
//                HBox footer = createFooter();
//                layout.setBottom(footer);
//
//                mainContainer.setCenter(layout);
//        }
//
//        // ========================================================================
//        // BUSINESS LOGIC HANDLERS
//        // ========================================================================
//
//        private void showCheckBalanceScreen() {
//                BorderPane layout = new BorderPane();
//                layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
//
//                VBox header = createHeader(
//                                currentLanguage == Language.FR ? "Vérifier le Solde" : "Check Balance");
//                layout.setTop(header);
//
//                // START SESSION - 30 second timeout
//                sessionManager.startSession(() -> {
//                        sessionManager.endSession();
//                        showStyledError(
//                                        currentLanguage == Language.FR ? "Session expirée. Retour au menu principal."
//                                                        : "Session timeout. Returning to main menu.",
//                                        layout);
//                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
//                        pause.setOnFinished(e -> fadeTransition(layout, this::showMainMenu));
//                        pause.play();
//                });
//
//                VBox content = new VBox(40);
//                content.setAlignment(Pos.CENTER);
//                content.setPadding(new Insets(60));
//
//                // Card Tap Area
//                VBox tapBox = new VBox(16);
//                tapBox.setAlignment(Pos.CENTER);
//                tapBox.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
//                                "-fx-padding: 32; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 20, 0, 0, 4);");
//                tapBox.setMaxWidth(600);
//
//                ImageView tapIcon = createMenuImage("/images/presto-card.png", 180, 110);
//                tapIcon.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.25)));
//
//                Label tapLabel = new Label(
//                                currentLanguage == Language.FR ? "Tapez votre carte PRESTO" : "Tap your PRESTO card");
//                tapLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
//                tapLabel.setTextFill(Color.web(DARK_TEXT));
//
//                TextField cardField = createStyledTextField(
//                                currentLanguage == Language.FR ? "Numéro de carte" : "Card number");
//                cardField.setMaxWidth(400);
//
//                // SESSION RESET on typing
//                cardField.setOnKeyTyped(e -> sessionManager.resetTimer());
//
//                Label balanceLabel = new Label("");
//                balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 56));
//                balanceLabel.setTextFill(Color.web(SUCCESS_COLOR));
//                balanceLabel.setVisible(false);
//
//                tapBox.getChildren().addAll(tapIcon, tapLabel, cardField, balanceLabel);
//
//                // Action Buttons
//                HBox buttonBox = new HBox(20);
//                buttonBox.setAlignment(Pos.CENTER);
//
//                Button checkBtn = createActionButton(
//                                currentLanguage == Language.FR ? "Vérifier" : "Check",
//                                PRIMARY_COLOR,
//                                true);
//                checkBtn.setOnAction(e -> {
//                        sessionManager.resetTimer();
//                        try {
//                                String cardNumber = cardField.getText().trim();
//                                PrestoCard card = prestoService.readCard(cardNumber);
//
//                                balanceLabel.setText(String.format("$%.2f", card.getBalance()));
//                                balanceLabel.setVisible(true);
//
//                                // Success animation
//                                ScaleTransition scale = new ScaleTransition(Duration.millis(300), balanceLabel);
//                                scale.setFromX(0.5);
//                                scale.setFromY(0.5);
//                                scale.setToX(1.0);
//                                scale.setToY(1.0);
//                                scale.play();
//
//                        } catch (InvalidCardException ex) {
//                                showStyledError(ex.getUserMessage(currentLanguage), layout);
//                                balanceLabel.setVisible(false);
//                        } catch (Exception ex) {
//                                Logger.error("Balance check failed", ex);
//                                showStyledError(
//                                                currentLanguage == Language.FR ? "Erreur lors de la vérification"
//                                                                : "Check error",
//                                                layout);
//                                balanceLabel.setVisible(false);
//                        }
//                });
//
//                Button backBtn = createActionButton(
//                                currentLanguage == Language.FR ? "Retour" : "Back",
//                                ERROR_COLOR,
//                                false);
//                backBtn.setOnAction(e -> {
//                        sessionManager.endSession();
//                        fadeTransition(layout, this::showMainMenu);
//                });
//
//                buttonBox.getChildren().addAll(checkBtn, backBtn);
//
//                content.getChildren().addAll(tapBox, buttonBox);
//                layout.setCenter(content);
//
//                HBox footer = createFooter();
//                layout.setBottom(footer);
//
//                mainContainer.setCenter(layout);
//        }
//
//        private void handlePurchase(Zone origin, Zone dest, TripType tripType,
//                        PaymentMethod paymentMethod, BorderPane layout) {
//                try {
//                        Fare fare = new Fare(origin, dest, tripType);
//                        double amount = fareCalculator.calculateAmount(fare);
//
//                        Transaction transaction = new Transaction(
//                                        TransactionType.TICKET_PURCHASE,
//                                        amount,
//                                        paymentMethod);
//
//                        // Show processing overlay
//                        showProcessingOverlay(layout,
//                                        currentLanguage == Language.FR ? "Traitement..." : "Processing...");
//
//                        // Simulate payment processing
//                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
//                        pause.setOnFinished(e -> {
//                                try {
//                                        boolean success = paymentService.processPayment(transaction);
//
//                                        if (success) {
//                                                Ticket ticket = new Ticket(fare, amount,
//                                                                LocalDateTime.now().plusHours(2));
//
//                                                // LOG TRANSACTION
//                                                transactionRepository.log(transaction);
//
//                                                String receipt = ticketPrinter.printTicket(ticket, currentLanguage);
//
//                                                showStyledSuccess(
//                                                                currentLanguage == Language.FR
//                                                                                ? "Billet acheté avec succès!"
//                                                                                : "Ticket purchased successfully!",
//                                                                receipt,
//                                                                layout);
//                                        }
//                                } catch (PaymentFailedException ex) {
//                                        showStyledError(ex.getUserMessage(currentLanguage), layout);
//                                } catch (Exception ex) {
//                                        Logger.error("Purchase failed", ex);
//                                        showStyledError(
//                                                        currentLanguage == Language.FR ? "Erreur lors de l'achat"
//                                                                        : "Purchase error",
//                                                        layout);
//                                }
//                        });
//                        pause.play();
//
//                } catch (Exception ex) {
//                        Logger.error("Purchase initialization failed", ex);
//                        showStyledError(
//                                        currentLanguage == Language.FR ? "Erreur lors de l'achat" : "Purchase error",
//                                        layout);
//                }
//        }
//
//        // ========================================================================
//        // UI COMPONENT BUILDERS
//        // ========================================================================
//
//        private void handleRecharge(String cardNumber, String amountText,
//                        PaymentMethod paymentMethod, BorderPane layout) {
//                try {
//                        double amount = Double.parseDouble(amountText.replace("$", "").trim());
//
//                        PrestoCard card = prestoService.readCard(cardNumber.trim());
//
//                        Transaction transaction = new Transaction(
//                                        TransactionType.CARD_RECHARGE,
//                                        amount,
//                                        paymentMethod);
//
//                        // LOG TRANSACTION
//                        transactionRepository.log(transaction);
//
//                        showProcessingOverlay(layout,
//                                        currentLanguage == Language.FR ? "Traitement..." : "Processing...");
//
//                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
//                        pause.setOnFinished(e -> {
//                                try {
//                                        boolean success = paymentService.processPayment(transaction);
//
//                                        if (success) {
//                                                prestoService.rechargeCard(card, amount);
//                                                showStyledSuccess(
//                                                                currentLanguage == Language.FR ? "Rechargement réussi!"
//                                                                                : "Recharge successful!",
//                                                                String.format(
//                                                                                currentLanguage == Language.FR
//                                                                                                ? "Nouveau solde: $%.2f"
//                                                                                                : "New balance: $%.2f",
//                                                                                card.getBalance()),
//                                                                layout);
//                                        }
//                                } catch (Exception ex) {
//                                        showStyledError(ex.getMessage(), layout);
//                                }
//                        });
//                        pause.play();
//
//                } catch (InvalidCardException ex) {
//                        showStyledError(ex.getUserMessage(currentLanguage), layout);
//                } catch (NumberFormatException ex) {
//                        showStyledError(
//                                        currentLanguage == Language.FR ? "Montant invalide" : "Invalid amount",
//                                        layout);
//                } catch (Exception ex) {
//                        Logger.error("Recharge failed", ex);
//                        showStyledError(
//                                        currentLanguage == Language.FR ? "Erreur de rechargement" : "Recharge error",
//                                        layout);
//                }
//        }
//
//        private VBox createLogoBox() {
//                VBox logoBox = new VBox(10);
//                logoBox.setAlignment(Pos.CENTER);
//
//                Label logoLabel = new Label("iGo");
//                logoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 72));
//                logoLabel.setTextFill(Color.WHITE);
//                logoLabel.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));
//
//                Label subtitleLabel = new Label("PRESTO Ticket Vending Machine");
//                subtitleLabel.setFont(Font.font("Arial", FontWeight.LIGHT, 20));
//                subtitleLabel.setTextFill(Color.WHITE);
//
//                logoBox.getChildren().addAll(logoLabel, subtitleLabel);
//                return logoBox;
//        }
//
//        private VBox createHeader(String title) {
//                VBox header = new VBox();
//                header.setStyle("-fx-background-color: linear-gradient(to right, " +
//                                SECONDARY_COLOR + ", " + PRIMARY_COLOR + "); -fx-padding: 20;");
//                header.setAlignment(Pos.CENTER);
//
//                Label titleLabel = new Label(title);
//                titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
//                titleLabel.setTextFill(Color.WHITE);
//
//                // Current Time
//                Label timeLabel = new Label(LocalDateTime.now().format(
//                                DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy • HH:mm")));
//                timeLabel.setFont(Font.font("Arial", FontWeight.LIGHT, 14));
//                timeLabel.setTextFill(Color.WHITE);
//                timeLabel.setStyle("-fx-opacity: 0.9;");
//
//                header.getChildren().addAll(titleLabel, timeLabel);
//                return header;
//        }
//
//        private HBox createFooter() {
//                HBox footer = new HBox(30);
//                footer.setAlignment(Pos.CENTER);
//                footer.setPadding(new Insets(15));
//                footer.setStyle("-fx-background-color: " + DARK_TEXT + ";");
//
//                Label helpLabel = new Label(
//                                currentLanguage == Language.FR ? "Besoin d'aide? Appuyez sur le bouton d'assistance"
//                                                : "Need help? Press the assistance button");
//                helpLabel.setFont(Font.font("Arial", 12));
//                helpLabel.setTextFill(Color.WHITE);
//
//                Region spacer = new Region();
//                HBox.setHgrow(spacer, Priority.ALWAYS);
//
//                Button languageButton = new Button(getLanguageSwitchLabel());
//                languageButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
//                languageButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ACCENT_COLOR + "; " +
//                                "-fx-border-color: " + ACCENT_COLOR
//                                + "; -fx-border-radius: 12; -fx-background-radius: 12; " +
//                                "-fx-padding: 6 14; -fx-cursor: hand;");
//                languageButton.setOnMouseEntered(e -> languageButton.setStyle(
//                                "-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: " + DARK_TEXT + "; " +
//                                                "-fx-border-color: transparent; -fx-border-radius: 12; -fx-background-radius: 12; "
//                                                +
//                                                "-fx-padding: 6 14; -fx-cursor: hand;"));
//                languageButton.setOnMouseExited(e -> languageButton.setStyle(
//                                "-fx-background-color: transparent; -fx-text-fill: " + ACCENT_COLOR + "; " +
//                                                "-fx-border-color: " + ACCENT_COLOR
//                                                + "; -fx-border-radius: 12; -fx-background-radius: 12; " +
//                                                "-fx-padding: 6 14; -fx-cursor: hand;"));
//                languageButton.setOnAction(e -> {
//                        currentLanguage = currentLanguage == Language.EN ? Language.FR : Language.EN;
//                        languageButton.setText(getLanguageSwitchLabel());
//                        fadeTransition(mainContainer, this::showMainMenu);
//                });
//
//                footer.getChildren().addAll(helpLabel, spacer, languageButton);
//
//                return footer;
//        }
//
//        private String getLanguageSwitchLabel() {
//                if (currentLanguage == Language.FR) {
//                        return "Langue : Français | English";
//                }
//                return "Language: English | Français";
//        }
//
//        private Button createLanguageButton(String text) {
//                Button btn = new Button(text);
//                btn.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//                btn.setPrefSize(280, 100);
//                btn.setStyle("-fx-background-color: white; -fx-text-fill: " + DARK_TEXT + "; " +
//                                "-fx-background-radius: 15; -fx-cursor: hand; " +
//                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");
//
//                btn.setOnMouseEntered(e -> {
//                        btn.setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: white; " +
//                                        "-fx-background-radius: 15; -fx-cursor: hand; " +
//                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 8);");
//                        ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
//                        st.setToX(1.05);
//                        st.setToY(1.05);
//                        st.play();
//                });
//
//                btn.setOnMouseExited(e -> {
//                        btn.setStyle("-fx-background-color: white; -fx-text-fill: " + DARK_TEXT + "; " +
//                                        "-fx-background-radius: 15; -fx-cursor: hand; " +
//                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");
//                        ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
//                        st.setToX(1.0);
//                        st.setToY(1.0);
//                        st.play();
//                });
//
//                return btn;
//        }
//
//        private Button createMenuButton(String title, String description, String emoji) {
//                Label emojiLabel = new Label(emoji);
//                emojiLabel.setFont(Font.font(48));
//                emojiLabel.setTextFill(Color.web(DARK_TEXT));
//                return createMenuButton(title, description, emojiLabel);
//        }
//
//        private Button createMenuButton(String title, String description, Node iconNode) {
//                VBox btnContent = new VBox(10);
//                btnContent.setAlignment(Pos.CENTER_LEFT);
//                btnContent.setPadding(new Insets(20));
//
//                HBox titleBox = new HBox(18);
//                titleBox.setAlignment(Pos.CENTER_LEFT);
//
//                Node icon = iconNode;
//                if (icon instanceof Label label) {
//                        label.setFont(Font.font(48));
//                        label.setTextFill(Color.web(DARK_TEXT));
//                } else if (icon instanceof ImageView imageView) {
//                        imageView.setFitWidth(72);
//                        imageView.setFitHeight(48);
//                        imageView.setPreserveRatio(true);
//                        imageView.setSmooth(true);
//                }
//
//                VBox textBox = new VBox(5);
//                Label titleLabel = new Label(title);
//                titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//                titleLabel.setTextFill(Color.web(DARK_TEXT));
//
//                Label descLabel = new Label(description);
//                descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
//                descLabel.setTextFill(Color.web(DARK_TEXT));
//                descLabel.setStyle("-fx-opacity: 0.7;");
//
//                textBox.getChildren().addAll(titleLabel, descLabel);
//                titleBox.getChildren().addAll(icon, textBox);
//                btnContent.getChildren().add(titleBox);
//
//                Button btn = new Button();
//                btn.setGraphic(btnContent);
//                btn.setPrefSize(500, 110);
//                btn.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
//                                "-fx-cursor: hand; -fx-border-color: transparent; " +
//                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
//
//                btn.setOnMouseEntered(e -> {
//                        btn.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
//                                        "-fx-cursor: hand; -fx-border-color: " + PRIMARY_COLOR + "; " +
//                                        "-fx-border-width: 3; " +
//                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");
//                        ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
//                        st.setToX(1.02);
//                        st.setToY(1.02);
//                        st.play();
//                });
//
//                btn.setOnMouseExited(e -> {
//                        btn.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
//                                        "-fx-cursor: hand; -fx-border-color: transparent; " +
//                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
//                        ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
//                        st.setToX(1.0);
//                        st.setToY(1.0);
//                        st.play();
//                });
//
//                return btn;
//        }
//
//        private ImageView createMenuImage(String resourcePath, double width, double height) {
//                URL resource = getClass().getResource(resourcePath);
//                Image image;
//                if (resource != null) {
//                        image = new Image(resource.toExternalForm(), width, height, true, true);
//                } else {
//                        String fallbackSvg = """
//                                        <svg xmlns='http://www.w3.org/2000/svg' width='240' height='160' viewBox='0 0 240 160'>
//                                            <defs>
//                                                <linearGradient id='g' x1='0%' y1='0%' x2='100%' y2='100%'>
//                                                    <stop offset='0%' stop-color='#00A651'/>
//                                                    <stop offset='100%' stop-color='#006437'/>
//                                                </linearGradient>
//                                            </defs>
//                                            <rect x='12' y='12' width='216' height='136' rx='20' fill='url(#g)'/>
//                                            <rect x='12' y='68' width='216' height='18' fill='#ffffff' opacity='0.18'/>
//                                            <rect x='30' y='40' width='60' height='10' fill='#ffffff' opacity='0.6'/>
//                                            <text x='32' y='114' font-family='Arial, Helvetica, sans-serif' font-size='44' font-weight='700' fill='#ffffff'>PRESTO</text>
//                                        </svg>
//                                        """;
//                        String encoded = URLEncoder.encode(fallbackSvg, StandardCharsets.UTF_8)
//                                        .replace("+", "%20");
//                        image = new Image("data:image/svg+xml," + encoded, width, height, true, true);
//                }
//
//                ImageView imageView = new ImageView(image);
//                imageView.setFitWidth(width);
//                imageView.setFitHeight(height);
//                imageView.setPreserveRatio(true);
//                imageView.setSmooth(true);
//                imageView.setCache(true);
//                imageView.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.25)));
//                return imageView;
//        }
//
//        private Button createActionButton(String text, String color, boolean isPrimary) {
//                Button btn = new Button(text);
//                btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//                btn.setPrefSize(200, 60);
//
//                if (isPrimary) {
//                        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
//                                        "-fx-background-radius: 30; -fx-cursor: hand; " +
//                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
//                } else {
//                        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + color + "; " +
//                                        "-fx-border-color: " + color + "; -fx-border-width: 2; " +
//                                        "-fx-background-radius: 30; -fx-border-radius: 30; -fx-cursor: hand;");
//                }
//
//                btn.setOnMouseEntered(e -> {
//                        ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
//                        st.setToX(1.05);
//                        st.setToY(1.05);
//                        st.play();
//                });
//
//                btn.setOnMouseExited(e -> {
//                        ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
//                        st.setToX(1.0);
//                        st.setToY(1.0);
//                        st.play();
//                });
//
//                return btn;
//        }
//
//        private Button createQuickAmountButton(String amount) {
//                Button btn = new Button(amount);
//                btn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
//                btn.setPrefSize(80, 50);
//                btn.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: white; " +
//                                "-fx-background-radius: 8; -fx-cursor: hand;");
//
//                btn.setOnMouseEntered(e -> btn
//                                .setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; " +
//                                                "-fx-background-radius: 8; -fx-cursor: hand;"));
//
//                btn.setOnMouseExited(e -> btn
//                                .setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: white; " +
//                                                "-fx-background-radius: 8; -fx-cursor: hand;"));
//
//                return btn;
//        }
//
//        private <T> ComboBox<T> createStyledComboBox() {
//                ComboBox<T> combo = new ComboBox<>();
//                combo.getStyleClass().add("igo-combo");
//                combo.setButtonCell(createComboCell());
//                combo.setCellFactory(listView -> createComboCell());
//                combo.setVisibleRowCount(6);
//                return combo;
//        }
//
//        private <T> ListCell<T> createComboCell() {
//                ListCell<T> cell = new ListCell<>() {
//                        @Override
//                        protected void updateItem(T item, boolean empty) {
//                                super.updateItem(item, empty);
//                                if (empty || item == null) {
//                                        setText(null);
//                                } else {
//                                        setText(item.toString());
//                                }
//                                setFont(Font.font("Arial", FontWeight.MEDIUM, 16));
//                        }
//                };
//                cell.setPadding(new Insets(8, 16, 8, 16));
//                return cell;
//        }
//
//        private TextField createStyledTextField(String prompt) {
//                TextField field = new TextField();
//                field.setPromptText(prompt);
//                field.setFont(Font.font("Arial", 16));
//                field.setStyle("-fx-background-radius: 8; -fx-border-color: #ccc; " +
//                                "-fx-border-radius: 8; -fx-padding: 12; -fx-border-width: 2;");
//
//                field.focusedProperty().addListener((obs, oldVal, newVal) -> {
//                        if (newVal) {
//                                field.setStyle("-fx-background-radius: 8; -fx-border-color: " +
//                                                PRIMARY_COLOR
//                                                + "; -fx-border-radius: 8; -fx-padding: 12; -fx-border-width: 2;");
//                        } else {
//                                field.setStyle("-fx-background-radius: 8; -fx-border-color: #ccc; " +
//                                                "-fx-border-radius: 8; -fx-padding: 12; -fx-border-width: 2;");
//                        }
//                });
//
//                return field;
//        }
//
//        private Node buildReceiptView(String receipt) {
//                VBox wrapper = new VBox(18);
//                wrapper.setAlignment(Pos.TOP_LEFT);
//                wrapper.setFillWidth(true);
//
//                Label badge = new Label("PRESTO");
//                badge.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 14));
//                badge.setTextFill(Color.WHITE);
//                badge.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-background-radius: 12; " +
//                                "-fx-padding: 4 14; -fx-letter-spacing: 2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 4);");
//
//                String[] lines = receipt.split("\\r?\\n");
//                String defaultHeadline = currentLanguage == Language.FR ? "Billet PRESTO" : "PRESTO Ticket";
//                String headline = null;
//                String footerLine = null;
//                List<String[]> detailRows = new java.util.ArrayList<>();
//
//                for (String line : lines) {
//                        if (line == null) {
//                                continue;
//                        }
//                        String trimmed = line.trim();
//                        if (trimmed.isEmpty() || trimmed.startsWith("=")) {
//                                continue;
//                        }
//
//                        if (headline == null) {
//                                headline = trimmed;
//                                continue;
//                        }
//
//                        if (trimmed.startsWith("Thank") || trimmed.startsWith("Merci")) {
//                                footerLine = trimmed;
//                                continue;
//                        }
//
//                        if (line.length() >= 20) {
//                                String labelPart = line.substring(0, Math.min(20, line.length())).trim();
//                                String valuePart = line.length() > 20 ? line.substring(20).trim() : "";
//                                if (!labelPart.isEmpty()) {
//                                        if (!labelPart.endsWith(":")) {
//                                                labelPart += ":";
//                                        }
//                                        detailRows.add(new String[]{labelPart, valuePart});
//                                        continue;
//                                }
//                        }
//
//                        if (!trimmed.isEmpty()) {
//                                detailRows.add(new String[]{trimmed, ""});
//                        }
//                }
//
//                if (headline == null) {
//                        headline = defaultHeadline;
//                }
//
//                Label headlineLabel = new Label(headline);
//                headlineLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 22));
//                headlineLabel.setTextFill(Color.web(DARK_TEXT));
//                headlineLabel.setWrapText(true);
//
//                GridPane detailGrid = new GridPane();
//                detailGrid.setHgap(24);
//                detailGrid.setVgap(12);
//                ColumnConstraints col1 = new ColumnConstraints();
//                col1.setPercentWidth(40);
//                ColumnConstraints col2 = new ColumnConstraints();
//                col2.setPercentWidth(60);
//                detailGrid.getColumnConstraints().addAll(col1, col2);
//
//                int rowIndex = 0;
//                for (String[] row : detailRows) {
//                        Label labelCell = new Label(row[0]);
//                        labelCell.setFont(Font.font("Arial", FontWeight.BOLD, 16));
//                        labelCell.setTextFill(Color.web(DARK_TEXT));
//                        labelCell.setAlignment(Pos.CENTER_RIGHT);
//                        labelCell.setMaxWidth(Double.MAX_VALUE);
//
//                        Label valueCell = new Label(row[1]);
//                        valueCell.setFont(Font.font("Arial", FontWeight.MEDIUM, 16));
//                        valueCell.setTextFill(Color.web(DARK_TEXT));
//                        valueCell.setWrapText(true);
//
//                        detailGrid.add(labelCell, 0, rowIndex);
//                        detailGrid.add(valueCell, 1, rowIndex);
//                        rowIndex++;
//                }
//
//                wrapper.getChildren().addAll(badge, headlineLabel, detailGrid);
//
//                if (footerLine != null) {
//                        Separator separator = new Separator();
//                        Label footer = new Label(footerLine);
//                        footer.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
//                        footer.setTextFill(Color.web(PRIMARY_COLOR));
//                        footer.setTextAlignment(TextAlignment.CENTER);
//                        footer.setMaxWidth(Double.MAX_VALUE);
//                        footer.setAlignment(Pos.CENTER);
//                        wrapper.getChildren().addAll(separator, footer);
//                }
//
//                return wrapper;
//        }
//
//        // ========================================================================
//        // DIALOGS & OVERLAYS
//        // ========================================================================
//
//        private HBox createFormField(String labelText, String value) {
//                HBox box = new HBox(20);
//                box.setAlignment(Pos.CENTER_LEFT);
//
//                Label label = new Label(labelText);
//                label.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
//                label.setTextFill(Color.web(DARK_TEXT));
//                label.setMinWidth(165);
//                label.setAlignment(Pos.CENTER_RIGHT);
//
//                box.getChildren().add(label);
//                return box;
//        }
//
//        private void showProcessingOverlay(BorderPane layout, String message) {
//                StackPane overlay = new StackPane();
//                overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
//
//                VBox content = new VBox(20);
//                content.setAlignment(Pos.CENTER);
//
//                ProgressIndicator progress = new ProgressIndicator();
//                progress.setStyle("-fx-progress-color: " + ACCENT_COLOR + ";");
//                progress.setPrefSize(80, 80);
//
//                Label msgLabel = new Label(message);
//                msgLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//                msgLabel.setTextFill(Color.WHITE);
//
//                content.getChildren().addAll(progress, msgLabel);
//                overlay.getChildren().add(content);
//
//                layout.setCenter(overlay);
//        }
//
//    private void showStyledSuccess(String title, String receipt, BorderPane layout) {
//        Platform.runLater(() -> {
//            if (rootContainer == null) {
//                Alert fallback = new Alert(Alert.AlertType.INFORMATION);
//                fallback.setTitle(title);
//                fallback.setHeaderText(null);
//                fallback.setContentText(receipt);
//                fallback.showAndWait();
//                fadeTransition(layout, this::showMainMenu);
//                return;
//            }
//
//            StackPane overlay = new StackPane();
//            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.55);");
//            overlay.setOpacity(0);
//            overlay.setPickOnBounds(true);
//
//            VBox card = new VBox(18);
//            card.setAlignment(Pos.TOP_CENTER);
//            card.setPadding(new Insets(28));
//            card.setSpacing(18);
//            card.setMaxWidth(520);
//            card.setMaxHeight(Region.USE_PREF_SIZE);
//            card.setStyle("-fx-background-color: white; -fx-background-radius: 28; " +
//                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 40, 0, 0, 12);" +
//                    "-fx-border-radius: 28; -fx-border-width: 1; -fx-border-color: rgba(0,0,0,0.08);");
//
//            Label heading = new Label(title);
//            heading.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30));
//            heading.setTextFill(Color.web(DARK_TEXT));
//            heading.setAlignment(Pos.CENTER);
//            heading.setMaxWidth(Double.MAX_VALUE);
//
//            Label subheading = new Label(currentLanguage == Language.FR ?
//                    "Votre billet est prêt!" : "Your ticket is ready!");
//            subheading.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 18));
//            subheading.setTextFill(Color.web(PRIMARY_COLOR));
//            subheading.setAlignment(Pos.CENTER);
//            subheading.setMaxWidth(Double.MAX_VALUE);
//
//            Node receiptView = buildReceiptView(receipt);
//
//            ScrollPane receiptScroll = new ScrollPane(receiptView);
//            receiptScroll.setFitToWidth(true);
//            receiptScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//            receiptScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//            receiptScroll.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-background-insets: 0;" +
//                    "-fx-border-color: transparent;");
//            receiptScroll.setOnScroll(Event::consume);
//            if (receiptView instanceof Region regionContent) {
//                regionContent.setMinHeight(Region.USE_PREF_SIZE);
//                double contentHeight = regionContent.prefHeight(-1) + 24;
//                double viewportCap = 420;
//                double viewportHeight = Math.min(contentHeight, viewportCap);
//                receiptScroll.setPrefViewportHeight(viewportHeight);
//                receiptScroll.setMinViewportHeight(viewportHeight);
//                receiptScroll.setMaxHeight(viewportHeight + 12);
//                if (contentHeight > viewportCap) {
//                    receiptScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//                }
//            } else {
//                receiptScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//            }
//
//            Button printButton = new Button(currentLanguage == Language.FR ? "Imprimer" : "Print");
//            printButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//            printButton.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; " +
//                    "-fx-background-radius: 28; -fx-padding: 12 26; -fx-cursor: hand;" +
//                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 2);");
//
//            Button closeButton = new Button(currentLanguage == Language.FR ? "Fermer" : "Close");
//            closeButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//            closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + DARK_TEXT + "; " +
//                    "-fx-border-color: rgba(0,0,0,0.15); -fx-border-radius: 28; -fx-background-radius: 28;" +
//                    "-fx-padding: 12 26; -fx-cursor: hand;" +
//                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 1);");
//            closeButton.setDefaultButton(true);
//
//            HBox buttonBar = new HBox(24, printButton, closeButton);
//            buttonBar.setAlignment(Pos.CENTER);
//
//            card.getChildren().addAll(heading, subheading, receiptScroll, buttonBar);
//            card.setFillWidth(true);
//
//            overlay.getChildren().add(card);
//            StackPane.setAlignment(card, Pos.CENTER);
//
//            rootContainer.getChildren().add(overlay);
//
//            FadeTransition fadeIn = new FadeTransition(Duration.millis(220), overlay);
//            fadeIn.setFromValue(0);
//            fadeIn.setToValue(1);
//            fadeIn.play();
//
//            Runnable closeOverlay = () -> {
//                FadeTransition fadeOut = new FadeTransition(Duration.millis(220), overlay);
//                fadeOut.setFromValue(overlay.getOpacity());
//                fadeOut.setToValue(0);
//                fadeOut.setOnFinished(evt -> {
//                    rootContainer.getChildren().remove(overlay);
//                    fadeTransition(layout, this::showMainMenu);
//                });
//                fadeOut.play();
//            };
//
//            closeButton.setOnAction(e -> closeOverlay.run());
//
//            printButton.setOnAction(e -> {
//                printButton.setDisable(true);
//                String overlayTitle = currentLanguage == Language.FR ? "Impression" : "Printing";
//                String overlayMsg = currentLanguage == Language.FR ?
//                        "Votre reçu est envoyé à l'imprimante." :
//                        "Your receipt was sent to the printer.";
//                showOverlayMessage(overlayTitle, overlayMsg, Color.web(SUCCESS_COLOR),
//                        Duration.seconds(1.6), 460);
//
//                PauseTransition reset = new PauseTransition(Duration.seconds(1.2));
//                reset.setOnFinished(ev -> printButton.setDisable(false));
//                reset.play();
//            });
//
//            overlay.setOnMouseClicked(e -> {
//                if (e.getTarget() == overlay) {
//                    closeOverlay.run();
//                }
//            });
//        });
//    }
//
//        // ========================================================================
//        // ANIMATIONS
//        // ========================================================================
//
//    private void showStyledError(String message, BorderPane layout) {
//        showOverlayMessage(
//                currentLanguage == Language.FR ? "Erreur" : "Error",
//                message,
//                Color.web(ERROR_COLOR),
//                Duration.seconds(2.8),
//                440
//        );
//    }
//
//    private void showOverlayMessage(String titleText, String message, Color accentColor,
//                                    Duration duration, double maxWidth) {
//        Platform.runLater(() -> {
//            if (rootContainer == null) {
//                Alert fallback = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
//                fallback.setHeaderText(titleText);
//                fallback.show();
//                return;
//            }
//
//            StackPane overlay = new StackPane();
//            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
//            overlay.setOpacity(0);
//            overlay.setPickOnBounds(true);
//
//            VBox card = new VBox(16);
//            card.setAlignment(Pos.CENTER);
//            card.setPadding(new Insets(26));
//            card.setSpacing(12);
//            card.setMaxWidth(maxWidth);
//            card.setMaxHeight(Region.USE_PREF_SIZE);
//            card.setStyle("-fx-background-color: white; -fx-background-radius: 26; -fx-border-radius: 26; " +
//                    "-fx-border-width: 2; -fx-border-color: " + toHex(accentColor) + "; " +
//                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 26, 0, 0, 10);");
//
//            Label heading = new Label(titleText);
//            heading.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 24));
//            heading.setTextFill(accentColor);
//            heading.setWrapText(true);
//            heading.setTextAlignment(TextAlignment.CENTER);
//
//            Label detail = new Label(message);
//            detail.setFont(Font.font("Arial", FontWeight.MEDIUM, 17));
//            detail.setTextFill(Color.web(DARK_TEXT));
//            detail.setWrapText(true);
//            detail.setTextAlignment(TextAlignment.CENTER);
//
//            card.getChildren().addAll(heading, detail);
//
//            overlay.getChildren().add(card);
//            StackPane.setAlignment(card, Pos.CENTER);
//
//            rootContainer.getChildren().add(overlay);
//
//            FadeTransition fadeIn = new FadeTransition(Duration.millis(220), overlay);
//            fadeIn.setFromValue(0);
//            fadeIn.setToValue(1);
//            fadeIn.play();
//
//            Runnable closeOverlay = () -> {
//                if (!rootContainer.getChildren().contains(overlay)) {
//                    return;
//                }
//                FadeTransition fadeOut = new FadeTransition(Duration.millis(220), overlay);
//                fadeOut.setFromValue(overlay.getOpacity());
//                fadeOut.setToValue(0);
//                fadeOut.setOnFinished(evt -> rootContainer.getChildren().remove(overlay));
//                fadeOut.play();
//            };
//
//            PauseTransition hold = new PauseTransition(duration);
//            hold.setOnFinished(e -> closeOverlay.run());
//            hold.play();
//
//            overlay.setOnMouseClicked(e -> {
//                if (e.getTarget() == overlay) {
//                    closeOverlay.run();
//                }
//            });
//        });
//    }
//
//    private String toHex(Color color) {
//        int r = (int) Math.round(color.getRed() * 255);
//        int g = (int) Math.round(color.getGreen() * 255);
//        int b = (int) Math.round(color.getBlue() * 255);
//        return String.format("#%02X%02X%02X", r, g, b);
//    }
//
//        // ========================================================================
//        // STYLESHEET
//        // ========================================================================
//
//        private void fadeTransition(Region node, Runnable onFinish) {
//                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), node);
//                fadeOut.setFromValue(1.0);
//                fadeOut.setToValue(0.0);
//                fadeOut.setOnFinished(e -> {
//                        onFinish.run();
//                        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), mainContainer);
//                        fadeIn.setFromValue(0.0);
//                        fadeIn.setToValue(1.0);
//                        fadeIn.play();
//                });
//                fadeOut.play();
//        }
//
//        private String getStylesheet() {
//                return "data:text/css," + String.join(" ",
//                                ".button:hover { -fx-cursor: hand; }",
//                                ".text-field:focused { -fx-border-color: " + PRIMARY_COLOR + "; }",
//                                ".combo-box.igo-combo { -fx-font-size: 16px; -fx-pref-width: 350px; -fx-background-color: white; "
//                                                +
//                                                "-fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: transparent; "
//                                                +
//                                                "-fx-padding: 6 16 6 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0, 0, 2); }",
//                                ".combo-box.igo-combo:focused, .combo-box.igo-combo:showing { -fx-border-color: "
//                                                + PRIMARY_COLOR + "; " +
//                                                "-fx-border-width: 2; -fx-effect: dropshadow(gaussian, rgba(0,166,81,0.35), 18, 0, 0, 4); }",
//                                ".combo-box.igo-combo .list-cell { -fx-text-fill: " + DARK_TEXT
//                                                + "; -fx-background-color: transparent; }",
//                                ".combo-box.igo-combo .arrow-button { -fx-background-color: transparent; -fx-padding: 0 10 0 0; }",
//                                ".combo-box.igo-combo .arrow-button:hover { -fx-background-color: rgba(0,0,0,0.05); " +
//                                                "-fx-background-radius: 8; }",
//                                ".combo-box-popup .list-view { -fx-background-color: white; -fx-background-radius: 12; "
//                                                +
//                                                "-fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 16, 0, 0, 4); "
//                                                +
//                                                "-fx-padding: 4; }",
//                                ".combo-box-popup .list-view .list-cell { -fx-padding: 10 16; -fx-font-size: 15px; -fx-background-color: transparent; }",
//                                ".combo-box-popup .list-view .list-cell:filled:selected, " +
//                                                ".combo-box-popup .list-view .list-cell:filled:hover { -fx-background-color: "
//                                                + PRIMARY_COLOR + "; " +
//                                                "-fx-text-fill: white; }");
//        }
//
//}
