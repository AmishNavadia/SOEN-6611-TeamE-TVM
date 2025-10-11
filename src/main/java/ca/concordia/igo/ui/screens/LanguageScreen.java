package ca.concordia.igo.ui.screens;

import ca.concordia.igo.IGoApplication;
import ca.concordia.igo.ui.UIComponents;
import ca.concordia.igo.util.Language;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import static ca.concordia.igo.ui.ThemeConstants.PRIMARY_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.SECONDARY_COLOR;

/**
 * Initial language selection screen for English or French.
 * First screen shown to users when starting the kiosk application.
 * Includes fade-in animation for professional appearance.
 */
public class LanguageScreen {

    private final IGoApplication app;

    public LanguageScreen(IGoApplication app) {
        this.app = app;
    }
    
    /**
     * Displays the language selection screen with fade-in animation.
     */
    public void show() {
        VBox content = buildView();
        app.navigateTo(content);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), content);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    /**
     * Constructs the bilingual welcome screen with language buttons.
     */
    private VBox buildView() {
        VBox content = new VBox(28);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(36));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                SECONDARY_COLOR + ", " + PRIMARY_COLOR + ");");

        VBox logoBox = UIComponents.createLogoBox();

        Label welcomeLabel = new Label(app.t("welcome.title"));
        welcomeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 28));
        welcomeLabel.setTextFill(Color.WHITE);
        welcomeLabel.setStyle("-fx-opacity: 0.9;");

        // Language selection buttons
        HBox languageButtons = new HBox(30);
        languageButtons.setAlignment(Pos.CENTER);

        Button englishBtn = UIComponents.createLanguageButton(app.t("lang.english"));
        englishBtn.setOnAction(e -> {
            app.setCurrentLanguage(Language.EN);
            app.fadeTransition(content, app::showMainMenu);
        });

        Button frenchBtn = UIComponents.createLanguageButton(app.t("lang.french"));
        frenchBtn.setOnAction(e -> {
            app.setCurrentLanguage(Language.FR);
            app.fadeTransition(content, app::showMainMenu);
        });

        languageButtons.getChildren().addAll(englishBtn, frenchBtn);

        Label instructionLabel = new Label(app.t("welcome.instruction"));
        instructionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        instructionLabel.setTextFill(Color.WHITE);
        instructionLabel.setStyle("-fx-text-alignment: center;");
        instructionLabel.setWrapText(true);

        content.getChildren().addAll(logoBox, welcomeLabel, languageButtons, instructionLabel);
        return content;
    }
}
