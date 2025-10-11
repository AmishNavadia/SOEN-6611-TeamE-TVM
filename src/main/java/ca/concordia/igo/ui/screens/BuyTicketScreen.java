package ca.concordia.igo.ui.screens;

import ca.concordia.igo.IGoApplication;
import ca.concordia.igo.exception.PaymentFailedException;
import ca.concordia.igo.model.Fare;
import ca.concordia.igo.model.PaymentMethod;
import ca.concordia.igo.model.Ticket;
import ca.concordia.igo.model.Transaction;
import ca.concordia.igo.model.TransactionType;
import ca.concordia.igo.model.TripType;
import ca.concordia.igo.model.Zone;
import ca.concordia.igo.ui.UIComponents;
import ca.concordia.igo.util.Language;
import ca.concordia.igo.util.Logger;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.time.LocalDateTime;

import static ca.concordia.igo.ui.ThemeConstants.BACKGROUND_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.DARK_TEXT;
import static ca.concordia.igo.ui.ThemeConstants.ERROR_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.PRIMARY_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.SUCCESS_COLOR;

/**
 * Ticket purchase screen with fare selection and payment processing.
 * Handles zone selection, trip type, payment method, and transaction
 * completion.
 * Displays dynamic fare calculation and prints receipt on successful purchase.
 */
public class BuyTicketScreen {

    private final IGoApplication app;

    public BuyTicketScreen(IGoApplication app) {
        this.app = app;
    }
    
    /**
     * Displays the ticket purchase screen with navigation.
     */
    public void show() {
        BorderPane layout = buildView();
        app.navigateTo(layout);
    }
    
    /**
     * Constructs the complete UI with fare selection and payment options.
     */
    private BorderPane buildView() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        Language language = app.getCurrentLanguage();
        String title = language == Language.FR ? "Acheter un Billet" : "Buy Ticket";
        layout.setTop(UIComponents.createHeader(title));

        app.startTimedSession(layout);

        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30));

        Label instructionLabel = new Label(
                language == Language.FR
                        ? "Sélectionnez votre trajet et mode de paiement"
                        : "Select your trip and payment method"
        );
        instructionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        instructionLabel.setTextFill(Color.web(DARK_TEXT));

        // Fare selection form
        VBox formBox = new VBox(15);
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        formBox.setPadding(new Insets(25));
        formBox.setMaxWidth(600);

        // Origin zone selector
        ComboBox<Zone> originCombo = UIComponents.createStyledComboBox();
        originCombo.getItems().addAll(Zone.ZONE_1, Zone.ZONE_2, Zone.ZONE_3, Zone.ZONE_4);
        originCombo.setValue(Zone.ZONE_1);
        formBox.getChildren().add(buildComboField(
                language == Language.FR ? "Origine :" : "Origin:",
                originCombo
        ));

        // Destination zone selector
        ComboBox<Zone> destCombo = UIComponents.createStyledComboBox();
        destCombo.getItems().addAll(Zone.ZONE_1, Zone.ZONE_2, Zone.ZONE_3, Zone.ZONE_4);
        destCombo.setValue(Zone.ZONE_3);
        formBox.getChildren().add(buildComboField(
                language == Language.FR ? "Destination :" : "Destination:",
                destCombo
        ));

        // Trip type selector
        ComboBox<TripType> tripCombo = UIComponents.createStyledComboBox();
        tripCombo.getItems().addAll(TripType.values());
        tripCombo.setValue(TripType.SINGLE);
        formBox.getChildren().add(buildComboField(
                language == Language.FR ? "Type de Trajet :" : "Trip Type:",
                tripCombo
        ));

        // Payment method selector
        ComboBox<PaymentMethod> paymentCombo = UIComponents.createStyledComboBox();
        paymentCombo.getItems().addAll(PaymentMethod.values());
        paymentCombo.setValue(PaymentMethod.CREDIT_CARD);
        formBox.getChildren().add(buildComboField(
                language == Language.FR ? "Paiement :" : "Payment:",
                paymentCombo
        ));

        // Dynamic fare display
        Label amountLabel = new Label("$0.00");
        amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        amountLabel.setTextFill(Color.web(PRIMARY_COLOR));
        amountLabel.setStyle("-fx-background-color: white; -fx-padding: 20; " +
                "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // Update fare when selections change
        Runnable updateAmount = () -> updateAmount(originCombo, destCombo, tripCombo, amountLabel);

        originCombo.setOnAction(e -> onComboChanged(updateAmount));
        destCombo.setOnAction(e -> onComboChanged(updateAmount));
        tripCombo.setOnAction(e -> onComboChanged(updateAmount));
        paymentCombo.setOnAction(e -> app.getSessionManager().resetTimer());

        updateAmount.run();

        // Action buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button purchaseBtn = UIComponents.createActionButton(
                language == Language.FR ? "Acheter" : "Purchase",
                SUCCESS_COLOR,
                true
        );
        purchaseBtn.setOnAction(e -> handlePurchase(
                originCombo.getValue(),
                destCombo.getValue(),
                tripCombo.getValue(),
                paymentCombo.getValue(),
                layout
        ));

        Button cancelBtn = UIComponents.createActionButton(
                language == Language.FR ? "Annuler" : "Cancel",
                ERROR_COLOR,
                false
        );
        cancelBtn.setOnAction(e -> {
            app.getSessionManager().endSession();
            app.fadeTransition(layout, app::showMainMenu);
        });

        buttonBox.getChildren().addAll(purchaseBtn, cancelBtn);

        content.getChildren().addAll(instructionLabel, formBox, amountLabel, buttonBox);
        layout.setCenter(content);
        layout.setBottom(app.createFooter());

        return layout;
    }

    /**
     * Creates a form field with label and combo box.
     */
    private <T> HBox buildComboField(String label, ComboBox<T> comboBox) {
        HBox field = UIComponents.createFormField(label, null);
        field.getChildren().add(comboBox);
        return field;
    }

    /**
     * Resets session timer on user interaction.
     */
    private void onComboChanged(Runnable updateAmount) {
        app.getSessionManager().resetTimer();
        updateAmount.run();
    }

    /**
     * Calculates and displays fare with animation when selections change.
     */
    private void updateAmount(ComboBox<Zone> originCombo,
                              ComboBox<Zone> destCombo,
                              ComboBox<TripType> tripCombo,
                              Label amountLabel) {
        try {
            Fare fare = new Fare(originCombo.getValue(), destCombo.getValue(), tripCombo.getValue());
            double amount = app.getFareCalculator().calculateAmount(fare);
            amountLabel.setText(String.format("$%.2f", amount));

            ScaleTransition pulse = new ScaleTransition(Duration.millis(200), amountLabel);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.1);
            pulse.setToY(1.1);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(2);
            pulse.play();
        } catch (Exception ex) {
            Logger.error("Error calculating fare", ex);
        }
    }

    /**
     * Processes ticket purchase with payment validation and receipt printing.
     */
    private void handlePurchase(Zone origin,
                                Zone dest,
                                TripType tripType,
                                PaymentMethod paymentMethod,
                                BorderPane layout) {
        app.getSessionManager().resetTimer();
        try {
            Fare fare = new Fare(origin, dest, tripType);
            double amount = app.getFareCalculator().calculateAmount(fare);

            Transaction transaction = new Transaction(
                    TransactionType.TICKET_PURCHASE,
                    amount,
                    paymentMethod
            );

            app.showProcessingOverlay(layout, app.t("purchase.processing"));

            // Simulate payment processing delay
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(evt -> {
                try {
                    boolean success = app.getPaymentService().processPayment(transaction);
                    if (success) {
                        LocalDateTime validUntil = LocalDateTime.now().plus(tripType.getValidityDuration());
                        Ticket ticket = new Ticket(fare, amount, validUntil);

                        app.getTransactionRepository().log(transaction);
                        String receipt = app.getTicketPrinter().printTicket(ticket, app.getCurrentLanguage());

                        app.showStyledSuccess(app.t("purchase.success"), receipt, layout);
                    }
                } catch (PaymentFailedException ex) {
                    app.showStyledError(ex.getUserMessage(app.getCurrentLanguage()));
                } catch (Exception ex) {
                    Logger.error("Purchase failed", ex);
                    app.showStyledError(app.t("purchase.error"));
                }
            });
            pause.play();

        } catch (Exception ex) {
            Logger.error("Purchase initialization failed", ex);
            app.showStyledError(app.t("purchase.error"));
        }
    }
}
