package de.do9fse.cwterminal.core.model.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class WinKeyApplicationExceptionTest {
    @Test
    void storesMessage() {
        final WinKeyApplicationException exception = new WinKeyApplicationException("Application failure");

        assertEquals("Application failure", exception.getMessage());
    }

    @Test
    void storesMessageAndCause() {
        final Throwable cause = new IllegalStateException("Root cause");
        final WinKeyApplicationException exception = new WinKeyApplicationException("Application failure", cause);

        assertEquals("Application failure", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
