package ca.concordia.igo.ui;

import ca.concordia.igo.util.Language;
import ca.concordia.igo.ui.ThemeConstants;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static ca.concordia.igo.ui.ThemeConstants.DARK_TEXT;
import static ca.concordia.igo.ui.ThemeConstants.ERROR_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.PRIMARY_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.SUCCESS_COLOR;

/**
 * Manages overlay dialogs and user notifications for the iGo application.
 * Provides consistent styling for processing indicators, success messages, and
 * errors.
 */
public class DialogHelper {

    private final StackPane rootContainer;
    private final Supplier<Language> languageSupplier;
    private final Function<String, String> translator;
    private final BiConsumer<Region, Runnable> fadeTransition;

    public DialogHelper(StackPane rootContainer,
                        Supplier<Language> languageSupplier,
                        Function<String, String> translator,
                        BiConsumer<Region, Runnable> fadeTransition) {
        this.rootContainer = rootContainer;
        this.languageSupplier = languageSupplier;
        this.translator = translator;
        this.fadeTransition = fadeTransition;
    }

    /**
     * Displays a processing overlay with a spinner and message.
     */
    public void showProcessingOverlay(BorderPane layout, String message) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        ProgressIndicator progress = new ProgressIndicator();
        progress.setStyle("-fx-progress-color: " + ThemeConstants.ACCENT_COLOR + ";");
        progress.setPrefSize(80, 80);

        Label msgLabel = new Label(message);
        msgLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        msgLabel.setTextFill(Color.WHITE);

        content.getChildren().addAll(progress, msgLabel);
        overlay.getChildren().add(content);

        layout.setCenter(overlay);
    }

    /**
     * Displays a success dialog with a receipt and print option.
     */
    public void showStyledSuccess(String title, String receipt, BorderPane layout, Runnable onClose) {
        Platform.runLater(() -> {
            if (rootContainer == null) {
                Alert fallback = new Alert(Alert.AlertType.INFORMATION);
                fallback.setTitle(title);
                fallback.setHeaderText(null);
                fallback.setContentText(receipt);
                fallback.show();
                return;
            }

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.55);");
            overlay.setOpacity(0);
            overlay.setPickOnBounds(true);

            VBox card = new VBox(20);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(28));
            card.setMaxWidth(620);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 26; -fx-border-radius: 26; " +
                    "-fx-border-width: 2; -fx-border-color: " + PRIMARY_COLOR + "; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 40, 0, 0, 12);");

            Label heading = new Label(title);
            heading.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 28));
            heading.setTextFill(Color.web(PRIMARY_COLOR));
            heading.setAlignment(Pos.CENTER);
            heading.setMaxWidth(Double.MAX_VALUE);

            Language lang = languageSupplier.get();

            Label subheading = new Label(lang == Language.FR ? "Votre billet est prêt!" : "Your ticket is ready!");
            subheading.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 18));
            subheading.setTextFill(Color.web(PRIMARY_COLOR));
            subheading.setAlignment(Pos.CENTER);
            subheading.setMaxWidth(Double.MAX_VALUE);

            Node receiptView = UIComponents.buildReceiptView(receipt, lang);

            ScrollPane receiptScroll = new ScrollPane(receiptView);

            receiptScroll.setFitToWidth(true);
            receiptScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            receiptScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            receiptScroll.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-background-insets: 0;" +
                    "-fx-border-color: transparent;");
            receiptScroll.setOnScroll(Event::consume);

            if (receiptView instanceof Region regionContent) {
                regionContent.setMinHeight(Region.USE_PREF_SIZE);
                double contentHeight = regionContent.prefHeight(-1) + 24;
                double viewportCap = 420;
                double viewportHeight = Math.min(contentHeight, viewportCap);
                receiptScroll.setPrefViewportHeight(viewportHeight);
                receiptScroll.setMinViewportHeight(viewportHeight);
                receiptScroll.setMaxHeight(viewportHeight + 12);
                if (contentHeight > viewportCap) {
                    receiptScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                }
            } else {
                receiptScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            }

            Button printButton = new Button(lang == Language.FR ? "Imprimer" : "Print");
            printButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            printButton.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; " +
                    "-fx-background-radius: 28; -fx-padding: 12 26; -fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 2);");

            Button closeButton = new Button(lang == Language.FR ? "Fermer" : "Close");
            closeButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + DARK_TEXT + "; " +
                    "-fx-border-color: rgba(0,0,0,0.15); -fx-border-radius: 28; -fx-background-radius: 28;" +
                    "-fx-padding: 12 26; -fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 1);");
            closeButton.setDefaultButton(true);

            HBox buttonBar = new HBox(24, printButton, closeButton);
            buttonBar.setAlignment(Pos.CENTER);

            card.getChildren().addAll(heading, subheading, receiptScroll, buttonBar);
            card.setFillWidth(true);

            overlay.getChildren().add(card);
            StackPane.setAlignment(card, Pos.CENTER);

            rootContainer.getChildren().add(overlay);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(220), overlay);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            Runnable closeOverlay = () -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(220), overlay);
                fadeOut.setFromValue(overlay.getOpacity());
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(evt -> {
                    rootContainer.getChildren().remove(overlay);
                    if (fadeTransition != null && layout != null && onClose != null) {
                        fadeTransition.accept(layout, onClose);
                    }
                });
                fadeOut.play();
            };

            closeButton.setOnAction(e -> closeOverlay.run());

            printButton.setOnAction(e -> {
                printButton.setDisable(true);
                String overlayTitle = lang == Language.FR ? "Impression" : "Printing";
                String overlayMsg = lang == Language.FR
                        ? "Votre reçu est envoyé à l'imprimante."
                        : "Your receipt was sent to the printer.";
                showOverlayMessage(overlayTitle, overlayMsg, Color.web(SUCCESS_COLOR),
                        Duration.seconds(1.6), 460);

                PauseTransition reset = new PauseTransition(Duration.seconds(1.2));
                reset.setOnFinished(ev -> printButton.setDisable(false));
                reset.play();
            });

            overlay.setOnMouseClicked(e -> {
                if (e.getTarget() == overlay) {
                    closeOverlay.run();
                }
            });
        });
    }

    /**
     * Displays an error message overlay.
     */
    public void showStyledError(String message) {
        showOverlayMessage(
                translator.apply("error.title"),
                message,
                Color.web(ERROR_COLOR),
                Duration.seconds(2.8),
                440
        );
    }

    /**
     * Displays a temporary message overlay with custom styling and duration.
     */
    public void showOverlayMessage(String titleText, String message, Color accentColor,
                                   Duration duration, double maxWidth) {
        Platform.runLater(() -> {
            if (rootContainer == null) {
                Alert fallback = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
                fallback.setHeaderText(titleText);
                fallback.show();
                return;
            }

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
            overlay.setOpacity(0);
            overlay.setPickOnBounds(true);

            VBox card = new VBox(16);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(26));
            card.setSpacing(12);
            card.setMaxWidth(maxWidth);
            card.setMaxHeight(Region.USE_PREF_SIZE);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 26; -fx-border-radius: 26; " +
                    "-fx-border-width: 2; -fx-border-color: " + toHex(accentColor) + "; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 26, 0, 0, 10);");

            Label heading = new Label(titleText);
            heading.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 24));
            heading.setTextFill(accentColor);
            heading.setWrapText(true);
            heading.setTextAlignment(TextAlignment.CENTER);

            Label detail = new Label(message);
            detail.setFont(Font.font("Arial", FontWeight.MEDIUM, 17));
            detail.setTextFill(Color.web(DARK_TEXT));
            detail.setWrapText(true);
            detail.setTextAlignment(TextAlignment.CENTER);

            card.getChildren().addAll(heading, detail);

            overlay.getChildren().add(card);
            StackPane.setAlignment(card, Pos.CENTER);

            rootContainer.getChildren().add(overlay);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(220), overlay);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            Runnable closeOverlay = () -> {
                if (!rootContainer.getChildren().contains(overlay)) return;
                FadeTransition fadeOut = new FadeTransition(Duration.millis(220), overlay);
                fadeOut.setFromValue(overlay.getOpacity());
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(evt -> rootContainer.getChildren().remove(overlay));
                fadeOut.play();
            };

            PauseTransition hold = new PauseTransition(duration);
            hold.setOnFinished(e -> closeOverlay.run());
            hold.play();

            overlay.setOnMouseClicked(e -> {
                if (e.getTarget() == overlay) {
                    closeOverlay.run();
                }
            });
        });
    }

    private String toHex(Color color) {
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
