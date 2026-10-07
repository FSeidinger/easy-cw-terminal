package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static javax.measure.MetricPrefix.MILLI;

import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;

class TailDelayTest {
    @Test
    void parsesPttTailInTenMillisecondUnits() {
        assertEquals(12.0, TailDelay.parseResponseByte(12).delay().to(MILLI(Units.SECOND)).getValue().doubleValue());
        assertEquals(250.0, TailDelay.parseResponseByte(250).delay().to(MILLI(Units.SECOND)).getValue().doubleValue());
    }
}
