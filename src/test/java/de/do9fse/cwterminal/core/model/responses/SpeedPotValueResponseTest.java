package de.do9fse.cwterminal.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.do9fse.cwterminal.core.model.configuration.WinKeyUnits;
import tech.units.indriya.quantity.Quantities;

class SpeedPotValueResponseTest {
    @Test
    void parsesSpeedPotValueAndIgnoresProtocolTagBits() {
        final SpeedPotValueResponse response = SpeedPotValueResponse.fromProtocol(new byte[] { (byte) 0x9f });

        assertEquals(31, response.speedOffset().getValue().intValue());
    }

    @Test
    void rejectsResponseWithoutExpectedTagBits() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpeedPotValueResponse.fromProtocol(new byte[] { (byte) 0xbf })
        );
    }

    @Test
    void rejectsSpeedPotValuesWithReservedHighValueBitSet() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpeedPotValueResponse.fromProtocol(new byte[] { (byte) 0xa0 })
        );
    }

    @Test
    void rejectsSpeedPotValuesOutsideProtocolRange() {
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SpeedPotValueResponse(Quantities.getQuantity(32, WinKeyUnits.WPM))
        );
        assertEquals(
            "Speed pot value must be between "
                + SpeedPotValueResponse.SPEED_OFFSET_MIN
                + " and "
                + SpeedPotValueResponse.SPEED_OFFSET_MAX
                + " but was 32 WPM",
            exception.getMessage()
        );
    }

    @Test
    void rejectsNonIntegerSpeedPotValues() {
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SpeedPotValueResponse(Quantities.getQuantity(1.5, WinKeyUnits.WPM))
        );

        assertEquals("Speed pot value must be an integer but was 1.5 WPM", exception.getMessage());
    }
}
