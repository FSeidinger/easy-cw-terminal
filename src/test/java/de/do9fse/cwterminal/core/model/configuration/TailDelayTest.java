package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static javax.measure.MetricPrefix.MILLI;

import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;

class TailDelayTest {
    @Test
    void parsesPttTailInTenMillisecondUnits() {
        assertEquals(12.0, TailDelay.fromProtocol(12).delay().to(MILLI(Units.SECOND)).getValue().doubleValue());
        assertEquals(250.0, TailDelay.fromProtocol(250).delay().to(MILLI(Units.SECOND)).getValue().doubleValue());
    }

    @Test
    void encodesDelayAsProtocolValue() {
        assertEquals(12, TailDelay.fromProtocol(12).toProtocolValue());
    }

    @Test
    void rejectsOutOfRangeResponseValue() {
        assertThrows(IllegalArgumentException.class, () -> TailDelay.fromProtocol(251));
    }
}
