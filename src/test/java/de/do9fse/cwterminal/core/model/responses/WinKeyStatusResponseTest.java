package de.do9fse.cwterminal.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WinKeyStatusResponseTest {
    @Test
    void parsesWk1CompatibleStatusFields() {
        final WinKeyStatusResponse response = WinKeyStatusResponse.fromProtocol(new byte[] { (byte) 0xdf });

        assertTrue(response.waiting());
        assertTrue(response.keyDown());
        assertTrue(response.busy());
        assertTrue(response.breakIn());
        assertTrue(response.xoff());
        assertFalse(response.pushButtonStatus());
        assertEquals(0, response.pushButtonMask());
    }

    @Test
    void parsesWk2StatusAndPushButtonFieldsWhenConfigured() {
        final WinKeyStatusResponse statusResponse = WinKeyStatusResponse.fromProtocol(
            new byte[] { (byte) 0xd7 },
            true
        );
        assertTrue(statusResponse.waiting());
        assertFalse(statusResponse.keyDown());
        assertTrue(statusResponse.busy());
        assertTrue(statusResponse.breakIn());
        assertTrue(statusResponse.xoff());
        assertFalse(statusResponse.pushButtonStatus());

        final WinKeyStatusResponse buttonResponse = WinKeyStatusResponse.fromProtocol(
            new byte[] { (byte) 0xdf },
            true
        );
        assertFalse(buttonResponse.keyDown());
        assertFalse(buttonResponse.waiting());
        assertFalse(buttonResponse.busy());
        assertTrue(buttonResponse.pushButtonStatus());
        assertEquals(15, buttonResponse.pushButtonMask());
    }

    @Test
    void rejectsStatusBytesWithoutTheProtocolTag() {
        assertThrows(
            IllegalArgumentException.class,
            () -> WinKeyStatusResponse.fromProtocol(new byte[] { 0x7f })
        );
    }
}
