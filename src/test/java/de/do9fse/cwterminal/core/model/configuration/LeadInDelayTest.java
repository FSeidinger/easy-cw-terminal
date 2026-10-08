package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static javax.measure.MetricPrefix.MILLI;

import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;

class LeadInDelayTest {
    @Test
    void parsesPttLeadInInTenMillisecondUnits() {
        assertEquals(12.0, LeadInDelay.fromProtocol(12).value().to(MILLI(Units.SECOND)).getValue().doubleValue());
        assertEquals(250.0, LeadInDelay.fromProtocol(250).value().to(MILLI(Units.SECOND)).getValue().doubleValue());
    }

    @Test
    void rejectsOutOfRangeResponseValue() {
        assertThrows(IllegalArgumentException.class, () -> LeadInDelay.fromProtocol(251));
    }

    @Test
    void encodesDelayAsProtocolValue() {
        assertEquals(12, LeadInDelay.fromProtocol(12).toProtocolValue());
    }
}
