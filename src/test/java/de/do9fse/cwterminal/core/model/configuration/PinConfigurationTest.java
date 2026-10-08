package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PinConfigurationTest {
    @Test
    void parsesPinFlagsPriorityAndHangTime() {
        final PinConfiguration pins = PinConfiguration.fromProtocol(0b10011011);

        assertTrue(pins.isPttEnabled());
        assertTrue(pins.isSidetoneEnabled());
        assertFalse(pins.isKeyOutput2Enabled());
        assertTrue(pins.isKeyOutput1Enabled());
        assertEquals(PinConfiguration.UltimaticPriority.DIT, pins.ultimaticPriority());
        assertEquals(PinConfiguration.PaddleHangTime.ONE_AND_ONE_THIRD_LETTERSPACES, pins.paddleHangTime());
        assertEquals(0b10011011, pins.toProtocolValue());
    }
}
