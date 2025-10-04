package ca.concordia.igo;

import ca.concordia.igo.exception.*;
import ca.concordia.igo.model.*;
import ca.concordia.igo.service.*;
import ca.concordia.igo.util.Language;
import ca.concordia.igo.util.Logger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDateTime;

/**
 * Main JavaFX application for iGo TVM system.
 */
public class IGo extends Application {
    private Language currentLanguage = Language.EN;
    private final FareCalculator fareCalculator = new FareCalculator();
    private final PaymentService paymentService = new PaymentService();
    private final PrestoCardService prestoService = new PrestoCardService();
    private final TicketPrinter ticketPrinter = new TicketPrinter();

    private Stage primaryStage;
    private Scene homeScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("iGo - PRESTO Ticket Vending Machine");

        showHomeScreen();

        primaryStage.setScene(homeScene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    private void showHomeScreen() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("iGo - PRESTO TVM");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label subtitleLabel = new Label(currentLanguage == Language.FR ?
                "Sélectionnez votre langue / Select your language" :
                "Select your language / Sélectionnez votre langue");
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setStyle("-fx-text-fill: #ecf0f1;");

        HBox languageBox = new HBox(20);
        languageBox.setAlignment(Pos.CENTER);

        Button englishBtn = createButton("English", 200, 60);
        englishBtn.setOnAction(e -> {
            currentLanguage = Language.EN;
            showMainMenu();
        });

        Button frenchBtn = createButton("Français", 200, 60);
        frenchBtn.setOnAction(e -> {
            currentLanguage = Language.FR;
            showMainMenu();
        });

        languageBox.getChildren().addAll(englishBtn, frenchBtn);

        root.getChildren().addAll(titleLabel, subtitleLabel, languageBox);
        homeScene = new Scene(root);
    }

    private void showMainMenu() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #34495e;");

        Label titleLabel = new Label(currentLanguage == Language.FR ?
                "Menu Principal" : "Main Menu");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: white;");

        Button buyTicketBtn = createMenuButton(
                currentLanguage == Language.FR ? "Acheter un billet" : "Buy Ticket");
        buyTicketBtn.setOnAction(e -> showBuyTicketScreen());

        Button rechargeBtn = createMenuButton(
                currentLanguage == Language.FR ? "Recharger carte PRESTO" : "Recharge PRESTO Card");
        rechargeBtn.setOnAction(e -> showRechargeScreen());

        Button checkBalanceBtn = createMenuButton(
                currentLanguage == Language.FR ? "Vérifier le solde" : "Check Balance");
        checkBalanceBtn.setOnAction(e -> showCheckBalanceScreen());

        Button backBtn = createButton(
                currentLanguage == Language.FR ? "Retour" : "Back", 150, 40);
        backBtn.setOnAction(e -> primaryStage.setScene(homeScene));

        root.getChildren().addAll(
                titleLabel, buyTicketBtn, rechargeBtn, checkBalanceBtn, backBtn);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }

    private void showBuyTicketScreen() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #ecf0f1;");

        Label titleLabel = new Label(currentLanguage == Language.FR ?
                "Acheter un billet" : "Buy Ticket");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);

        Label originLabel = new Label(currentLanguage == Language.FR ?
                "Origine:" : "Origin:");
        ComboBox<Zone> originCombo = new ComboBox<>();
        originCombo.getItems().addAll(Zone.ZONE_1, Zone.ZONE_2, Zone.ZONE_3, Zone.ZONE_4);
        originCombo.setValue(Zone.ZONE_1);

        Label destLabel = new Label(currentLanguage == Language.FR ?
                "Destination:" : "Destination:");
        ComboBox<Zone> destCombo = new ComboBox<>();
        destCombo.getItems().addAll(Zone.ZONE_1, Zone.ZONE_2, Zone.ZONE_3, Zone.ZONE_4);
        destCombo.setValue(Zone.ZONE_3);

        Label tripLabel = new Label(currentLanguage == Language.FR ?
                "Type de trajet:" : "Trip Type:");
        ComboBox<TripType> tripCombo = new ComboBox<>();
        tripCombo.getItems().addAll(TripType.values());
        tripCombo.setValue(TripType.SINGLE);

        Label paymentLabel = new Label(currentLanguage == Language.FR ?
                "Mode de paiement:" : "Payment Method:");
        ComboBox<PaymentMethod> paymentCombo = new ComboBox<>();
        paymentCombo.getItems().addAll(PaymentMethod.values());
        paymentCombo.setValue(PaymentMethod.CREDIT_CARD);

        Label amountLabel = new Label(currentLanguage == Language.FR ?
                "Montant: $0.00" : "Amount: $0.00");
        amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Runnable updateAmount = () -> {
            try {
                Fare fare = new Fare(originCombo.getValue(),
                        destCombo.getValue(), tripCombo.getValue());
                double amount = fareCalculator.calculateAmount(fare);
                amountLabel.setText(String.format(
                        currentLanguage == Language.FR ? "Montant: $%.2f" : "Amount: $%.2f",
                        amount));
            } catch (Exception e) {
                Logger.error("Error calculating fare", e);
            }
        };

        originCombo.setOnAction(e -> updateAmount.run());
        destCombo.setOnAction(e -> updateAmount.run());
        tripCombo.setOnAction(e -> updateAmount.run());

        updateAmount.run();

        formGrid.add(originLabel, 0, 0);
        formGrid.add(originCombo, 1, 0);
        formGrid.add(destLabel, 0, 1);
        formGrid.add(destCombo, 1, 1);
        formGrid.add(tripLabel, 0, 2);
        formGrid.add(tripCombo, 1, 2);
        formGrid.add(paymentLabel, 0, 3);
        formGrid.add(paymentCombo, 1, 3);

        Button purchaseBtn = createButton(
                currentLanguage == Language.FR ? "Acheter" : "Purchase", 200, 50);
        purchaseBtn.setOnAction(e -> {
            try {
                Fare fare = new Fare(originCombo.getValue(),
                        destCombo.getValue(), tripCombo.getValue());
                double amount = fareCalculator.calculateAmount(fare);

                Transaction transaction = new Transaction(
                        TransactionType.TICKET_PURCHASE,
                        amount,
                        paymentCombo.getValue()
                );

                boolean success = paymentService.processPayment(transaction);

                if (success) {
                    Ticket ticket = new Ticket(fare, amount,
                            LocalDateTime.now().plusHours(2));
                    String receipt = ticketPrinter.printTicket(ticket, currentLanguage);

                    showSuccessDialog(
                            currentLanguage == Language.FR ?
                                    "Billet acheté avec succès!" :
                                    "Ticket purchased successfully!",
                            receipt);
                }
            } catch (PaymentFailedException ex) {
                showErrorDialog(ex.getUserMessage(currentLanguage));
            } catch (Exception ex) {
                Logger.error("Purchase failed", ex);
                showErrorDialog(currentLanguage == Language.FR ?
                        "Erreur lors de l'achat" : "Purchase error");
            }
        });

        Button cancelBtn = createButton(
                currentLanguage == Language.FR ? "Annuler" : "Cancel", 200, 50);
        cancelBtn.setOnAction(e -> showMainMenu());

        HBox buttonBox = new HBox(20, purchaseBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titleLabel, formGrid, amountLabel, buttonBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }

    private void showRechargeScreen() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ecf0f1;");

        Label titleLabel = new Label(currentLanguage == Language.FR ?
                "Recharger carte PRESTO" : "Recharge PRESTO Card");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Label instructionLabel = new Label(currentLanguage == Language.FR ?
                "Tapez votre carte PRESTO" : "Tap your PRESTO card");
        instructionLabel.setFont(Font.font("Arial", 16));

        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText(currentLanguage == Language.FR ?
                "Numéro de carte" : "Card number");
        cardNumberField.setMaxWidth(300);

        TextField amountField = new TextField();
        amountField.setPromptText(currentLanguage == Language.FR ?
                "Montant à recharger" : "Recharge amount");
        amountField.setMaxWidth(300);

        ComboBox<PaymentMethod> paymentCombo = new ComboBox<>();
        paymentCombo.getItems().addAll(PaymentMethod.values());
        paymentCombo.setValue(PaymentMethod.CREDIT_CARD);
        paymentCombo.setMaxWidth(300);

        Button rechargeBtn = createButton(
                currentLanguage == Language.FR ? "Recharger" : "Recharge", 200, 50);
        rechargeBtn.setOnAction(e -> {
            try {
                String cardNumber = cardNumberField.getText();
                double amount = Double.parseDouble(amountField.getText());

                PrestoCard card = prestoService.readCard(cardNumber);

                Transaction transaction = new Transaction(
                        TransactionType.CARD_RECHARGE,
                        amount,
                        paymentCombo.getValue()
                );

                boolean success = paymentService.processPayment(transaction);

                if (success) {
                    prestoService.rechargeCard(card, amount);
                    showSuccessDialog(
                            currentLanguage == Language.FR ?
                                    "Rechargement réussi!" : "Recharge successful!",
                            String.format(
                                    currentLanguage == Language.FR ?
                                            "Nouveau solde: $%.2f" : "New balance: $%.2f",
                                    card.getBalance()));
                }
            } catch (InvalidCardException ex) {
                showErrorDialog(ex.getUserMessage(currentLanguage));
            } catch (PaymentFailedException ex) {
                showErrorDialog(ex.getUserMessage(currentLanguage));
            } catch (NumberFormatException ex) {
                showErrorDialog(currentLanguage == Language.FR ?
                        "Montant invalide" : "Invalid amount");
            } catch (Exception ex) {
                Logger.error("Recharge failed", ex);
                showErrorDialog(currentLanguage == Language.FR ?
                        "Erreur de rechargement" : "Recharge error");
            }
        });

        Button backBtn = createButton(
                currentLanguage == Language.FR ? "Retour" : "Back", 200, 50);
        backBtn.setOnAction(e -> showMainMenu());

        HBox buttonBox = new HBox(20, rechargeBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
                titleLabel, instructionLabel, cardNumberField,
                amountField, paymentCombo, buttonBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }

    private void showCheckBalanceScreen() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ecf0f1;");

        Label titleLabel = new Label(currentLanguage == Language.FR ?
                "Vérifier le solde" : "Check Balance");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Label instructionLabel = new Label(currentLanguage == Language.FR ?
                "Tapez votre carte PRESTO" : "Tap your PRESTO card");
        instructionLabel.setFont(Font.font("Arial", 16));

        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText(currentLanguage == Language.FR ?
                "Numéro de carte" : "Card number");
        cardNumberField.setMaxWidth(300);

        Label balanceLabel = new Label("");
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        balanceLabel.setStyle("-fx-text-fill: #27ae60;");

        Button checkBtn = createButton(
                currentLanguage == Language.FR ? "Vérifier" : "Check", 200, 50);
        checkBtn.setOnAction(e -> {
            try {
                String cardNumber = cardNumberField.getText();
                PrestoCard card = prestoService.readCard(cardNumber);

                balanceLabel.setText(String.format(
                        currentLanguage == Language.FR ?
                                "Solde: $%.2f" : "Balance: $%.2f",
                        card.getBalance()));
            } catch (InvalidCardException ex) {
                showErrorDialog(ex.getUserMessage(currentLanguage));
                balanceLabel.setText("");
            } catch (Exception ex) {
                Logger.error("Balance check failed", ex);
                showErrorDialog(currentLanguage == Language.FR ?
                        "Erreur lors de la vérification" : "Check error");
                balanceLabel.setText("");
            }
        });

        Button backBtn = createButton(
                currentLanguage == Language.FR ? "Retour" : "Back", 200, 50);
        backBtn.setOnAction(e -> showMainMenu());

        HBox buttonBox = new HBox(20, checkBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
                titleLabel, instructionLabel, cardNumberField,
                balanceLabel, buttonBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }

    private Button createButton(String text, double width, double height) {
        Button button = new Button(text);
        button.setMinSize(width, height);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; " +
                        "-fx-background-radius: 10; -fx-cursor: hand;"));
        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                        "-fx-background-radius: 10; -fx-cursor: hand;"));
        return button;
    }

    private Button createMenuButton(String text) {
        return createButton(text, 400, 80);
    }

    private void showSuccessDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(currentLanguage == Language.FR ? "Erreur" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
