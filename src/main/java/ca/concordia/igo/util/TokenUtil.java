package ca.concordia.igo.util;

/**
 * Utility for masking-sensitive card information (PCI-DSS compliance).
 * Masks Primary Account Numbers (PAN) to protect cardholder data.
 */
public class TokenUtil {

    private static final char MASK_CHAR = '*';
    private static final int VISIBLE_DIGITS = 4; // Last 4 digits visible

    /**
     * Masks a card number, showing only the last 4 digits.
     * Example: "1234567890" -> "******7890"
     *
     * @param cardNumber the full card number to mask
     * @return masked card number with only last 4 digits visible
     */
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "";
        }

        Logger.debug("Masking card number for display");

        int length = cardNumber.length();
        if (length <= VISIBLE_DIGITS) {
            // If the card number is too short, mask it all
            return String.valueOf(MASK_CHAR).repeat(length);
        }

        // Show only the last 4 digits
        String masked = String.valueOf(MASK_CHAR).repeat(length - VISIBLE_DIGITS);
        String visible = cardNumber.substring(length - VISIBLE_DIGITS);

        return masked + visible;
    }

    /**
     * Masks a card number with formatting (groups of 4).
     * Example: "1234567890123456" -> "**** **** **** 3456"
     *
     * @param cardNumber the full card number to mask
     * @return formatted masked card number
     */
    public static String maskCardNumberFormatted(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "";
        }

        String masked = maskCardNumber(cardNumber);

        // Add spaces every 4 characters for readability
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < masked.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(masked.charAt(i));
        }

        return formatted.toString();
    }

    /**
     * Masks middle digits, showing first and last portions.
     * Example: "1234567890" -> "12****7890"
     *
     * @param cardNumber the full card number
     * @param prefixLength number of digits to show at start
     * @param suffixLength number of digits to show at end
     * @return partially masked card number
     */
    public static String maskCardNumberPartial(String cardNumber,
                                               int prefixLength,
                                               int suffixLength) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "";
        }

        int length = cardNumber.length();
        if (length <= (prefixLength + suffixLength)) {
            return maskCardNumber(cardNumber);
        }

        String prefix = cardNumber.substring(0, prefixLength);
        String suffix = cardNumber.substring(length - suffixLength);
        int maskedLength = length - prefixLength - suffixLength;
        String masked = String.valueOf(MASK_CHAR).repeat(maskedLength);

        return prefix + masked + suffix;
    }
}
