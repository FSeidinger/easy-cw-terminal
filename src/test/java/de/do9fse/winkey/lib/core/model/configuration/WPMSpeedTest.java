package de.do9fse.winkey.lib.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WPMSpeedTest {
    @Test
    void parsesUnsignedSpeedResponseByte() {
        assertEquals(99.0, WPMSpeed.fromProtocol(99).value().getValue().doubleValue());
        assertEquals(99.0, WPMSpeed.fromProtocol((byte) 99).value().getValue().doubleValue());
    }

    @Test
    void convertsSpeedToProtocolValue() {
        assertEquals(99, WPMSpeed.fromProtocol(99).toProtocolValue());
    }
}
