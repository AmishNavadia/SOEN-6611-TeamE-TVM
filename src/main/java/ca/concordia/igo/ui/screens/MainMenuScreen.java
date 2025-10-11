package ca.concordia.igo.ui.screens;

import ca.concordia.igo.IGoApplication;
import ca.concordia.igo.ui.UIComponents;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import static ca.concordia.igo.ui.ThemeConstants.BACKGROUND_COLOR;

/**
 * Main navigation menu displaying all available kiosk functions.
 * Provides access to ticket purchase, card recharge, balance check, and
 * maintenance.
 * Includes slide-in animation when screen loads.
 */
public class MainMenuScreen {

    private final IGoApplication app;

    public MainMenuScreen(IGoApplication app) {
        this.app = app;
    }

    /**
     * Displays the main menu with slide-in animation.
     */
    public void show() {
        BorderPane layout = buildView();
        app.navigateTo(layout);

        Node center = layout.getCenter();
        if (center != null) {
            TranslateTransition slide = new TranslateTransition(Duration.millis(400), center);
            slide.setFromX(100);
            slide.setToX(0);
            slide.play();
        }
    }

    /**
     * Constructs the menu layout with navigation buttons.
     */
    private BorderPane buildView() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        VBox header = UIComponents.createHeader(app.t("menu.title"));
        layout.setTop(header);

        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        // Buy ticket button
        Node ticketIcon = UIComponents.createMenuImage("/images/ticket.png", 96, 64);
        Button buyTicketBtn = UIComponents.createMenuButton(
                app.t("menu.buy"),
                app.t("menu.buy.desc"),
                ticketIcon
        );
        buyTicketBtn.setOnAction(e -> app.fadeTransition(layout, app::showBuyTicketScreen));

        // Recharge card button
        Node rechargeIcon = UIComponents.createMenuImage("/images/presto-card.png", 96, 64);
        Button rechargeBtn = UIComponents.createMenuButton(
                app.t("menu.recharge"),
                app.t("menu.recharge.desc"),
                rechargeIcon
        );
        rechargeBtn.setOnAction(e -> app.fadeTransition(layout, app::showRechargeScreen));

        // Check balance button
        Node balanceIcon = UIComponents.createMenuImage("/images/presto-card.png", 96, 64);
        Button checkBalanceBtn = UIComponents.createMenuButton(
                app.t("menu.balance"),
                app.t("menu.balance.desc"),
                balanceIcon
        );
        checkBalanceBtn.setOnAction(e -> app.fadeTransition(layout, app::showBalanceScreen));

        // Maintenance button
        Node toolIcon = UIComponents.createMenuImage("/images/tool.png", 96, 64);
        Button maintenanceBtn = UIComponents.createMenuButton(
                app.t("menu.maintenance"),
                app.t("menu.maintenance.desc"),
                toolIcon
        );
        maintenanceBtn.setOnAction(e -> app.fadeTransition(layout, app::showMaintenanceLoginScreen));

        content.getChildren().addAll(buyTicketBtn, rechargeBtn, checkBalanceBtn, maintenanceBtn);
        layout.setCenter(content);

        HBox footer = app.createFooter();
        layout.setBottom(footer);

        return layout;
    }
}
