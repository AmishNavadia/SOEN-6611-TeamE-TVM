package ca.concordia.igo.ui.screens;

import ca.concordia.igo.IGoApplication;
import ca.concordia.igo.ui.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static ca.concordia.igo.ui.ThemeConstants.BACKGROUND_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.DARK_TEXT;
import static ca.concordia.igo.ui.ThemeConstants.ERROR_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.PRIMARY_COLOR;

/**
 * Authentication screen for maintenance access.
 * Requires PIN code verification before granting access to maintenance
 * dashboard.
 * Displays error messages for invalid authentication attempts.
 */
public class MaintenanceLoginScreen {

    private final IGoApplication app;

    public MaintenanceLoginScreen(IGoApplication app) {
        this.app = app;
    }
    
    /**
     * Displays the maintenance login screen.
     */
    public void show() {
        BorderPane layout = buildView();
        app.navigateTo(layout);
    }

    /**
     * Constructs the login form with PIN field and authentication.
     */
    private BorderPane buildView() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        layout.setTop(UIComponents.createHeader(app.t("maint.title")));

        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(60));

        Label lockIcon = new Label("[LOCK]");
        lockIcon.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 48));
        lockIcon.setTextFill(Color.web(PRIMARY_COLOR));

        Label titleLabel = new Label(app.t("maint.access"));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web(DARK_TEXT));

        // PIN input field
        PasswordField pinField = new PasswordField();
        pinField.setPromptText(app.t("maint.pin"));
        pinField.setMaxWidth(300);
        pinField.setFont(Font.font("Arial", 18));
        pinField.setStyle("-fx-background-radius: 8; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-padding: 12;");

        // Error message label
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web(ERROR_COLOR));
        errorLabel.setVisible(false);

        // Action buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button loginBtn = UIComponents.createActionButton(app.t("maint.login"), PRIMARY_COLOR, true);
        loginBtn.setOnAction(e -> onLogin(pinField.getText(), errorLabel, pinField, layout));

        Button backBtn = UIComponents.createActionButton(app.t("common.back"), ERROR_COLOR, false);
        backBtn.setOnAction(e -> app.fadeTransition(layout, app::showMainMenu));

        buttonBox.getChildren().addAll(loginBtn, backBtn);

        content.getChildren().addAll(lockIcon, titleLabel, pinField, errorLabel, buttonBox);
        layout.setCenter(content);
        layout.setBottom(app.createFooter());

        return layout;
    }

    /**
     * Validates PIN and grants access to maintenance dashboard on success.
     */
    private void onLogin(String pin,
                         Label errorLabel,
                         PasswordField pinField,
                         BorderPane layout) {
        if (pin == null || pin.isBlank()) {
            errorLabel.setText(app.t("maint.badpin"));
            errorLabel.setVisible(true);
            return;
        }
        if (app.getMaintenanceService().authenticate(pin)) {
            app.fadeTransition(layout, app::showMaintenanceDashboard);
        } else {
            errorLabel.setText(app.t("maint.badpin"));
            errorLabel.setVisible(true);
            pinField.clear();
        }
    }
}
