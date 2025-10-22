package ca.concordia.igo.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class IGoExceptionTest {

    @Test
    void storesMessageAndCause() {
        Throwable cause = new IllegalArgumentException("root");
        IGoException exception = new IGoException("wrapper", cause);

        assertEquals("wrapper", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
