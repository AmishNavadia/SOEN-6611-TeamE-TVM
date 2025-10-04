package ca.concordia.igo.util;

/**
 * Supported languages for the system.
 */
public enum Language {
    EN("English"),
    FR("Français");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

