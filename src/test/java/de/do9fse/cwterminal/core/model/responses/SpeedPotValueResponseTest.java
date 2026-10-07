package de.do9fse.cwterminal.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.do9fse.cwterminal.core.model.configuration.WinKeyUnits;
import tech.units.indriya.quantity.Quantities;

class SpeedPotValueResponseTest {
    @Test
    void parsesSpeedPotValueAndIgnoresProtocolTagBits() {
        final SpeedPotValueResponse response = SpeedPotValueResponse.parseResponse(new byte[] { (byte) 0x9f });

        assertEquals(31, response.speedOffset().getValue().intValue());
    }

    @Test
    void rejectsResponseWithoutExpectedTagBits() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpeedPotValueResponse.parseResponse(new byte[] { (byte) 0xbf })
        );
    }

    @Test
    void rejectsSpeedPotValuesOutsideProtocolRange() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new SpeedPotValueResponse(Quantities.getQuantity(32, WinKeyUnits.WPM))
        );
    }
}
