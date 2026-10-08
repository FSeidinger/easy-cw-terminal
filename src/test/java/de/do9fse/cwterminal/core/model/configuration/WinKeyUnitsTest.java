package de.do9fse.cwterminal.core.model.configuration;

import static javax.measure.MetricPrefix.MILLI;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

class WinKeyUnitsTest {
    @Test
    void convertsDimensionlessQuantitiesToWinKeyUnits() {
        assertEquals(Quantities.getQuantity(20, WinKeyUnits.WPM), WinKeyUnits.asWpm(
            Quantities.getQuantity(20, AbstractUnit.ONE)
        ));
        assertEquals(Quantities.getQuantity(3, WinKeyUnits.RATIO), WinKeyUnits.asRatio(
            Quantities.getQuantity(3, AbstractUnit.ONE)
        ));
        assertEquals(Quantities.getQuantity(50, Units.PERCENT), WinKeyUnits.asPercent(
            Quantities.getQuantity(0.5, AbstractUnit.ONE)
        ));
    }

    @Test
    void convertsTimeQuantitiesToMilliseconds() {
        assertEquals(
            Quantities.getQuantity(1500, MILLI(Units.SECOND)),
            WinKeyUnits.asMilliseconds(Quantities.getQuantity(1.5, Units.SECOND))
        );
    }
}
