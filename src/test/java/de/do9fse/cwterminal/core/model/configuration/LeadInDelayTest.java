package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static javax.measure.MetricPrefix.MILLI;

import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;

class LeadInDelayTest {
    @Test
    void parsesPttLeadInInTenMillisecondUnits() {
        assertEquals(12.0, LeadInDelay.parseResponseByte(12).delay().to(MILLI(Units.SECOND)).getValue().doubleValue());
        assertEquals(250.0, LeadInDelay.parseResponseByte(250).delay().to(MILLI(Units.SECOND)).getValue().doubleValue());
    }

    @Test
    void rejectsOutOfRangeResponseValue() {
        assertThrows(IllegalArgumentException.class, () -> LeadInDelay.parseResponseByte(251));
    }
}
