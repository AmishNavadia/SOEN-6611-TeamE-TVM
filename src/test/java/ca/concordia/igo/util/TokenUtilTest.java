package ca.concordia.igo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenUtilTest {

    @Test
    void maskCardNumberHandlesNullOrEmpty() {
        assertEquals("", TokenUtil.maskCardNumber(null));
        assertEquals("", TokenUtil.maskCardNumber(""));
    }

    @Test
    void maskCardNumberShowsLastFourDigits() {
        assertEquals("******7890", TokenUtil.maskCardNumber("1234567890"));
    }

    @Test
    void maskCardNumberFormattedAddsSpaces() {
        assertEquals("**** **** **** 3456",
                TokenUtil.maskCardNumberFormatted("1234567890123456"));
    }

    @Test
    void maskCardNumberPartialKeepsPrefixAndSuffix() {
        assertEquals("12****7890",
                TokenUtil.maskCardNumberPartial("1234567890", 2, 4));
    }
}
