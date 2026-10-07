package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FarnsworthSpeedTest {
    @Test
    void parsesFarnsworthWpmAndDisabledValue() {
        assertEquals(18.0, FarnsworthSpeed.parseResponseByte(18).wpmSpeed().getValue().doubleValue());
        assertEquals(0.0, FarnsworthSpeed.parseResponseByte(0).wpmSpeed().getValue().doubleValue());
    }

    @Test
    void rejectsUnsupportedWpm() {
        assertThrows(IllegalArgumentException.class, () -> FarnsworthSpeed.parseResponseByte(9));
    }
}
