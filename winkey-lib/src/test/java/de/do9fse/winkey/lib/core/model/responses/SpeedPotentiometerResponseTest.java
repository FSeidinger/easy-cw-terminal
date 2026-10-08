package de.do9fse.winkey.lib.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Speed potentiometer response tests")
class SpeedPotentiometerResponseTest {

    @Test
    void parsesResponseByteAsUnsignedValue() {
        final SpeedPotentiometerResponse response = SpeedPotentiometerResponse.fromProtocol(
            new byte[] { (byte) 0x80 }
        );

        assertEquals(128, response.value());
    }
}
