package ca.concordia.igo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageTest {

    @Test
    void exposesDisplayNames() {
        assertEquals("English", Language.EN.getDisplayName());
        assertEquals("Français", Language.FR.getDisplayName());
    }
}
