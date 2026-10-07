package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DitDahRatioTest {
    @Test
    void parsesRatioFromProtocolByte() {
        assertEquals(3.0, DitDahRatio.parseResponseByte(50).ratio().getValue().doubleValue());
    }

    @Test
    void rejectsOutOfRangeRatio() {
        assertThrows(IllegalArgumentException.class, () -> DitDahRatio.parseResponseByte(0));
    }
}
