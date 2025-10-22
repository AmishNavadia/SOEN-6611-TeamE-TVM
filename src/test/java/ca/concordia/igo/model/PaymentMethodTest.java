package ca.concordia.igo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentMethodTest {

    @Test
    void paymentMethodsExposeDisplayName() {
        assertEquals("Credit Card", PaymentMethod.CREDIT_CARD.getDisplayName());
        assertEquals("Debit Card", PaymentMethod.DEBIT_CARD.getDisplayName());
        assertEquals("Contactless", PaymentMethod.CONTACTLESS.getDisplayName());
        assertEquals("Cash", PaymentMethod.CASH.getDisplayName());
    }
}
