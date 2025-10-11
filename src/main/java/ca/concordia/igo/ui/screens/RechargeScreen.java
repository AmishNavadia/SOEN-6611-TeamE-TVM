package ca.concordia.igo.ui.screens;

import ca.concordia.igo.IGoApplication;
import ca.concordia.igo.exception.InvalidCardException;
import ca.concordia.igo.exception.PaymentFailedException;
import ca.concordia.igo.model.PaymentMethod;
import ca.concordia.igo.model.PrestoCard;
import ca.concordia.igo.model.Transaction;
import ca.concordia.igo.model.TransactionType;
import ca.concordia.igo.ui.UIComponents;
import ca.concordia.igo.util.Logger;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import static ca.concordia.igo.ui.ThemeConstants.BACKGROUND_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.DARK_TEXT;
import static ca.concordia.igo.ui.ThemeConstants.ERROR_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.PRIMARY_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.SUCCESS_COLOR;

/**
 * PRESTO card recharge screen with quick amount selection.
 * Allows users to add funds to their card using various payment methods.
 * Includes preset amount buttons and validation for card and payment.
 */
public class RechargeScreen {

    private final IGoApplication app;

    public RechargeScreen(IGoApplication app) {
        this.app = app;
    }

    /**
     * Displays the recharge screen with navigation.
     */
    public void show() {
        BorderPane layout = buildView();
        app.navigateTo(layout);
    }


    /**
     * Constructs the complete UI with card input and amount selection.
     */
    private BorderPane buildView() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        layout.setTop(UIComponents.createHeader(app.t("recharge.title")));

        app.startTimedSession(layout);

        VBox content = new VBox(24);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(32));
        
        // Card input section
        VBox tapBox = new VBox(12);
        tapBox.setAlignment(Pos.CENTER);
        tapBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-padding: 28; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 3);");
        tapBox.setMaxWidth(500);

        ImageView tapIcon = UIComponents.createMenuImage("/images/presto-card.png", 180, 110);
        tapIcon.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.25)));

        Label tapLabel = new Label(app.t("recharge.tapCard"));
        tapLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        tapLabel.setTextFill(Color.web(DARK_TEXT));

        TextField cardField = UIComponents.createStyledTextField(app.t("recharge.cardNumber"));
        cardField.setMaxWidth(350);
        cardField.setOnKeyTyped(e -> app.getSessionManager().resetTimer());

        tapBox.getChildren().addAll(tapIcon, tapLabel, cardField);
        
        // Amount selection section
        VBox amountBox = new VBox(12);
        amountBox.setAlignment(Pos.CENTER);
        amountBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-padding: 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        amountBox.setMaxWidth(500);

        Label amountLabel = new Label(app.t("recharge.amount"));
        amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        amountLabel.setTextFill(Color.web(DARK_TEXT));

        // Quick amount buttons
        HBox quickAmounts = new HBox(15);
        quickAmounts.setAlignment(Pos.CENTER);

        TextField amountField = UIComponents.createStyledTextField("$");
        amountField.setMaxWidth(200);
        amountField.setOnKeyTyped(e -> app.getSessionManager().resetTimer());

        for (double amount : new double[]{10, 20, 50, 100}) {
            Button quickBtn = UIComponents.createQuickAmountButton(String.format("$%.0f", amount));
            quickBtn.setOnAction(e -> {
                app.getSessionManager().resetTimer();
                amountField.setText(String.format("%.2f", amount));
            });
            quickAmounts.getChildren().add(quickBtn);
        }

        // Payment method selector
        ComboBox<PaymentMethod> paymentCombo = UIComponents.createStyledComboBox();
        paymentCombo.getItems().addAll(PaymentMethod.values());
        paymentCombo.setValue(PaymentMethod.CREDIT_CARD);
        paymentCombo.setMaxWidth(350);
        paymentCombo.setOnAction(e -> app.getSessionManager().resetTimer());

        amountBox.getChildren().addAll(amountLabel, quickAmounts, amountField, paymentCombo);

        // Action buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button rechargeBtn = UIComponents.createActionButton(app.t("recharge.recharge"), SUCCESS_COLOR, true);
        rechargeBtn.setOnAction(e -> handleRecharge(
                cardField.getText(),
                amountField.getText(),
                paymentCombo.getValue(),
                layout
        ));

        Button backBtn = UIComponents.createActionButton(app.t("common.back"), ERROR_COLOR, false);
        backBtn.setOnAction(e -> {
            app.getSessionManager().endSession();
            app.fadeTransition(layout, app::showMainMenu);
        });

        buttonBox.getChildren().addAll(rechargeBtn, backBtn);

        content.getChildren().addAll(tapBox, amountBox, buttonBox);
        layout.setCenter(content);
        layout.setBottom(app.createFooter());

        return layout;
    }

    /**
     * Processes recharge request with validation and payment processing.
     */
    private void handleRecharge(String cardNumber,
                                String amountText,
                                PaymentMethod paymentMethod,
                                BorderPane layout) {
        app.getSessionManager().resetTimer();
        
        // Validate card number
        String cardNo = cardNumber == null ? "" : cardNumber.trim();
        if (cardNo.isEmpty()) {
            app.showStyledError(app.t("recharge.invalidCard"));
            return;
        }

        // Validate and parse amount
        Double amount = app.parseAmount(amountText);
        if (amount == null || amount <= 0.0) {
            app.showStyledError(app.t("recharge.invalidAmount"));
            return;
        }

        try {
            PrestoCard card = app.getPrestoService().readCard(cardNo);

            Transaction transaction = new Transaction(
                    TransactionType.CARD_RECHARGE,
                    amount,
                    paymentMethod
            );

            app.showProcessingOverlay(layout, app.t("recharge.processing"));

            // Simulate payment processing delay
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(evt -> {
                try {
                    boolean success = app.getPaymentService().processPayment(transaction);
                    if (success) {
                        app.getPrestoService().rechargeCard(card, amount);
                        app.getTransactionRepository().log(transaction);

                        app.showStyledSuccess(
                                app.t("recharge.success"),
                                String.format(app.t("recharge.newBalance"), card.getBalance()),
                                layout
                        );
                    }
                } catch (PaymentFailedException ex) {
                    app.showStyledError(ex.getUserMessage(app.getCurrentLanguage()));
                } catch (Exception ex) {
                    Logger.error("Recharge failed", ex);
                    app.showStyledError(app.t("recharge.error"));
                }
            });
            pause.play();

        } catch (InvalidCardException ex) {
            app.showStyledError(ex.getUserMessage(app.getCurrentLanguage()));
        } catch (Exception ex) {
            Logger.error("Recharge init failed", ex);
            app.showStyledError(app.t("recharge.error"));
        }
    }
}
