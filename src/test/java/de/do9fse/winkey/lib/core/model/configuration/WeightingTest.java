package de.do9fse.winkey.lib.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WeightingTest {
    @Test
    void parsesPercentage() {
        assertEquals(50.0, Weighting.fromProtocol(50).value().getValue().doubleValue());
    }

    @Test
    void rejectsValuesOutsideSupportedRange() {
        assertThrows(IllegalArgumentException.class, () -> Weighting.fromProtocol(0));
    }

    @Test
    void convertsPercentageToProtocolValue() {
        assertEquals(50, Weighting.fromProtocol(50).toProtocolValue());
    }
}
