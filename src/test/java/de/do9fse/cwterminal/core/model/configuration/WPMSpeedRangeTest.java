package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WPMSpeedRangeTest {
    @Test
    void parsesUnsignedWpmRange() {
        assertEquals(0, WPMSpeedRange.parseResponseByte(0).range());
        assertEquals(30, WPMSpeedRange.parseResponseByte(30).range());
        assertEquals(30, WPMSpeedRange.parseResponseByte((byte) 30).range());
        assertThrows(IllegalArgumentException.class, () -> WPMSpeedRange.parseResponseByte(100));
    }
}
