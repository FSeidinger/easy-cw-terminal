package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WPMSpeedTest {
    @Test
    void parsesUnsignedSpeedResponseByte() {
        assertEquals(99.0, WPMSpeed.parseResponseByte(99).wpmSpeed().getValue().doubleValue());
        assertEquals(99.0, WPMSpeed.parseResponseByte((byte) 99).wpmSpeed().getValue().doubleValue());
    }
}
