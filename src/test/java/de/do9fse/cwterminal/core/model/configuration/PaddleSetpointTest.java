package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PaddleSetpointTest {
    @Test
    void parsesPercentageIncludingDisabledValue() {
        assertEquals(0.0, PaddleSetpoint.fromProtocol(0).percentage().getValue().doubleValue());
        assertEquals(55.0, PaddleSetpoint.fromProtocol(55).percentage().getValue().doubleValue());
    }

    @Test
    void rejectsOutOfRangePercentage() {
        assertThrows(IllegalArgumentException.class, () -> PaddleSetpoint.fromProtocol(91));
    }

    @Test
    void convertsPercentageToProtocolValue() {
        assertEquals(55, PaddleSetpoint.fromProtocol(55).toProtocolValue());
    }
}
