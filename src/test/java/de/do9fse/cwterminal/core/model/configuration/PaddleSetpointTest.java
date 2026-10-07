package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PaddleSetpointTest {
    @Test
    void parsesPercentageIncludingDisabledValue() {
        assertEquals(0.0, PaddleSetpoint.parseResponseByte(0).percentage().getValue().doubleValue());
        assertEquals(55.0, PaddleSetpoint.parseResponseByte(55).percentage().getValue().doubleValue());
    }

    @Test
    void rejectsOutOfRangePercentage() {
        assertThrows(IllegalArgumentException.class, () -> PaddleSetpoint.parseResponseByte(91));
    }
}
