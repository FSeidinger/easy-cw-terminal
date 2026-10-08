package de.do9fse.winkey.lib.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Calibration value response tests")
class CalibrationValueResponseTest {

    @Test
    void parsesResponseByteAsUnsignedValue() {
        final CalibrationValueResponse response = CalibrationValueResponse.fromProtocol(new byte[] { (byte) 0xff });

        assertEquals(255, response.value());
    }
}
