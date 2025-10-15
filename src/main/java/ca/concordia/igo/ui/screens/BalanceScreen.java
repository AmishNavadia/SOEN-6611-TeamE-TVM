package ca.concordia.igo.ui.screens;

import ca.concordia.igo.IGoApplication;
import ca.concordia.igo.exception.InvalidCardException;
import ca.concordia.igo.model.PrestoCard;
import ca.concordia.igo.ui.UIComponents;
import ca.concordia.igo.util.Logger;
import ca.concordia.igo.util.TokenUtil;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
 * Balance inquiry screen for checking PRESTO card balance.
 * Allows users to tap their card and view the current balance with validation.
 * Includes session timeout management and error handling.
 */
public class BalanceScreen {

    private final IGoApplication app;

    public BalanceScreen(IGoApplication app) {
        this.app = app;
    }

    /**
     * Displays the balance check screen with navigation.
     */
    public void show() {
        BorderPane layout = buildView();
        app.navigateTo(layout);
    }
    
    /**
     * Constructs the complete UI layout with card input and balance display.
     */
    private BorderPane buildView() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        layout.setTop(UIComponents.createHeader(app.t("balance.title")));

        // Start session timeout timer
        app.startTimedSession(layout);

        VBox content = new VBox(40);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(60));

        // Card input section
        VBox tapBox = new VBox(16);
        tapBox.setAlignment(Pos.CENTER);
        tapBox.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                "-fx-padding: 32; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 20, 0, 0, 4);");
        tapBox.setMaxWidth(600);

        ImageView tapIcon = UIComponents.createMenuImage("/images/presto-card.png", 180, 110);
        tapIcon.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.25)));

        Label tapLabel = new Label(app.t("balance.tap"));
        tapLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        tapLabel.setTextFill(Color.web(DARK_TEXT));

        TextField cardField = UIComponents.createStyledTextField(app.t("balance.cardNumber"));
        cardField.setMaxWidth(400);
        cardField.setOnKeyTyped(e -> app.getSessionManager().resetTimer());

        // Balance display label (hidden initially)
        Label balanceLabel = new Label();
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        balanceLabel.setTextFill(Color.web(SUCCESS_COLOR));
        balanceLabel.setVisible(false);

        tapBox.getChildren().addAll(tapIcon, tapLabel, cardField, balanceLabel);

        // Action buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button checkBtn = UIComponents.createActionButton(app.t("balance.check"), PRIMARY_COLOR, true);
        checkBtn.setOnAction(e -> onCheckBalance(cardField, balanceLabel, layout));

        Button backBtn = UIComponents.createActionButton(app.t("common.back"), ERROR_COLOR, false);
        backBtn.setOnAction(e -> {
            app.getSessionManager().endSession();
            app.fadeTransition(layout, app::showMainMenu);
        });

        buttonBox.getChildren().addAll(checkBtn, backBtn);

        content.getChildren().addAll(tapBox, buttonBox);
        layout.setCenter(content);
        layout.setBottom(app.createFooter());

        return layout;
    }

    /**
     * Handles balance check request with card validation and animated display.
     */
    private void onCheckBalance(TextField cardField, Label balanceLabel, BorderPane layout) {
        app.getSessionManager().resetTimer();
        try {
            String cardNumber = cardField.getText().trim();
            PrestoCard card = app.getPrestoService().readCard(cardNumber);

            // LOG: Mask card number in logs for security
            Logger.info("Balance check completed for card: " +
                    TokenUtil.maskCardNumber(cardNumber));

            // Display balance with masked card number
            balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            balanceLabel.setText(String.format("Card: %s\nBalance: $%.2f",
                    card.getMaskedCardNumberFormatted(),
                    card.getBalance()));
            balanceLabel.setVisible(true);
//            // Display balance with animation
//            balanceLabel.setText(String.format("$%.2f", card.getBalance()));
//            balanceLabel.setVisible(true);

            ScaleTransition scale = new ScaleTransition(Duration.millis(300), balanceLabel);
            scale.setFromX(0.5);
            scale.setFromY(0.5);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();

        } catch (InvalidCardException ex) {
            balanceLabel.setVisible(false);
            app.showStyledError(ex.getUserMessage(app.getCurrentLanguage()));
        } catch (Exception ex) {
            Logger.error("Balance check failed", ex);
            balanceLabel.setVisible(false);
            app.showStyledError(app.t("balance.error"));
        }
    }
}
