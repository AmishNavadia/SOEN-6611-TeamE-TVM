package ca.concordia.igo.ui;

import ca.concordia.igo.util.Language;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static ca.concordia.igo.ui.ThemeConstants.ACCENT_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.DARK_TEXT;
import static ca.concordia.igo.ui.ThemeConstants.PRIMARY_COLOR;
import static ca.concordia.igo.ui.ThemeConstants.SECONDARY_COLOR;

/**
 * Reusable UI component factory for consistent styling across screens.
 */
public final class UIComponents {

    private UIComponents() {
        // utility
    }

    /**
     * Creates the iGo logo box with title and subtitle.
     */
    public static VBox createLogoBox() {
        VBox logoBox = new VBox(10);
        logoBox.setAlignment(Pos.CENTER);

        Label logoLabel = new Label("iGo");
        logoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        logoLabel.setTextFill(Color.WHITE);
        logoLabel.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));

        Label subtitleLabel = new Label("PRESTO Ticket Vending Machine");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.LIGHT, 20));
        subtitleLabel.setTextFill(Color.WHITE);

        logoBox.getChildren().addAll(logoLabel, subtitleLabel);
        return logoBox;
    }

    /**
     * Creates a gradient header with title and timestamp.
     */
    public static VBox createHeader(String title) {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, " +
                SECONDARY_COLOR + ", " + PRIMARY_COLOR + "); -fx-padding: 20;");
        header.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);

        Label timeLabel = new Label(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy • HH:mm")));
        timeLabel.setFont(Font.font("Arial", FontWeight.LIGHT, 14));
        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setStyle("-fx-opacity: 0.9;");

        header.getChildren().addAll(titleLabel, timeLabel);
        return header;
    }

    /**
     * Creates a footer with help text and language switcher.
     */
    public static HBox createFooter(String helpText,
                                    Supplier<String> languageLabelSupplier,
                                    Runnable onLanguageButtonClicked) {
        HBox footer = new HBox(30);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: " + DARK_TEXT + ";");

        Label helpLabel = new Label(helpText);
        helpLabel.setFont(Font.font("Arial", 12));
        helpLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button languageButton = new Button(languageLabelSupplier.get());
        languageButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        languageButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ACCENT_COLOR + "; " +
                "-fx-border-color: " + ACCENT_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12; " +
                "-fx-padding: 6 14; -fx-cursor: hand;");
        languageButton.setOnMouseEntered(e -> languageButton.setStyle(
                "-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: " + DARK_TEXT + "; " +
                        "-fx-border-color: transparent; -fx-border-radius: 12; -fx-background-radius: 12; " +
                        "-fx-padding: 6 14; -fx-cursor: hand;"));
        languageButton.setOnMouseExited(e -> languageButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: " + ACCENT_COLOR + "; " +
                        "-fx-border-color: " + ACCENT_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12; " +
                        "-fx-padding: 6 14; -fx-cursor: hand;"));
        languageButton.setOnAction(e -> {
            onLanguageButtonClicked.run();
            languageButton.setText(languageLabelSupplier.get());
        });

        footer.getChildren().addAll(helpLabel, spacer, languageButton);
        return footer;
    }

    /**
     * Creates a menu button with icon and description text.
     */
    public static Button createLanguageButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        btn.setPrefSize(280, 100);
        btn.setStyle("-fx-background-color: white; -fx-text-fill: " + DARK_TEXT + "; " +
                "-fx-background-radius: 15; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: white; " +
                    "-fx-background-radius: 15; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 8);");
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: white; -fx-text-fill: " + DARK_TEXT + "; " +
                    "-fx-background-radius: 15; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        return btn;
    }

    public static Button createMenuButton(String title, String description, String emoji) {
        Label emojiLabel = new Label(emoji);
        emojiLabel.setFont(Font.font(48));
        emojiLabel.setTextFill(Color.web(DARK_TEXT));
        return createMenuButton(title, description, emojiLabel);
    }

    public static Button createMenuButton(String title, String description, Node iconNode) {
        VBox btnContent = new VBox(10);
        btnContent.setAlignment(Pos.CENTER_LEFT);
        btnContent.setPadding(new Insets(20));

        HBox titleBox = new HBox(18);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        if (iconNode instanceof Label label) {
            label.setFont(Font.font(48));
            label.setTextFill(Color.web(DARK_TEXT));
        } else if (iconNode instanceof ImageView imageView) {
            imageView.setFitWidth(72);
            imageView.setFitHeight(48);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
        }

        VBox textBox = new VBox(5);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web(DARK_TEXT));

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        descLabel.setTextFill(Color.web(DARK_TEXT));
        descLabel.setStyle("-fx-opacity: 0.7;");

        textBox.getChildren().addAll(titleLabel, descLabel);
        titleBox.getChildren().addAll(iconNode, textBox);
        btnContent.getChildren().add(titleBox);

        return buildMenuButton(btnContent);
    }

    /**
     * Creates an image view for menu icons with fallback SVG.
     */
    public static ImageView createMenuImage(String resourcePath, double width, double height) {
        URL resource = UIComponents.class.getResource(resourcePath);
        Image image;
        if (resource != null) {
            image = new Image(resource.toExternalForm(), width, height, true, true);
        } else {
            String fallbackSvg = """
                    <svg xmlns='http://www.w3.org/2000/svg' width='240' height='160' viewBox='0 0 240 160'>
                        <defs>
                            <linearGradient id='g' x1='0%' y1='0%' x2='100%' y2='100%'>
                                <stop offset='0%' stop-color='#00A651'/>
                                <stop offset='100%' stop-color='#006437'/>
                            </linearGradient>
                        </defs>
                        <rect x='12' y='12' width='216' height='136' rx='20' fill='url(#g)'/>
                        <rect x='12' y='68' width='216' height='18' fill='#ffffff' opacity='0.18'/>
                        <rect x='30' y='40' width='60' height='10' fill='#ffffff' opacity='0.6'/>
                        <text x='32' y='114' font-family='Arial, Helvetica, sans-serif' font-size='44' font-weight='700' fill='#ffffff'>PRESTO</text>
                    </svg>
                    """;
            String encoded = URLEncoder.encode(fallbackSvg, StandardCharsets.UTF_8).replace("+", "%20");
            image = new Image("data:image/svg+xml," + encoded, width, height, true, true);
        }

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.25)));
        return imageView;
    }

    /**
     * Creates a styled action button with hover effects.
     */
    public static Button createActionButton(String text, String color, boolean isPrimary) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        btn.setPrefSize(200, 60);

        if (isPrimary) {
            btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                    "-fx-background-radius: 30; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + color + "; " +
                    "-fx-border-color: " + color + "; -fx-border-width: 2; " +
                    "-fx-background-radius: 30; -fx-border-radius: 30; -fx-cursor: hand;");
        }

        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        return btn;
    }


    public static Button createQuickAmountButton(String amount) {
        Button btn = new Button(amount);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        btn.setPrefSize(80, 50);
        btn.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-cursor: hand;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + PRIMARY_COLOR +
                "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + SECONDARY_COLOR +
                "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        return btn;
    }

    public static <T> ComboBox<T> createStyledComboBox() {
        ComboBox<T> combo = new ComboBox<>();
        combo.getStyleClass().add("igo-combo");
        combo.setButtonCell(createComboCell());
        combo.setCellFactory(listView -> createComboCell());
        combo.setVisibleRowCount(6);
        return combo;
    }

    public static <T> ListCell<T> createComboCell() {
        ListCell<T> cell = new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                setFont(Font.font("Arial", FontWeight.MEDIUM, 16));
            }
        };
        cell.setPadding(new Insets(8, 16, 8, 16));
        return cell;
    }

    public static TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setFont(Font.font("Arial", 16));
        field.setStyle("-fx-background-radius: 8; -fx-border-color: #ccc; " +
                "-fx-border-radius: 8; -fx-padding: 12; -fx-border-width: 2;");

        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-background-radius: 8; -fx-border-color: " + PRIMARY_COLOR +
                        "; -fx-border-radius: 8; -fx-padding: 12; -fx-border-width: 2;");
            } else {
                field.setStyle("-fx-background-radius: 8; -fx-border-color: #ccc; " +
                        "-fx-border-radius: 8; -fx-padding: 12; -fx-border-width: 2;");
            }
        });

        return field;
    }

    /**
     * Builds a formatted receipt view from receipt text.
     */
    public static Node buildReceiptView(String receipt, Language language) {
        VBox wrapper = new VBox(18);
        wrapper.setPrefWidth(540);
        wrapper.setMaxWidth(Region.USE_PREF_SIZE);
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: #f3f3f3; -fx-background-radius: 18;");

        wrapper.setAlignment(Pos.TOP_LEFT);
        wrapper.setFillWidth(true);

        Label badge = new Label("PRESTO");
        badge.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 14));
        badge.setTextFill(Color.WHITE);
        badge.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-background-radius: 12; " +
                "-fx-padding: 4 14; -fx-letter-spacing: 2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 4);");

        String[] lines = receipt.split("\\r?\\n");
        String defaultHeadline = language == Language.FR ? "Billet PRESTO" : "PRESTO Ticket";
        String headline = null;
        String footerLine = null;
        List<String[]> detailRows = new ArrayList<>();

        for (String line : lines) {
            if (line == null) continue;
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("=")) continue;

            if (headline == null) {
                headline = trimmed;
                continue;
            }

            if (trimmed.startsWith("Thank") || trimmed.startsWith("Merci")) {
                footerLine = trimmed;
                continue;
            }

            if (line.length() >= 20) {
                String labelPart = line.substring(0, Math.min(20, line.length())).trim();
                String valuePart = line.length() > 20 ? line.substring(20).trim() : "";
                if (!labelPart.isEmpty()) {
                    if (!labelPart.endsWith(":")) labelPart += ":";
                    detailRows.add(new String[]{labelPart, valuePart});
                    continue;
                }
            }
            if (!trimmed.isEmpty()) detailRows.add(new String[]{trimmed, ""});
        }

        if (headline == null) headline = defaultHeadline;

        Label headlineLabel = new Label(headline);
        headlineLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 22));
        headlineLabel.setTextFill(Color.web(DARK_TEXT));
        headlineLabel.setWrapText(true);

        GridPane detailGrid = new GridPane();
        // detailGrid.setHgap(24);
        // detailGrid.setVgap(12);
        detailGrid.setHgap(80);
        detailGrid.setVgap(25);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(40);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(60);
        detailGrid.getColumnConstraints().addAll(col1, col2);

        int rowIndex = 0;
        for (String[] row : detailRows) {
            Label labelCell = new Label(row[0]);
            labelCell.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            labelCell.setTextFill(Color.web(DARK_TEXT));
            labelCell.setAlignment(Pos.CENTER_RIGHT);
            labelCell.setMaxWidth(Double.MAX_VALUE);

            Label valueCell = new Label(row[1]);
            valueCell.setFont(Font.font("Arial", FontWeight.MEDIUM, 16));
            valueCell.setTextFill(Color.web(DARK_TEXT));
            valueCell.setWrapText(true);

            detailGrid.add(labelCell, 0, rowIndex);
            detailGrid.add(valueCell, 1, rowIndex);
            rowIndex++;
        }

        wrapper.getChildren().addAll(badge, headlineLabel, detailGrid);

        if (footerLine != null) {
            javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
            Label footer = new Label(footerLine);
            footer.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
            footer.setTextFill(Color.web(PRIMARY_COLOR));
            footer.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            footer.setMaxWidth(Double.MAX_VALUE);
            footer.setAlignment(Pos.CENTER);
            wrapper.getChildren().addAll(separator, footer);
        }

        return wrapper;
    }

    public static HBox createFormField(String labelText, String value) {
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
        label.setTextFill(Color.web(DARK_TEXT));
        label.setMinWidth(165);
        label.setAlignment(Pos.CENTER_RIGHT);

        box.getChildren().add(label);

        if (value != null) {
            Label valueLabel = new Label(value);
            valueLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 16));
            valueLabel.setTextFill(Color.web(DARK_TEXT));
            box.getChildren().add(valueLabel);
        }

        return box;
    }

    @NotNull
    private static Button buildMenuButton(VBox btnContent) {
        Button btn = new Button();
        btn.setGraphic(btnContent);
        btn.setPrefSize(500, 110);
        btn.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-cursor: hand; -fx-border-color: transparent; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                    "-fx-cursor: hand; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-width: 3; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.02);
            st.setToY(1.02);
            st.play();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                    "-fx-cursor: hand; -fx-border-color: transparent; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
        return btn;
    }
}
