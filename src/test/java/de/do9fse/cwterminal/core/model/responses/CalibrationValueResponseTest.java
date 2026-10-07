package de.do9fse.cwterminal.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Calibration value response tests")
class CalibrationValueResponseTest {

    @Test
    void parsesResponseByteAsUnsignedValue() {
        final CalibrationValueResponse response = CalibrationValueResponse.parseResponse(new byte[] { (byte) 0xff });

        assertEquals(255, response.value());
    }
}
