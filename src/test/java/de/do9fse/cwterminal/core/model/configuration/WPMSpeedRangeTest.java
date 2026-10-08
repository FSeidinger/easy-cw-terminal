package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;

class WPMSpeedRangeTest {
    @Test
    void parsesUnsignedWpmRange() {
        assertEquals(0, WPMSpeedRange.fromProtocol(0).range().getValue().intValue());
        assertEquals(30, WPMSpeedRange.fromProtocol(30).range().getValue().intValue());
        assertEquals(30, WPMSpeedRange.fromProtocol((byte) 30).range().getValue().intValue());
        assertThrows(IllegalArgumentException.class, () -> WPMSpeedRange.fromProtocol(100));
    }

    @Test
    void convertsRangeToWpmUnit() {
        final Quantity<Dimensionless> oneWpm = Quantities.getQuantity(1, WinKeyUnits.WPM);

        assertEquals(oneWpm, new WPMSpeedRange(oneWpm).range());
    }

    @Test
    void convertsRangeToProtocolValue() {
        assertEquals(30, WPMSpeedRange.fromProtocol(30).toProtocolValue());
    }
}
