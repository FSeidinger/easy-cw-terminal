package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ModeRegisterTest {
    @Test
    void parsesModeFlagsAndKeyMode() {
        final ModeRegister modeRegister = ModeRegister.parseResponseByte(0xff);

        assertFalse(modeRegister.isPaddleWatchdogEnabled());
        assertTrue(modeRegister.isPaddleEchobackEnabled());
        assertEquals(KeyMode.BUG_MODE, modeRegister.keyMode());
        assertTrue(modeRegister.isPaddleSwapped());
        assertTrue(modeRegister.isSerialEchobackEnabled());
        assertTrue(modeRegister.isAutospaceEnabled());
        assertTrue(modeRegister.isCTSpacingEnabled());
    }

    @Test
    void treatsWatchdogBitAsDisableFlagAndSupportsSignedBytes() {
        assertFalse(ModeRegister.parseResponseByte((byte) 0x80).isPaddleWatchdogEnabled());
        assertTrue(ModeRegister.parseResponseByte(0).isPaddleWatchdogEnabled());
    }
}
