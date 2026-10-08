package de.do9fse.winkey.lib.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EEPROM dump response tests")
class EEPROMDumpResponseTest {

    @Test
    void parses256ResponseBytes() {
        final byte[] responseBytes = new byte[256];
        responseBytes[0] = 0x12;
        responseBytes[255] = (byte) 0xff;

        final EEPROMDumpResponse response = EEPROMDumpResponse.fromProtocol(responseBytes);

        assertArrayEquals(responseBytes, response.data());
    }

    @Test
    void copiesDataWhenConstructedAndWhenAccessed() {
        final byte[] input = new byte[256];
        final EEPROMDumpResponse response = new EEPROMDumpResponse(input);
        input[0] = 0x01;

        final byte[] data = response.data();
        assertNotSame(input, data);
        assertEquals(0, data[0]);

        data[1] = 0x02;
        assertEquals(0, response.data()[1]);
    }

    @Test
    void rejectsDataWithIncorrectLength() {
        final byte[] responseBytes = new byte[255];

        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> EEPROMDumpResponse.fromProtocol(responseBytes)
        );

        assertEquals(
            "EEPROM dump must contain exactly " + EEPROMDumpResponse.DATA_LENGTH + " bytes but contained 255",
            exception.getMessage()
        );
    }

    @Test
    void rejectsNullData() {
        final NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new EEPROMDumpResponse(null)
        );

        assertEquals("EEPROM dump data must not be null", exception.getMessage());
    }
}
