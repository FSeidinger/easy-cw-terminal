package de.do9fse.winkey.lib.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;

class WPMSpeedWithResetTest {
    @Test
    void convertsResetAndConfiguredSpeedsToProtocolValues() {
        assertEquals(0, WPMSpeedWithReset.useSpeedPot().toProtocolValue());
        assertEquals(5, new WPMSpeedWithReset(Quantities.getQuantity(5, WinKeyUnits.WPM)).toProtocolValue());
        assertEquals(99, new WPMSpeedWithReset(WPMSpeed.fromProtocol(99)).toProtocolValue());
    }

    @Test
    void rejectsSpeedsOutsideSupportedRange() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new WPMSpeedWithReset(Quantities.getQuantity(4, WinKeyUnits.WPM))
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new WPMSpeedWithReset(Quantities.getQuantity(100, WinKeyUnits.WPM))
        );
    }

    @Test
    void acceptsFractionalSpeedsWithinSupportedRange() {
        assertEquals(
            46,
            new WPMSpeedWithReset(Quantities.getQuantity(45.5, WinKeyUnits.WPM)).toProtocolValue()
        );
    }
}
